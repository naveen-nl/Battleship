package com.api.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.backend.dto.GameDTO;
import com.api.backend.entity.GameEntity;
import com.api.backend.entity.GameGridEntity;
import com.api.backend.entity.GamePlayerEntity;
import com.api.backend.entity.GameShipEntity;
import com.api.backend.entity.GameStatus;
import com.api.backend.entity.PlayerEntity;
import com.api.backend.entity.ShipStatus;
import com.api.backend.exception.BattleshipApplicationException;
import com.api.backend.exception.ValidationException;
import com.api.backend.repository.GamePlayerRepository;
import com.api.backend.repository.GameRepository;
import com.api.backend.request.GameCreationRequest;
import com.api.backend.request.GameShipRequest;
import com.api.backend.response.GamePlayResponse;
import com.api.backend.response.GameShipResponse;
import com.api.backend.request.GamePlayRequest;

/**
 * The GameService class provides operations and business logic related to the
 * game.
 */

@Service
public class GameService {

	private static final Logger logger = LogManager.getLogger(GameService.class);

	private final ModelMapper modelMapper;

	private GameRepository gameRepository;

	private GameGridService gameGridService;

	private GamePlayerRepository gamePlayerRepository;

	private GameShipService gameShipService;

	private PlayerService playerService;

	private Environment env;

	/**
	 * Instantiates a new game service.
	 *
	 * @param modelMapper          the model mapper
	 * @param gameRepository       the game repository
	 * @param gameGridService      the game grid service
	 * @param gamePlayerRepository the game player repository
	 * @param gameShipService      the game ship service
	 * @param env                  the env
	 * @param playerService        the player service
	 */
	@Autowired
	public GameService(ModelMapper modelMapper, GameRepository gameRepository, GameGridService gameGridService,
			GamePlayerRepository gamePlayerRepository, GameShipService gameShipService, Environment env,
			PlayerService playerService) {
		this.modelMapper = createModelMapper();
		this.gameRepository = gameRepository;
		this.gameGridService = gameGridService;
		this.gameShipService = gameShipService;
		this.gamePlayerRepository = gamePlayerRepository;
		this.playerService = playerService;
		this.env = env;
		configureModelMapper();
	}

	/**
	 * Convert the GameEntity to a GameDTO.
	 *
	 * @param gameEntity the game entity
	 * @return the corresponding game DTO
	 */
	public GameDTO convertToGameDTO(GameEntity gameEntity) {
		return modelMapper.map(gameEntity, GameDTO.class);
	}

	/**
	 * Convert the GameEntity to a GameDTO.
	 *
	 * @param gameEntity the game entity
	 * @return the corresponding game DTO
	 */
	public GameEntity convertToGameEntity(GameDTO gameDTO) {
		return modelMapper.map(gameDTO, GameEntity.class);
	}

	/**
	 * Configure the ModelMapper with appropriate mapping strategies for winner and
	 * current player.
	 */
	private void configureModelMapper() {
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		modelMapper.typeMap(GameEntity.class, GameDTO.class)
				.addMapping(src -> src.getCurrentPlayer().getGamePlayerId(), GameDTO::setCurrentPlayerId)
				.addMapping(src -> src.getWinner() != null ? src.getWinner().getGamePlayerId() : null,
						GameDTO::setWinnerId);
	}
	 private ModelMapper createModelMapper() {
	        ModelMapper modelMapper = new ModelMapper();
	        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
	        return modelMapper;
	    }
	/**
	 * Fetch game details by gameId.This method is cached for a particular gameId.
	 *
	 * @param gameId the game id
	 * @return the game DTO containing game details
	 */
	@Cacheable("games")
	public GameDTO fetchGameDetails(String gameId) {
		GameEntity game = fetchGameByGameId(gameId);
		if (null == game) {
			throw new BattleshipApplicationException(env.getProperty("GameService.ID_NOT_EXISTS"));
		} else {
			return convertToGameDTO(game);
		}
	}

	/**
	 * Fetch game details by gameId.
	 *
	 * @param gameId the game id
	 * @return the game DTO containing game details
	 */
	public GameEntity fetchGameByGameId(String gameId) {
		GameEntity gameInfo = null;
		try {
			UUID gameUuid = UUID.fromString(gameId);
			gameInfo = gameRepository.findById(gameUuid).orElse(null);
		} catch (Exception e) {
			throw new ValidationException(env.getProperty("GameService.INVALID_GAME_ID"));
		}

		return gameInfo;
	}

	/**
	 * Fetch game player details by game player Id.
	 *
	 * @param gameId the game id
	 * @return the game DTO containing game details
	 */
	public GamePlayerEntity fetchGamePlayerByGamePlayerId(GamePlayRequest request) {
		return gamePlayerRepository.findById(request.getCurrentPlayer()).orElse(null);
	}

	/**
	 * Creates a new game based on the provided information.
	 *
	 * @param gameCreationRequests the game creation requests containing player and
	 *                             their respective ship information
	 * @return the gameId of the newly created game
	 */
	@Transactional
	public String createGame(List<GameCreationRequest> gameCreationRequests) {

		if (2 != gameCreationRequests.size()) {
			throw new ValidationException(env.getProperty("GameService.INVALID_PLAYER_COUNT"));
		}
		List<GamePlayerEntity> gamePlayers = new ArrayList<>();
		for (GameCreationRequest gameCreationRequest : gameCreationRequests) {

			// validate the players and identify whether
			PlayerEntity player = playerService.validatePlayer(gameCreationRequest.getPlayerName());
			// Populate Game player
			GamePlayerEntity gamePlayer = populateGamePlayer(player);

			gamePlayers.add(gamePlayer);
			// Player Gameships
			List<GameShipEntity> gameShips = validateAndPopulateGameShip(gameCreationRequest.getShipsInfo(),
					gamePlayer);
			// player gameboard
			List<GameGridEntity> gameBoard = populateGameBoard(gameCreationRequest, gameShips, gamePlayer);
			player.setIsPlaying(true);
			gamePlayerRepository.save(gamePlayer);
		}
		// Populate game object and store in db
		GameEntity gameEntity = populateGame(gamePlayers);
		if(gameEntity.getGameId()!=null) {
		return gameEntity.getGameId().toString();
		}
		else {
			throw new ValidationException("Unable to create game due to invalid input");
		}
	}

	/**
	 * Populate game player object with player details
	 *
	 * @param player the player
	 * @return the game player entity
	 */
	private GamePlayerEntity populateGamePlayer(PlayerEntity player) {
		GamePlayerEntity gamePlayer = new GamePlayerEntity();
		gamePlayer.setPlayer(player);
		gamePlayer.setGameShips(new ArrayList<>());
		gamePlayer.setGameBoard(new ArrayList<>());
		return gamePlayer;
	}

	/**
	 * Validate and populate game ship.
	 *
	 * @param shipsInfo  the ships info
	 * @param gamePlayer the game player
	 * @return the list
	 */
	private List<GameShipEntity> validateAndPopulateGameShip(List<GameShipRequest> shipsInfo,
			GamePlayerEntity gamePlayer) {
		List<GameShipEntity> gameShips = new ArrayList<>();
		gameShipService.validateGameShipGrids(shipsInfo);
		for (GameShipRequest gameShipRequest : shipsInfo) {
			gameShipService.validateShip(gameShipRequest.getShipId(), gameShipRequest.getGameShipLocations().size());
			GameShipEntity gameShipEntity = gameShipService.mapToGameShipEntity(gameShipRequest.getShipId(),
					gamePlayer);
			gameShips.add(gameShipEntity);
		}
		return gameShips;
	}

	/**
	 * Populate game board for the game player
	 *
	 * @param gameCreationRequest the game creation request
	 * @param gameShips           the game ships
	 * @param gamePlayer          the game player
	 * @return the list
	 */
	private List<GameGridEntity> populateGameBoard(GameCreationRequest gameCreationRequest,
			List<GameShipEntity> gameShips, GamePlayerEntity gamePlayer) {
		List<GameGridEntity> gameBoard = gameGridService.populateGameBoard(gameCreationRequest, gameShips, gamePlayer);
		return gameBoard;
	}

	/**
	 * Populate game object with game player details.
	 *
	 * @param gamePlayers the game players
	 * @return the game entity
	 */
	private GameEntity populateGame(List<GamePlayerEntity> gamePlayers) {
		GameEntity gameEntity = new GameEntity();
		gameEntity.setPlayer1(gamePlayers.get(0));
		gameEntity.setPlayer2(gamePlayers.get(1));
		gameEntity.setCurrentPlayer(gamePlayers.get(0));
		gameEntity.setGameStatus(GameStatus.ONGOING);
		gameRepository.save(gameEntity);
		return gameEntity;
	}

	/**
	 * Performs a player turn during the game and updates game and player status.
	 *
	 * @param request the request containing game and player information for the
	 *                turn
	 * @return the game play response with the result of the turn
	 */
	@Transactional
	@CacheEvict(value = "games", key = "#request.getGameId()")
	public GamePlayResponse playerTurn(GamePlayRequest request) {
		GameEntity gameEntity = fetchGameByGameId(request.getGameId());
		if (gameEntity == null) {
			throw new ValidationException(env.getProperty("GameService.INVALID_GAME_ID"));
		}

		GamePlayerEntity currentPlayer = fetchGamePlayerByGamePlayerId(request);
		if (currentPlayer == null) {
			throw new ValidationException(env.getProperty("GameService.PLAYER_NOT_FOUND"));
		}

		if (!gameEntity.getCurrentPlayer().equals(currentPlayer)) {
			throw new ValidationException(env.getProperty("GameService.PLAYER_TURN-OVER"));
		}
		GamePlayerEntity OpponentPlayer = gameEntity.getOpponent();
		// saving the gameboard grid status
		GameShipEntity opponentGameShipEntity = gameGridService.validateAndSaveGrid(request.getAttackedGrid(),
				OpponentPlayer);
		// save the game ship entity
		gameShipService.updateGameShipStatus(opponentGameShipEntity, OpponentPlayer);
		// save game player details
		gamePlayerRepository.save(OpponentPlayer);

		// check whether game is completed
		if (isGameOver(OpponentPlayer)) {
			// setting winner and game status
			gameEntity.setWinner(currentPlayer);
			gameEntity.setGameStatus(GameStatus.COMPLETED);
			gameEntity.getPlayer1().getPlayer().setIsPlaying(false);
			gameEntity.getPlayer2().getPlayer().setIsPlaying(false);
			gameEntity.getPlayer1().getPlayer().setIsLocked(false);
			gameEntity.getPlayer2().getPlayer().setIsLocked(false);
		}
		// saving the game
		gameEntity.setCurrentPlayer(OpponentPlayer);
		gameRepository.save(gameEntity);

		return convertToGamePlayResponse(gameEntity, opponentGameShipEntity);
	}

	/**
	 * Convert to game play response.
	 *
	 * @param gameEntity     the game entity
	 * @param gameShipEntity the game ship entity
	 * @return the game play response
	 */
	private GamePlayResponse convertToGamePlayResponse(GameEntity gameEntity, GameShipEntity gameShipEntity) {
		GamePlayResponse response = new GamePlayResponse();
		response.setGameId(gameEntity.getGameId().toString());
		if (gameEntity.getWinner() != null) {
			response.setGameWinnerId(gameEntity.getWinner().getGamePlayerId());
		} else {
			response.setGameWinnerId(null);
		}
		if (gameShipEntity != null && gameShipEntity.getShip()!=null) {
			GameShipResponse gameShipResponse = GameShipResponse.builder()
					.gameShipName(gameShipEntity.getShip().getShipName())
					.gameShipStatus(gameShipEntity.getShipStatus().toString()).build();
			response.setGameShipHitOrSunked(gameShipResponse);
		} else {
			response.setGameShipHitOrSunked(null);
		}
		return response;
	}

	/**
	 * Checks whether this player turn resulted in game over.
	 *
	 * @param gamePlayerEntity the game player entity
	 * @return the boolean
	 */
	private Boolean isGameOver(GamePlayerEntity gamePlayerEntity) {

		if (gamePlayerEntity.getGameShips().stream()
				.allMatch(gameShip -> gameShip.getShipStatus() == ShipStatus.SUNK)) {

			return true;
		}
		return false;
	}
}
