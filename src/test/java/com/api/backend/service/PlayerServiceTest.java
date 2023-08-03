package com.api.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import com.api.backend.dto.PlayerDTO;
import com.api.backend.entity.PlayerEntity;
import com.api.backend.exception.InvalidPlayerDataException;
import com.api.backend.exception.ValidationException;
import com.api.backend.repository.PlayerRepository;
import com.api.backend.request.PlayerInputRequest;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class PlayerServiceTest {
	@Mock
	private PlayerRepository playerRepository;

	@Mock
	private ModelMapper modelMapper;

	@Mock
	private Environment environment;

	@InjectMocks
	private PlayerService playerService;

	@Test
	void testConvertToDTO() {
		// Create a player entity
		PlayerEntity playerEntity = new PlayerEntity();
		playerEntity.setPlayerId(1L);
		playerEntity.setPlayerName("JohnDoe");

		// Mock modelMapper.map()
		when(modelMapper.map(playerEntity, PlayerDTO.class)).thenReturn(new PlayerDTO());

		PlayerDTO playerDTO = playerService.convertToDTO(playerEntity);

		// Verify modelMapper.map() is called
		verify(modelMapper, times(1)).map(playerEntity, PlayerDTO.class);
	}

	@Test
	void testConvertToEntity() {

		PlayerDTO playerDTO = new PlayerDTO();
		playerDTO.setPlayerId(1L);
		playerDTO.setPlayerName("JohnDoe");

		when(modelMapper.map(playerDTO, PlayerEntity.class)).thenReturn(new PlayerEntity());

		PlayerEntity playerEntity = playerService.convertToEntity(playerDTO);

		verify(modelMapper, times(1)).map(playerDTO, PlayerEntity.class);
	}

	@Test
	void testFetchAllPlayersWhoAreNotLocked() {

		List<PlayerEntity> unlockedPlayers = new ArrayList<>();
		unlockedPlayers.add(new PlayerEntity());
		unlockedPlayers.add(new PlayerEntity());

		// Mock playerRepository.findAllByIsLocked()
		when(playerRepository.findAllByIsLocked(false)).thenReturn(unlockedPlayers);

		// Call the method
		List<PlayerEntity> result = playerService.fetchAllPlayersWhoAreNotLocked();

		// Verify playerRepository.findAllByIsLocked() is called
		verify(playerRepository, times(1)).findAllByIsLocked(false);
		// Verify the result
		assertEquals(unlockedPlayers, result);
	}

	@Test
	void testCreatePlayerWithValidData() {
		PlayerInputRequest playerInputRequest = new PlayerInputRequest();
		playerInputRequest.setPlayerName("John1");

		// Mock fetchPlayerByPlayerName() to return null (player does not exist)
		when(playerRepository.findByplayerName("John1")).thenReturn(null);

		// Mock the save method of playerRepository to return a new PlayerEntity
		when(playerRepository.save(any(PlayerEntity.class))).thenAnswer(invocation -> {
			PlayerEntity playerEntityArg = invocation.getArgument(0);
			playerEntityArg.setPlayerId(1L);
			return playerEntityArg;
		});

		PlayerDTO result = playerService.createPlayer(playerInputRequest);

		verify(playerRepository, times(1)).findByplayerName("John1");

		verify(playerRepository, times(1)).save(any(PlayerEntity.class));

	}

	@Test
	void testCreatePlayerWithEmptyName() {
		PlayerInputRequest playerInputRequest = new PlayerInputRequest();

		assertThrows(InvalidPlayerDataException.class, () -> playerService.createPlayer(playerInputRequest));
	}

	@Test
	void testCreatePlayerWithExistingPlayerAlreadyInGame() {
		PlayerInputRequest playerInputRequest = new PlayerInputRequest();
		playerInputRequest.setPlayerName("JohnDoe");
		PlayerEntity existingPlayer = new PlayerEntity();
		existingPlayer.setPlayerName("JohnDoe");
		existingPlayer.setIsPlaying(true);

		when(playerService.fetchPlayerByPlayerName("JohnDoe")).thenReturn(existingPlayer);

		assertThrows(InvalidPlayerDataException.class, () -> playerService.createPlayer(playerInputRequest));
	}

	@Test
	void testCreatePlayerWithExistingPlayerAlreadyJoinedGame() {
		PlayerInputRequest playerInputRequest = new PlayerInputRequest();
		playerInputRequest.setPlayerName("JohnDoe");

		PlayerEntity existingPlayer = new PlayerEntity();
		existingPlayer.setPlayerName("JohnDoe");
		existingPlayer.setIsPlaying(false);
		existingPlayer.setIsLocked(true);

		when(playerService.fetchPlayerByPlayerName("JohnDoe")).thenReturn(existingPlayer);

		assertThrows(InvalidPlayerDataException.class, () -> playerService.createPlayer(playerInputRequest));
	}

	@Test
	void testFetchPlayerByPlayerName() {
		PlayerEntity playerEntity = new PlayerEntity();
		playerEntity.setPlayerName("JohnDoe");

		when(playerRepository.findByplayerName("JohnDoe")).thenReturn(playerEntity);
		PlayerEntity result = playerService.fetchPlayerByPlayerName("JohnDoe");

		verify(playerRepository, times(1)).findByplayerName("JohnDoe");
		assertNotNull(result);
		assertEquals("JohnDoe", result.getPlayerName());
	}

	@Test
	void testFetchAllAvailablePlayersWithAvailablePlayers() {
		List<PlayerEntity> unlockedPlayers = new ArrayList<>();
		unlockedPlayers.add(new PlayerEntity());
		unlockedPlayers.add(new PlayerEntity());
		when(playerRepository.findAllByIsLocked(false)).thenReturn(unlockedPlayers);

		List<PlayerDTO> result = playerService.fetchAllAvailablePlayers();

		verify(playerRepository, times(1)).findAllByIsLocked(false);

		assertNotNull(result);
		assertEquals(unlockedPlayers.size(), result.size());
	}

	@Test
	void testFetchAllAvailablePlayersWithNoAvailablePlayers() {
		when(playerService.fetchAllPlayersWhoAreNotLocked()).thenReturn(new ArrayList<>());
		assertThrows(InvalidPlayerDataException.class, () -> playerService.fetchAllAvailablePlayers());
	}

	@Test
	void testValidatePlayerWithValidPlayer() {
		PlayerEntity playerEntity = new PlayerEntity();
		playerEntity.setPlayerName("John1");
		playerEntity.setIsLocked(true);
		playerEntity.setIsPlaying(false);
		when(playerService.fetchPlayerByPlayerName("John1")).thenReturn(playerEntity);
		PlayerEntity result = playerService.validatePlayer("John1");
		assertNotNull(result);
		assertEquals("John1", result.getPlayerName());
	}

	@Test
	void testValidatePlayerWithNonExistingPlayer() {
		when(playerService.fetchPlayerByPlayerName("JohnDoe")).thenReturn(null);
		assertThrows(ValidationException.class, () -> playerService.validatePlayer("JohnDoe"));
	}

	@Test
	void testValidatePlayerWithUnlockedPlayer() {
		PlayerEntity playerEntity = new PlayerEntity();
		playerEntity.setPlayerName("JohnDoe");
		playerEntity.setIsLocked(false);
		when(playerService.fetchPlayerByPlayerName("JohnDoe")).thenReturn(playerEntity);
		assertThrows(ValidationException.class, () -> playerService.validatePlayer("JohnDoe"));
	}

	@Test
	void testValidatePlayerWithPlayerAlreadyPlaying() {
		PlayerEntity playerEntity = new PlayerEntity();
		playerEntity.setPlayerName("JohnDoe");
		playerEntity.setIsLocked(true);
		playerEntity.setIsPlaying(true);
		when(playerService.fetchPlayerByPlayerName("JohnDoe")).thenReturn(playerEntity);
		assertThrows(ValidationException.class, () -> playerService.validatePlayer("JohnDoe"));
	}
}
