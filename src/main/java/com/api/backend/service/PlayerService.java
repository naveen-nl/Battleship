package com.api.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.api.backend.dto.PlayerDTO;
import com.api.backend.entity.PlayerEntity;
import com.api.backend.exception.InvalidPlayerDataException;
import com.api.backend.exception.ValidationException;
import com.api.backend.repository.PlayerRepository;
import com.api.backend.request.PlayerInputRequest;

/**
 * The Class PlayerService handles operations related to players.
 */
@Service
public class PlayerService {

	private static final Logger logger = LogManager.getLogger(PlayerService.class);

	private final ModelMapper modelMapper;

	private PlayerRepository playerRepository;

	private Environment env;

	/**
	 * Instantiates a new player service.
	 *
	 * @param modelMapper      the model mapper
	 * @param playerRepository the player repository
	 * @param env              the environment
	 */
	@Autowired
	public PlayerService(ModelMapper modelMapper, PlayerRepository playerRepository, Environment env) {
		this.modelMapper = modelMapper;
		this.playerRepository = playerRepository;
		this.env = env;
	}

	/**
	 * Convert to player DTO.
	 *
	 * @param playerEntity the player entity
	 * @return the player DTO
	 */
	public PlayerDTO convertToDTO(PlayerEntity playerEntity) {
		return modelMapper.map(playerEntity, PlayerDTO.class);
	}

	/**
	 * Convert to player entity.
	 *
	 * @param playerDTO the player DTO
	 * @return the player entity
	 */
	public PlayerEntity convertToEntity(PlayerDTO playerDTO) {
		return modelMapper.map(playerDTO, PlayerEntity.class);
	}

	/**
	 * Fetch all players who are not locked.
	 *
	 * @return the list of players who are not locked by game.
	 */
	@Cacheable("players")
	public List<PlayerEntity> fetchAllPlayersWhoAreNotLocked() {
		return playerRepository.findAllByIsLocked(false);
	}

	/**
	 * Creates a new player or lock the player for game
	 *
	 * @param playerInputRequest the player input request
	 * @return the created player DTO
	 */
	@Transactional
	public PlayerDTO createPlayer(PlayerInputRequest playerInputRequest) {
		if (!StringUtils.hasText(playerInputRequest.getPlayerName())) {
			throw new InvalidPlayerDataException(env.getProperty("PlayerService.PLAYER_NAME_IS_EMPTY"));
		}
		PlayerEntity playerEntity = fetchPlayerByPlayerName(playerInputRequest.getPlayerName());

		if (playerEntity != null) {
			if (playerEntity.getIsPlaying()) {
				throw new InvalidPlayerDataException(env.getProperty("PlayerService.PLAYER_ALREADY_INGAME"));
			} else if (playerEntity.getIsLocked()) {
				throw new InvalidPlayerDataException(env.getProperty("PlayerService.PLAYER_ALREADY_JOINEDGAME"));
			}
			playerEntity.setIsLocked(true);
		} else {

			playerEntity = new PlayerEntity();
			playerEntity.setPlayerName(playerInputRequest.getPlayerName());
			playerEntity.setIsLocked(true);
			playerEntity.setIsPlaying(false);
		}

		return convertToDTO(playerRepository.save(playerEntity));
	}

	/**
	 * Fetch player by player name.
	 *
	 * @param playerName the player name
	 * @return the player entity
	 */
	public PlayerEntity fetchPlayerByPlayerName(String playerName) {
		return playerRepository.findByplayerName(playerName);
	}

	/**
	 * Fetch all available players who are not locked.
	 *
	 * @return the list of available player DTOs
	 */
	public List<PlayerDTO> fetchAllAvailablePlayers() {
		List<PlayerEntity> availablePlayerEntities = fetchAllPlayersWhoAreNotLocked();
		if (!availablePlayerEntities.isEmpty()) {
			return availablePlayerEntities.stream().map(this::convertToDTO).collect(Collectors.toList());
		} else {
			throw new InvalidPlayerDataException(env.getProperty("PlayerService.NO_PLAYERS"));
		}
	}

	/**
	 * Validate player by checking if the player exists and is locked.
	 *
	 * @param playerName the player name
	 * @return the player entity
	 * @throws ValidationException if the player does not exist or is not locked
	 */
	public PlayerEntity validatePlayer(String playerName) {
		PlayerEntity playerEntity = fetchPlayerByPlayerName(playerName);
		if (playerEntity == null) {
			throw new ValidationException("Player " + playerName + " does not exists");
		} else if (!playerEntity.getIsLocked()) {
			throw new ValidationException("Player " + playerName + " did not joined the game");
		} else if (playerEntity.getIsPlaying()) {
			throw new ValidationException("Player " + playerName + " is already playing the game");
		}
		return playerEntity;
	}
}
