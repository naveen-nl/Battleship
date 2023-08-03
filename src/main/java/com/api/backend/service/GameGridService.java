package com.api.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.api.backend.dto.GameGridDTO;
import com.api.backend.entity.GameGridEntity;
import com.api.backend.entity.GamePlayerEntity;
import com.api.backend.entity.GameShipEntity;
import com.api.backend.entity.GridValue;
import com.api.backend.exception.ValidationException;
import com.api.backend.repository.GameGridRepository;
import com.api.backend.request.GameCreationRequest;
import com.api.backend.request.GameShipLocationRequest;
import com.api.backend.request.GameShipRequest;
import com.api.backend.utils.Constants;

/**
 * Service class that provides various methods to manage the game grid for
 * battleship games.
 */
@Service
public class GameGridService {

	private static final Logger logger = LogManager.getLogger(GameGridService.class);

	private final ModelMapper modelMapper;
	private GameGridRepository gameGridRepository;
	private Environment env;

	@Autowired
	public GameGridService(ModelMapper modelMapper, GameGridRepository gameGridRepository, Environment env) {
		this.modelMapper = modelMapper;
		this.gameGridRepository = gameGridRepository;
		this.env = env;
	}

	/**
	 * Converts a {@link GameGridEntity} object to a {@link GameGridDTO} object.
	 *
	 * @param gameGridEntity The {@link GameGridEntity} object to convert.
	 * @return The corresponding {@link GameGridDTO} object.
	 */
	public GameGridDTO convertToDTO(GameGridEntity gameGridEntity) {
		return modelMapper.map(gameGridEntity, GameGridDTO.class);
	}

	/**
	 * Converts a {@link GameGridDTO} object to a {@link GameGridEntity} object.
	 *
	 * @param gameGridDTO The {@link GameGridDTO} object to convert.
	 * @return The corresponding {@link GameGridEntity} object.
	 */
	public GameGridEntity convertToEntity(GameGridDTO gameGridDTO) {
		return modelMapper.map(gameGridDTO, GameGridEntity.class);
	}


	

	/**
	 * Populates the game board with game grid entities based on the given game
	 * creation request, list of game ships, and game player.
	 *
	 * @param gameCreationRequest The game creation request containing ship
	 *                            information.
	 * @param gameShips           The list of game ships to be placed on the game
	 *                            board.
	 * @param gamePlayer          The game player for whom to populate the game
	 *                            board.
	 * @return The list of populated game grid entities.
	 */
	public List<GameGridEntity> populateGameBoard(GameCreationRequest gameCreationRequest,
			List<GameShipEntity> gameShips, GamePlayerEntity gamePlayer) {
		List<GameGridEntity> gameGrids = new ArrayList<>();
		int gridSize = Constants.GRID_VALUE;
		for (int row = 0; row < gridSize; row++) {
			for (int col = 0; col < gridSize; col++) {
				GameGridEntity gameGridEntity = new GameGridEntity();
				gameGridEntity.setRowIndex(row);
				gameGridEntity.setColumnIndex(col);
				gameGridEntity.setGridValue(GridValue.NOT_ATTACKED);
				gameGridEntity.setGamePlayer(gamePlayer);
				logger.debug("populateGameBoard Method : Inside for loop : " + row + col);
				// Find and associate the GameShipEntity if it exists at this grid location
				for (GameShipRequest gameShipRequest : gameCreationRequest.getShipsInfo()) {
					for (GameShipLocationRequest locationRequest : gameShipRequest.getGameShipLocations()) {
						if (row == locationRequest.getRowIndex() && col == locationRequest.getColumnIndex()) {
							// Find the GameShipEntity by shipId and associate it with the GameGridEntity
							GameShipEntity gameShipEntityOptional = gamePlayer.getGameShips().stream()
									.filter(gameShipEntity -> gameShipEntity.getShip().getShipId()
											.equals(gameShipRequest.getShipId()))
									.findFirst().orElse(null);
							if (null != gameShipEntityOptional) {
								gameGridEntity.setGameShip(gameShipEntityOptional);
								gameShipEntityOptional.addGameShipGrid(gameGridEntity);
							}
							logger.debug("populateGameBoard Method : Inside IF loop : " + row + col);
							break;
						}
					}
				}
				gamePlayer.addGameBoard(gameGridEntity);
			}
		}
		return gameGrids;
	}

	/**
	 * Validates the given game grid entity and throws a {@link ValidationException}
	 * if it is null or if its grid value is already attacked.
	 *
	 * @param gameGridEntity The game grid entity to validate.
	 * @throws ValidationException If the game grid entity is null or its grid value
	 *                             is already attacked.
	 */
	public void validateGameBoardCoordinate(GameGridEntity gameGridEntity) {
		Optional.ofNullable(gameGridEntity)
				.orElseThrow(() -> new ValidationException(env.getProperty("GameGridService.COORDINATES_NOT_EXISTS")));

		Optional.of(gameGridEntity.getGridValue()).filter(value -> value != GridValue.ATTACKED)
				.orElseThrow(() -> new ValidationException(env.getProperty("GameGridService.ALREADY_ATTACKED")));
	}

	/**
	 * Validates and saves the grid for the attacked location of the Opponent by the current player.
	 *
	 * @param attackedGrid  The attacked grid location.
	 * @param opponentPlayer The opponent player entity.
	 * @return The associated game ship entity (if any) for the attacked location.
	 * @throws ValidationException If the attacked grid location is invalid or
	 *                             already attacked.
	 */
	public GameShipEntity validateAndSaveGrid(GameShipLocationRequest attackedGrid, GamePlayerEntity opponentPlayer) {
		List<GameGridEntity> gameBoard = opponentPlayer.getGameBoard();
		GameGridEntity gameGridEntity = gameBoard.stream()
				.filter(gameGrid -> gameGrid.getRowIndex() == attackedGrid.getRowIndex()
						&& gameGrid.getColumnIndex() == attackedGrid.getColumnIndex()
						&& gameGrid.getGamePlayer().equals(opponentPlayer))
				.findFirst().orElse(null);

		validateGameBoardCoordinate(gameGridEntity);

		updateGameBoardCoordinate(opponentPlayer, gameGridEntity);

		return gameGridEntity.getGameShip();
	}

	/**
	 * Updates the game board coordinate by setting the grid value to attacked,
	 * saves the entity, and adds it to the opponent player's game board.
	 *
	 * @param opponentPlayer  The opponent player entity.
	 * @param gameGridEntity The game grid entity to update.
	 */
	public void updateGameBoardCoordinate(GamePlayerEntity opponentPlayer, GameGridEntity gameGridEntity) {
		gameGridEntity.setGridValue(GridValue.ATTACKED);
		gameGridRepository.save(gameGridEntity);
		opponentPlayer.addGameBoard(gameGridEntity);
	}
}
