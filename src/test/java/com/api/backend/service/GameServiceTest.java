package com.api.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import com.api.backend.dto.GameDTO;
import com.api.backend.entity.*;
import com.api.backend.exception.BattleshipApplicationException;
import com.api.backend.exception.ValidationException;
import com.api.backend.repository.GamePlayerRepository;
import com.api.backend.repository.GameRepository;
import com.api.backend.request.*;
import com.api.backend.response.GamePlayResponse;
import com.api.backend.response.GameShipResponse;

import java.util.Arrays;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class GameServiceTest {

	@Mock
	private ModelMapper modelMapper;

	@Mock
	private GameRepository gameRepository;

	@Mock
	private GameGridService gameGridService;

	@Mock
	private GamePlayerRepository gamePlayerRepository;

	@Mock
	private GameShipService gameShipService;

	@Mock
	private PlayerService playerService;

	@Mock
	private Environment env;

	@InjectMocks
	private GameService gameService;

	private UUID gameId;
	private GameEntity gameEntity;
	private GamePlayerEntity player1;
	private GamePlayerEntity player2;
	private List<GameShipRequest> gameShipRequests;
	private GameShipLocationRequest gameShipLocationRequest;

	@BeforeEach
	public void setUp() {
		gameId = UUID.randomUUID();
		gameEntity = new GameEntity();
		gameEntity.setGameId(gameId);

		player1 = new GamePlayerEntity();
		player1.setGamePlayerId(1L);
		player1.setPlayer(new PlayerEntity());
		player1.setGameShips(new ArrayList<>());
		player1.setGameBoard(new ArrayList<>());

		player2 = new GamePlayerEntity();
		player2.setGamePlayerId(2L);
		player2.setPlayer(new PlayerEntity());
		player2.setGameShips(new ArrayList<>());
		player2.setGameBoard(new ArrayList<>());

		gameEntity.setPlayer1(player1);
		gameEntity.setPlayer2(player2);
		gameEntity.setCurrentPlayer(player1);
		gameEntity.setGameStatus(GameStatus.ONGOING);

		gameShipRequests = new ArrayList<>();

		// Adding a sample GameShipLocationRequest to the test setup
		gameShipLocationRequest = new GameShipLocationRequest();
		gameShipLocationRequest.setRowIndex(0);
		gameShipLocationRequest.setColumnIndex(0);

		// Create a new ModelMapper instance and set the matching strategy
		modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
	}

	@Test
	public void testFetchGameDetails_InvalidGameId_NullGameEntity() {

		when(gameRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

		assertThrows(BattleshipApplicationException.class,
				() -> gameService.fetchGameDetails(UUID.randomUUID().toString()));
	}

	@Test
	public void testFetchGameDetails_InvalidGameIdFormat_NullGameEntity() {

		assertThrows(ValidationException.class, () -> gameService.fetchGameDetails("invalid-game-id-format"));
	}

	@Test
	public void testCreateGame_DuplicatePlayerNames() {

		List<GameCreationRequest> gameCreationRequests = Arrays.asList(
				new GameCreationRequest("Player1", Collections.emptyList()),
				new GameCreationRequest("Player1", Collections.emptyList()));

		when(playerService.validatePlayer("Player1")).thenReturn(new PlayerEntity());

		assertThrows(ValidationException.class, () -> gameService.createGame(gameCreationRequests));
	}

	@Test
	public void testPlayerTurn_InvalidGameId_NullGameEntity() {

		UUID invalidGameId = UUID.randomUUID();
		GamePlayRequest request = new GamePlayRequest(invalidGameId.toString(), player1.getGamePlayerId(),
				gameShipLocationRequest);

		when(gameRepository.findById(invalidGameId)).thenReturn(Optional.empty());

		assertThrows(ValidationException.class, () -> gameService.playerTurn(request));
	}

	@Test
	public void testPlayerTurn_InvalidCurrentPlayerId() {

		Long invalidPlayerId = 1L;
		GamePlayRequest request = new GamePlayRequest(gameId.toString(), invalidPlayerId, gameShipLocationRequest);

		when(gameRepository.findById(gameId)).thenReturn(Optional.of(gameEntity));
		when(gamePlayerRepository.findById(invalidPlayerId)).thenReturn(Optional.empty());

		assertThrows(ValidationException.class, () -> gameService.playerTurn(request));
	}

	@Test
	public void testPlayerTurn_GameOver() {

		Long invalidPlayerId = 1L;

		GamePlayRequest request = new GamePlayRequest(gameId.toString(), invalidPlayerId, gameShipLocationRequest);

		when(gameRepository.findById(gameId)).thenReturn(Optional.of(gameEntity));
		when(gamePlayerRepository.findById(player1.getGamePlayerId())).thenReturn(Optional.of(player1));

		GamePlayResponse result = gameService.playerTurn(request);

		assertNotNull(result);
		assertEquals(GameStatus.COMPLETED, gameEntity.getGameStatus());
		assertNotNull(gameEntity.getWinner());
		assertFalse(gameEntity.getPlayer1().getPlayer().getIsPlaying());
		assertFalse(gameEntity.getPlayer2().getPlayer().getIsPlaying());
		assertFalse(gameEntity.getPlayer1().getPlayer().getIsLocked());
		assertFalse(gameEntity.getPlayer2().getPlayer().getIsLocked());
	}
}
