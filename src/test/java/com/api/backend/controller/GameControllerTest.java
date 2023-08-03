package com.api.backend.controller;


import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.api.backend.dto.GameDTO;
import com.api.backend.dto.PlayerDTO;
import com.api.backend.dto.ShipDTO;
import com.api.backend.request.GameCreationRequest;
import com.api.backend.request.GamePlayRequest;
import com.api.backend.request.PlayerInputRequest;
import com.api.backend.response.GamePlayResponse;
import com.api.backend.service.GameService;
import com.api.backend.service.GameShipService;
import com.api.backend.service.PlayerService;

public class GameControllerTest {

    private GameController gameController;
    private GameService gameService;
    private GameShipService gameShipService;
    private PlayerService playerService;

    @BeforeEach
    public void setUp() {
        gameService = mock(GameService.class);
        gameShipService = mock(GameShipService.class);
        playerService = mock(PlayerService.class);
        gameController = new GameController(gameService, gameShipService, playerService);
    }

    @Test
    public void testJoinGame() {
        PlayerInputRequest inputRequest = new PlayerInputRequest();
        PlayerDTO playerDTO = new PlayerDTO();
        when(playerService.createPlayer(any())).thenReturn(playerDTO);

        ResponseEntity<PlayerDTO> response = gameController.joinGame(inputRequest);

        verify(playerService, times(1)).createPlayer(any());
        assertSame(HttpStatus.OK, response.getStatusCode());
        assertSame(playerDTO, response.getBody());
    }

    @Test
    public void testGetAvailablePlayers() {
        List<PlayerDTO> playerList = new ArrayList<>();
        when(playerService.fetchAllAvailablePlayers()).thenReturn(playerList);

        ResponseEntity<List<PlayerDTO>> response = gameController.getAvailablePlayers();

        verify(playerService, times(1)).fetchAllAvailablePlayers();
        assertSame(HttpStatus.OK, response.getStatusCode());
        assertSame(playerList, response.getBody());
    }

    @Test
    public void testCreateGame() {
        List<GameCreationRequest> requests = new ArrayList<>();
        when(gameService.createGame(anyList())).thenReturn("Game created successfully");

        ResponseEntity<String> response = gameController.createGame(requests);

        verify(gameService, times(1)).createGame(anyList());
        assertSame(HttpStatus.OK, response.getStatusCode());
        assertSame("Game created successfully", response.getBody());
    }

    @Test
    public void testFetchGameDetails() {
        String gameId = "123";
        GameDTO gameDTO = new GameDTO();
        when(gameService.fetchGameDetails(eq(gameId))).thenReturn(gameDTO);

        ResponseEntity<GameDTO> response = gameController.fetchGameDetails(gameId);

        verify(gameService, times(1)).fetchGameDetails(eq(gameId));
        assertSame(HttpStatus.OK, response.getStatusCode());
        assertSame(gameDTO, response.getBody());
    }

    @Test
    public void testGamePlay() {
        GamePlayRequest request = new GamePlayRequest();
        GamePlayResponse gamePlayResponse = new GamePlayResponse();
        when(gameService.playerTurn(any())).thenReturn(gamePlayResponse);

        ResponseEntity<GamePlayResponse> response = gameController.gamePlay(request);

        verify(gameService, times(1)).playerTurn(any());
        assertSame(HttpStatus.OK, response.getStatusCode());
        assertSame(gamePlayResponse, response.getBody());
    }

    @Test
    public void testGetShips() {
        List<ShipDTO> shipList = new ArrayList<>();
        when(gameShipService.getShips()).thenReturn(shipList);

        ResponseEntity<List<ShipDTO>> response = gameController.getShips();

        verify(gameShipService, times(1)).getShips();
        assertSame(HttpStatus.OK, response.getStatusCode());
        assertSame(shipList, response.getBody());
    }
}
