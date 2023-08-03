package com.api.backend.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;

import com.api.backend.dto.GameGridDTO;
import com.api.backend.entity.GameGridEntity;
import com.api.backend.entity.GamePlayerEntity;
import com.api.backend.entity.GameShipEntity;
import com.api.backend.entity.GridValue;
import com.api.backend.entity.ShipEntity;
import com.api.backend.exception.ValidationException;
import com.api.backend.repository.GameGridRepository;
import com.api.backend.request.GameCreationRequest;
import com.api.backend.request.GameShipLocationRequest;
import com.api.backend.request.GameShipRequest;

@ExtendWith(MockitoExtension.class)
class GameGridServiceTest {

	@Mock
	private ModelMapper modelMapper;

	@Mock
	private GameGridRepository gameGridRepository;

	@Mock
	private Environment environment;

	@InjectMocks
	private GameGridService gameGridService;

	private GamePlayerEntity gamePlayer;
	private GameCreationRequest gameCreationRequest;
	private List<GameShipEntity> gameShips;
	private List<GameShipRequest> gameShipRequests;
	List<GameShipLocationRequest> gameShipLocations;

	@BeforeEach
	void setUp() {
		gamePlayer = new GamePlayerEntity();
		gamePlayer.setGameBoard(new ArrayList<>());
		gamePlayer.setGameShips(new ArrayList<>());

		gameCreationRequest = new GameCreationRequest();
		gameShipLocations = new ArrayList<>();
		gameShips = new ArrayList<>();
		gameShipRequests = new ArrayList<>();
		gameCreationRequest.setShipsInfo(gameShipRequests);
	}

	@Test
	void testConvertToDTO() {

		GameGridEntity gameGridEntity = new GameGridEntity();
		gameGridEntity.setRowIndex(1);
		gameGridEntity.setColumnIndex(2);
		gameGridEntity.setGridValue(GridValue.NOT_ATTACKED);

		GameGridDTO gameGridDTO = new GameGridDTO();
		gameGridDTO.setRowIndex(1);
		gameGridDTO.setColumnIndex(2);
		gameGridDTO.setGridValue(GridValue.NOT_ATTACKED.toString());

		when(modelMapper.map(gameGridEntity, GameGridDTO.class)).thenReturn(gameGridDTO);

		GameGridDTO result = gameGridService.convertToDTO(gameGridEntity);

		assertEquals(gameGridDTO, result);
	}

	@Test
	void testConvertToEntity() {

		GameGridDTO gameGridDTO = new GameGridDTO();
		gameGridDTO.setRowIndex(1);
		gameGridDTO.setColumnIndex(2);
		gameGridDTO.setGridValue(GridValue.NOT_ATTACKED.toString());

		GameGridEntity gameGridEntity = new GameGridEntity();
		gameGridEntity.setRowIndex(1);
		gameGridEntity.setColumnIndex(2);
		gameGridEntity.setGridValue(GridValue.NOT_ATTACKED);

		when(modelMapper.map(gameGridDTO, GameGridEntity.class)).thenReturn(gameGridEntity);

		GameGridEntity result = gameGridService.convertToEntity(gameGridDTO);

		assertEquals(gameGridEntity, result);
	}

	@Test
	void testPopulateGameBoard() {

		GameCreationRequest gameCreationRequest = mock(GameCreationRequest.class);

		GamePlayerEntity gamePlayer = mock(GamePlayerEntity.class);

		int gridSize = 10;

		// Mock the behavior of the gameCreationRequest.getShipsInfo() method
		List<GameShipRequest> gameShipRequests = new ArrayList<>();

		// Mock a GameShipRequest with a ship of length 5 at (0, 0)
		GameShipRequest gameShipRequest = new GameShipRequest();
		gameShipRequest.setShipId(1L);
		List<GameShipLocationRequest> gameShipLocations = new ArrayList<>();

		// Populate the game ship locations based on the ship's length
		for (int i = 0; i < 5; i++) {
			GameShipLocationRequest locationRequest = new GameShipLocationRequest();
			locationRequest.setRowIndex(i);
			locationRequest.setColumnIndex(0);
			gameShipLocations.add(locationRequest);
		}

		gameShipRequest.setGameShipLocations(gameShipLocations);
		gameShipRequests.add(gameShipRequest);

		when(gameCreationRequest.getShipsInfo()).thenReturn(gameShipRequests);
		gameCreationRequest.setShipsInfo(gameShipRequests);
		ShipEntity se = new ShipEntity();
		se.setShipId(1L);
		se.setShipLength(5);
		se.setShipName("Carrier");
		// Mock the behavior of the gamePlayer.getGameShips() method
		GameShipEntity gameShipEntity = mock(GameShipEntity.class);
		when(gameShipEntity.getShip()).thenReturn(se);
		gameShipEntity.setShip(se);

		List<GameShipEntity> gameShipsList = new ArrayList<>();
		gameShipsList.add(gameShipEntity);
		when(gamePlayer.getGameShips()).thenReturn(gameShipsList);

		List<GameGridEntity> gameGrids = gameGridService.populateGameBoard(gameCreationRequest, gameShipsList,
				gamePlayer);

		// Verify that the GameShipEntity's addGameShipGrid method is called when a ship
		// is associated with a GameGridEntity
		verify(gameShipEntity, times(5)).addGameShipGrid(any(GameGridEntity.class));
	}

	@Test
	void testValidateAndSaveGrid_ValidCoordinate() {

		GameGridEntity gameGridEntity = new GameGridEntity();
		gameGridEntity.setRowIndex(1);
		gameGridEntity.setColumnIndex(2);
		gameGridEntity.setGridValue(GridValue.NOT_ATTACKED);
		gameGridEntity.setGamePlayer(gamePlayer);

		GameShipLocationRequest attackedGrid = new GameShipLocationRequest();
		attackedGrid.setRowIndex(1);
		attackedGrid.setColumnIndex(2);

		gamePlayer.getGameBoard().add(gameGridEntity);

		GameShipEntity result = gameGridService.validateAndSaveGrid(attackedGrid, gamePlayer);

		assertEquals(GridValue.ATTACKED, gameGridEntity.getGridValue());
		assertEquals(gameGridEntity.getGameShip(), result);
	}

	@Test
	void testValidateAndSaveGrid_InvalidCoordinate() {

		GameGridEntity gameGridEntity = new GameGridEntity();
		gameGridEntity.setRowIndex(1);
		gameGridEntity.setColumnIndex(2);
		gameGridEntity.setGridValue(GridValue.ATTACKED);
		gameGridEntity.setGamePlayer(gamePlayer);

		GameShipLocationRequest attackedGrid = new GameShipLocationRequest();
		attackedGrid.setRowIndex(1);
		attackedGrid.setColumnIndex(2);

		gamePlayer.getGameBoard().add(gameGridEntity);

		assertThrows(ValidationException.class, () -> gameGridService.validateAndSaveGrid(attackedGrid, gamePlayer));
	}

	@Test
	void testUpdateGameBoardCoordinate() {

		GameGridEntity gameGridEntity = new GameGridEntity();
		gameGridEntity.setRowIndex(1);
		gameGridEntity.setColumnIndex(2);
		gameGridEntity.setGridValue(GridValue.NOT_ATTACKED);
		gameGridEntity.setGamePlayer(gamePlayer);
		// Create a mock of GamePlayerEntity
		GamePlayerEntity gamePlayer = mock(GamePlayerEntity.class);

		when(gameGridRepository.save(any(GameGridEntity.class))).thenReturn(gameGridEntity);

		gameGridService.updateGameBoardCoordinate(gamePlayer, gameGridEntity);

		// Verify that the game grid entity is saved and added to the game player
		verify(gameGridRepository).save(gameGridEntity);
		verify(gamePlayer).addGameBoard(gameGridEntity);

		assertEquals(GridValue.ATTACKED, gameGridEntity.getGridValue());
	}

	@Test
	void testValidateGameBoardCoordinate_Valid() {

		GameGridEntity gameGridEntity = new GameGridEntity();
		gameGridEntity.setRowIndex(1);
		gameGridEntity.setColumnIndex(2);
		gameGridEntity.setGridValue(GridValue.NOT_ATTACKED);

		assertDoesNotThrow(() -> gameGridService.validateGameBoardCoordinate(gameGridEntity));

	}

	@Test
	void testValidateGameBoardCoordinate_Invalid() {

		GameGridEntity gameGridEntity = new GameGridEntity();
		gameGridEntity.setRowIndex(1);
		gameGridEntity.setColumnIndex(2);
		gameGridEntity.setGridValue(GridValue.ATTACKED);

		assertThrows(ValidationException.class, () -> gameGridService.validateGameBoardCoordinate(gameGridEntity));
	}
}
