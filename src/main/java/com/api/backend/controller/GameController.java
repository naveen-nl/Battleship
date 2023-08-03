package com.api.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * The Class GameController handles HTTP requests and responses related to the game functionalities.
 */
@RestController
@OpenAPIDefinition(info = @io.swagger.v3.oas.annotations.info.Info(title = "Battleship APIs", version = "1.0.0"))
@RequestMapping("/game/v1")
public class GameController {

	private final GameService gameService;
	
	private final GameShipService gameShipService;
	
	private final PlayerService playerService;

	/**
	 * Instantiates a new Game Controller with the necessary services.
	 *
	 * @param gameService the game service to handle game-related operations
	 * @param gameShipService the game ship service to manage ships
	 * @param playerService the player service to handle player-related operations
	 */
	@Autowired
	public GameController(GameService gameService, GameShipService gameShipService, PlayerService playerService) {
		this.gameService = gameService;
		this.gameShipService = gameShipService;
		this.playerService = playerService;
	}

	/**
	 * API endpoint to create a new player who joins the game.
	 *
	 * @param playerDTO the player DTO containing player details
	 * @return the response entity with the created player's details
	 */
	@PostMapping("/joingame")
	@Operation(summary = "API used to create player in the backend who has joined the game")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Player created successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid player details are shared") })
	public ResponseEntity<PlayerDTO> joinGame(@RequestBody @Validated PlayerInputRequest playerDTO) {

		return ResponseEntity.ok(playerService.createPlayer(playerDTO));

	}

	/**
	 * API endpoint to get a list of available players who are ready to play the game.
	 *
	 * @return the response entity containing the list of available players
	 */
	@GetMapping("/availableplayers")
	@Operation(summary = "API used to get available players who are ready to play the game")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "List of players retrieved successfully") })
	public ResponseEntity<List<PlayerDTO>> getAvailablePlayers() {

		return ResponseEntity.ok(playerService.fetchAllAvailablePlayers());

	}

	/**
	 * API endpoint to create a new game with two players.
	 *
	 * @param requests the list of game creation requests containing player details
	 * @return the response entity with the status message of the game creation process
	 */
	@PostMapping("/creategame")
	@Operation(summary = "API used to create a new game with two players")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Game created successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid game creation request") })
	public ResponseEntity<String> createGame(@RequestBody List<GameCreationRequest> requests) {

		String response = gameService.createGame(requests);
		return ResponseEntity.ok(response);

	}

	/**
	 * API endpoint to retrieve game information for a given game ID.
	 *
	 * @param gameId the game ID to fetch game details
	 * @return the response entity containing the game details
	 */
	@GetMapping("/{gameId}")
	@Operation(summary = "API used to retrieve game information")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Game information retrieved successfully") })
	public ResponseEntity<GameDTO> fetchGameDetails(@PathVariable String gameId) {

		return ResponseEntity.ok(gameService.fetchGameDetails(gameId));

	}

	/**
	 * API endpoint to perform a game play turn based on the provided game play request.
	 *
	 * @param request the game play request containing player and grid details
	 * @return the response entity containing the game play response
	 */
	@PostMapping("/gameplay")
	@Operation(summary = "API used to perform a game play turn")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Game play turn executed successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid game play request") })
	public ResponseEntity<GamePlayResponse> gamePlay(@RequestBody @Validated GamePlayRequest request) {

		GamePlayResponse response;

		response = gameService.playerTurn(request);
		return ResponseEntity.ok(response);

	}

	/**
	 * API endpoint to get the list of available ships.
	 *
	 * @return the response entity containing the list of available ships
	 */
	@GetMapping("/ships")
	@Operation(summary = "API used to get the list of available ships")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "List of ships retrieved successfully") })
	public ResponseEntity<List<ShipDTO>> getShips() {

		List<ShipDTO> ships = gameShipService.getShips();
		return ResponseEntity.ok(ships);

	}

}
