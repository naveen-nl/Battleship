package com.api.backend.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.api.backend.dto.GameShipDTO;
import com.api.backend.dto.ShipDTO;
import com.api.backend.entity.GamePlayerEntity;
import com.api.backend.entity.GameShipEntity;
import com.api.backend.entity.GridValue;
import com.api.backend.entity.ShipEntity;
import com.api.backend.entity.ShipStatus;
import com.api.backend.exception.ValidationException;
import com.api.backend.repository.GameShipRepository;
import com.api.backend.repository.ShipRepository;
import com.api.backend.request.GameShipLocationRequest;
import com.api.backend.request.GameShipRequest;

/**
 * The Class GameShipService handles operations related to the game ships.
 */
@Service
public class GameShipService {
	
	private static final Logger logger = LogManager.getLogger(GameShipService.class);

	private final ModelMapper modelMapper;
	
	private GameShipRepository gameShipRepository;
	
	private ShipRepository shipRepository;
	
	private Environment env;

	/**
	 * Instantiates a new game ship service.
	 *
	 * @param modelMapper the model mapper
	 * @param gameShipRepository the game ship repository
	 * @param shipRepository the ship repository
	 * @param env the env
	 */
	@Autowired
	public GameShipService(ModelMapper modelMapper, GameShipRepository gameShipRepository,
			ShipRepository shipRepository, Environment env) {
		super();
		this.modelMapper = modelMapper;
		this.gameShipRepository = gameShipRepository;
		this.shipRepository = shipRepository;
		this.env = env;
	}

	/**
	 * Converts a GameShipEntity to a GameShipDTO.
	 *
	 * @param gameShipEntity the game ship entity to convert
	 * @return the corresponding game ship DTO
	 */
	public GameShipDTO convertToDTO(GameShipEntity gameShipEnitity) {
		return modelMapper.map(gameShipEnitity, GameShipDTO.class);
	}

	/**
	 * Converts a GameShipDTO to a GameShipEntity.
	 *
	 * @param gameShipDTO the game ship DTO to convert
	 * @return the corresponding game ship entity
	 */
	public GameShipEntity convertToEntity(GameShipDTO gameShipDTO) {
		return modelMapper.map(gameShipDTO, GameShipEntity.class);
	}

	/**
	 * Converts a ShipEntity to a ShipDTO.
	 *
	 * @param shipEntity the ship entity to convert
	 * @return the corresponding ship DTO
	 */
	public ShipDTO convertToShipDTO(ShipEntity shipEnitity) {
		return modelMapper.map(shipEnitity, ShipDTO.class);
	}

	/**
	 * Converts a ShipDTO to a ShipEntity.
	 *
	 * @param shipDTO the ship DTO to convert
	 * @return the corresponding ship entity
	 */
	public ShipEntity convertToShipEntity(ShipDTO shipDTO) {
		return modelMapper.map(shipDTO, ShipEntity.class);
	}

	/**
	 * Retrieves a list of all ships as ShipDTO objects.This method is cached with key ships.
	 *
	 * @return the list of all ships
	 */
	@Cacheable("ships")
	public List<ShipDTO> getShips() {
		return shipRepository.findAll().stream().map(this::convertToShipDTO).collect(Collectors.toList());
	}

	/**
	 * Retrieves a ship entity by its ship ID.
	 *
	 * @param shipId the ship ID to look up
	 * @return the corresponding ship entity
	 * @throws ValidationException if the ship ID does not exist
	 */
	@Cacheable("ships")
	public ShipEntity getShipByShipId(Long shipId) {
		return shipRepository.findById(shipId)
				.orElseThrow(() -> new ValidationException("Ship Id " + shipId + "does not exists"));
	}

	/**
	 * Validates whether a given ship ID matches the expected size.
	 *
	 * @param shipId the ship ID to validate
	 * @param size the expected size of the ship
	 * @throws ValidationException if the ship ID is not provided or its length does not match the expected size
	 */
	public void validateShip(Long shipId, int size) {
		if (0 == size) {
			throw new ValidationException(env.getProperty("GameShipService.SHIP_ID_NOT_PROVIDED") + shipId);
		}
		ShipEntity shipEntity = getShipByShipId(shipId);
		if (size != shipEntity.getShipLength()) {
			throw new ValidationException("Provided Ship Id " + shipId + " length does not match ");
		}
	}

	/**
	 * Validates the grids of the game ships to ensure they are not overlapping or not adjacent.
	 *
	 * @param shipsInfo the list of game ship requests to validate
	 * @throws ValidationException if the ship grids are overlapping or not adjacent
	 */
	public void validateGameShipGrids(List<GameShipRequest> shipsInfo) {

		if (areShipLocationsOverlapping(shipsInfo)) {
			logger.debug("inside if loop: cells are over lapping");
			throw new ValidationException(env.getProperty("GameShipService.SHIPS_OVERLAPPING"));
		}
		if (areShipLocationsNotAdjacent(shipsInfo)) {
			logger.debug("inside if loop: cells are not adjacent");
			throw new ValidationException(env.getProperty("GameShipService.SHIPS_NOT_ADJACENT"));
		}
	}

	/**
	 * Checks if the grid locations of ships are not adjacent.
	 *
	 * @param shipsInfo the list of game ship requests to check
	 * @return true if ship locations are not adjacent, false otherwise
	 */
	public Boolean areShipLocationsNotAdjacent(List<GameShipRequest> shipsInfo) {

		for (GameShipRequest gameShipRequest : shipsInfo) {
			if (!areAdjacent(gameShipRequest.getGameShipLocations())) {
				logger.debug("inside if loop: cells are not adjacent");
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the grid locations of ships are adjacent.
	 *
	 * @param locations the list of grid locations to check
	 * @return true if ship locations are adjacent, false otherwise
	 */
	public Boolean areAdjacent(List<GameShipLocationRequest> locations) {
		boolean isHorizontal = true;
		boolean isVertical = true;

		int prevRow = -1;
		int prevCol = -1;
		for (GameShipLocationRequest location : locations) {
			int row = location.getRowIndex();
			int col = location.getColumnIndex();
			logger.debug("Inside for loop : prevrow " + prevRow + " prevColum " + prevCol);
			logger.debug("Inside for loop : row " + row + " Colum " + col);
			if (prevRow != -1 && prevCol != -1) {
				// Check if the locations are adjacent horizontally
				if (row != prevRow || Math.abs(col - prevCol) > 1) {
					logger.debug("inside horizontal if condition");
					isHorizontal = false;
				}
				// Check if the locations are adjacent vertically
				if (col != prevCol || Math.abs(row - prevRow) > 1) {
					logger.debug("inside vertical if condition");
					isVertical = false;
				}
			}
			prevRow = row;
			prevCol = col;
		}
		logger.debug("END : areNotAdjacent final result :" + (isHorizontal || isVertical) + " isHorizontal : "
				+ isHorizontal + " isvertical :  " + isVertical);
		return isHorizontal || isVertical;
	}

	/**
	 * Checks if the grid locations of ships are overlapping.
	 *
	 * @param shipsInfo the list of game ship requests to check
	 * @return true if ship locations are overlapping, false otherwise
	 */
	public Boolean areShipLocationsOverlapping(List<GameShipRequest> shipsInfo) {
		Set<String> visitedGridCells = new HashSet<>();
		for (GameShipRequest gameShipRequest : shipsInfo) {
			for (GameShipLocationRequest locationRequest : gameShipRequest.getGameShipLocations()) {
				String gridCell = locationRequest.getRowIndex() + "-" + locationRequest.getColumnIndex();
				logger.debug("grid cell values " + gridCell);
				if (visitedGridCells.contains(gridCell)) {
					logger.debug("Inside if loop for overlapping cells " + gridCell);
					return true;
				}
				visitedGridCells.add(gridCell);
			}
		}
		return false;
	}

	/**
	 * Map all validated params to game ship entity.
	 *
	 * @param shipId the ship id
	 * @param gamePlayer the game player
	 * @return the game ship entity
	 */
	public GameShipEntity mapToGameShipEntity(Long shipId, GamePlayerEntity gamePlayer) {
		GameShipEntity gameShipEntity = new GameShipEntity();
		try {
			gameShipEntity.setShip(getShipByShipId(shipId));
			gameShipEntity.setShipStatus(ShipStatus.NOT_SUNK);
			gameShipEntity.setGamePlayer(gamePlayer);
			// gameShipRepository.save(gameShipEntity);
			gamePlayer.addGameShip(gameShipEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gameShipEntity;
	}

	/**
	 * Update game ship status of the opponent player
	 *
	 * @param gameShipEntity the game ship entity
	 * @param opponentPlayer the opponent player
	 */
	public void updateGameShipStatus(GameShipEntity gameShipEntity, GamePlayerEntity opponentPlayer) {

		if (gameShipEntity != null) {
			if (gameShipEntity.getGameShipGrids().stream()
					.allMatch(grid -> grid.getGridValue() == GridValue.ATTACKED)) {

				gameShipEntity.setShipStatus(ShipStatus.SUNK);
				gameShipRepository.save(gameShipEntity);
				opponentPlayer.addGameShip(gameShipEntity);
			}
		}
	}
}
