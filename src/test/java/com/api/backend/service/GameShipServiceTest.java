package com.api.backend.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
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

import com.api.backend.dto.GameShipDTO;
import com.api.backend.dto.ShipDTO;
import com.api.backend.entity.GameGridEntity;
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

@ExtendWith(MockitoExtension.class)
class GameShipServiceTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private GameShipRepository gameShipRepository;

    @Mock
    private ShipRepository shipRepository;

    @Mock
    private Environment env;

    @InjectMocks
    private GameShipService gameShipService;

    private ShipEntity shipEntity;

    @BeforeEach
    void setUp() {
        shipEntity = new ShipEntity();
        shipEntity.setShipId(1L);
        shipEntity.setShipLength(3);
    }

    @Test
    void testConvertToDTO() {
        GameShipEntity gameShipEntity = new GameShipEntity();
        GameShipDTO expectedDTO = new GameShipDTO();
        when(modelMapper.map(gameShipEntity, GameShipDTO.class)).thenReturn(expectedDTO);

        GameShipDTO actualDTO = gameShipService.convertToDTO(gameShipEntity);

        assertEquals(expectedDTO, actualDTO);
    }

    @Test
    void testConvertToEntity() {
        GameShipDTO gameShipDTO = new GameShipDTO();
        GameShipEntity expectedEntity = new GameShipEntity();
        when(modelMapper.map(gameShipDTO, GameShipEntity.class)).thenReturn(expectedEntity);

        GameShipEntity actualEntity = gameShipService.convertToEntity(gameShipDTO);

        assertEquals(expectedEntity, actualEntity);
    }

    @Test
    void testConvertToShipDTO() {
        ShipDTO expectedDTO = new ShipDTO();
        when(modelMapper.map(shipEntity, ShipDTO.class)).thenReturn(expectedDTO);

        ShipDTO actualDTO = gameShipService.convertToShipDTO(shipEntity);

        assertEquals(expectedDTO, actualDTO);
    }

    @Test
    void testConvertToShipEntity() {
        ShipDTO shipDTO = new ShipDTO();
        when(modelMapper.map(shipDTO, ShipEntity.class)).thenReturn(shipEntity);

        ShipEntity actualEntity = gameShipService.convertToShipEntity(shipDTO);

        assertEquals(shipEntity, actualEntity);
    }

    @Test
    void testGetShips() {
        ShipEntity ship1 = new ShipEntity();
        ship1.setShipId(1L);
        ShipEntity ship2 = new ShipEntity();
        ship2.setShipId(2L);
        when(shipRepository.findAll()).thenReturn(Arrays.asList(ship1, ship2));
        ShipDTO shipDTO1 = new ShipDTO();
        ShipDTO shipDTO2 = new ShipDTO();
        when(modelMapper.map(ship1, ShipDTO.class)).thenReturn(shipDTO1);
        when(modelMapper.map(ship2, ShipDTO.class)).thenReturn(shipDTO2);

        List<ShipDTO> actualShips = gameShipService.getShips();

        assertEquals(2, actualShips.size());
        assertEquals(shipDTO1, actualShips.get(0));
        assertEquals(shipDTO2, actualShips.get(1));
    }

    @Test
    void testGetShipByShipId() {
        when(shipRepository.findById(1L)).thenReturn(java.util.Optional.of(shipEntity));

        ShipEntity actualEntity = gameShipService.getShipByShipId(1L);

        assertEquals(shipEntity, actualEntity);
    }

    @Test
    void testGetShipByShipId_ShouldThrowValidationException() {
        when(shipRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(ValidationException.class, () -> gameShipService.getShipByShipId(1L));
    }

    @Test
    void testValidateShip() {
        Long shipId = 1L;
        int size = 3;
        when(shipRepository.findById(shipId)).thenReturn(java.util.Optional.of(shipEntity));

        gameShipService.validateShip(shipId, size);
    }

    @Test
    void testValidateShip_ShouldThrowValidationException_SizeMismatch() {
        Long shipId = 1L;
        int size = 4;
        when(shipRepository.findById(shipId)).thenReturn(java.util.Optional.of(shipEntity));

        assertThrows(ValidationException.class, () -> gameShipService.validateShip(shipId, size));
    }

    @Test
    void testValidateGameShipGrids_ShouldNotThrowValidationException() {
        GameShipRequest request1 = new GameShipRequest();
        request1.setGameShipLocations(Collections.singletonList(new GameShipLocationRequest(0, 0)));
        GameShipRequest request2 = new GameShipRequest();
        request2.setGameShipLocations(Collections.singletonList(new GameShipLocationRequest(0, 1)));
        List<GameShipRequest> shipsInfo = Arrays.asList(request1, request2);

        assertDoesNotThrow(() -> gameShipService.validateGameShipGrids(shipsInfo));
    }

    @Test
    void testValidateGameShipGrids_ShouldThrowValidationException_Overlapping() {
        GameShipRequest request1 = new GameShipRequest();
        request1.setGameShipLocations(Collections.singletonList(new GameShipLocationRequest(0, 0)));
        GameShipRequest request2 = new GameShipRequest();
        request2.setGameShipLocations(Collections.singletonList(new GameShipLocationRequest(0, 0)));
        List<GameShipRequest> shipsInfo = Arrays.asList(request1, request2);

        assertThrows(ValidationException.class, () -> gameShipService.validateGameShipGrids(shipsInfo));
    }

    @Test
    void testValidateGameShipGrids_ShouldThrowValidationException_NotAdjacent() {
        GameShipRequest request1 = new GameShipRequest();
        request1.setGameShipLocations(Arrays.asList(
            new GameShipLocationRequest(0, 0),
            new GameShipLocationRequest(2, 0)
        ));
        List<GameShipRequest> shipsInfo = Collections.singletonList(request1);

        assertThrows(ValidationException.class, () -> gameShipService.validateGameShipGrids(shipsInfo));
    }

    @Test
    void testAreAdjacent_Horizontal() {
        List<GameShipLocationRequest> locations = Arrays.asList(
            new GameShipLocationRequest(0, 0),
            new GameShipLocationRequest(0, 1),
            new GameShipLocationRequest(0, 2)
        );

        assertTrue(gameShipService.areAdjacent(locations));
    }

    @Test
    void testAreAdjacent_Vertical() {
        List<GameShipLocationRequest> locations = Arrays.asList(
            new GameShipLocationRequest(0, 0),
            new GameShipLocationRequest(1, 0),
            new GameShipLocationRequest(2, 0)
        );

        assertTrue(gameShipService.areAdjacent(locations));
    }

    @Test
    void testAreAdjacent_ShouldReturnFalse() {
        List<GameShipLocationRequest> locations = Arrays.asList(
            new GameShipLocationRequest(0, 0),
            new GameShipLocationRequest(2, 0),
            new GameShipLocationRequest(3, 0)
        );

        assertFalse(gameShipService.areAdjacent(locations));
    }

    @Test
    void testAreShipLocationsOverlapping_ShouldNotOverlap() {
        GameShipRequest request1 = new GameShipRequest();
        request1.setGameShipLocations(Collections.singletonList(new GameShipLocationRequest(0, 0)));
        GameShipRequest request2 = new GameShipRequest();
        request2.setGameShipLocations(Collections.singletonList(new GameShipLocationRequest(1, 0)));
        List<GameShipRequest> shipsInfo = Arrays.asList(request1, request2);

        assertFalse(gameShipService.areShipLocationsOverlapping(shipsInfo));
    }

    @Test
    void testAreShipLocationsOverlapping_ShouldOverlap() {
        GameShipRequest request1 = new GameShipRequest();
        request1.setGameShipLocations(Collections.singletonList(new GameShipLocationRequest(0, 0)));
        GameShipRequest request2 = new GameShipRequest();
        request2.setGameShipLocations(Collections.singletonList(new GameShipLocationRequest(0, 0)));
        List<GameShipRequest> shipsInfo = Arrays.asList(request1, request2);

        assertTrue(gameShipService.areShipLocationsOverlapping(shipsInfo));
    }

    @Test
    void testMapToGameShipEntity() {
        Long shipId = 1L;
        GamePlayerEntity gamePlayer = new GamePlayerEntity();
        when(shipRepository.findById(shipId)).thenReturn(java.util.Optional.of(shipEntity));

        GameShipEntity gameShipEntity = gameShipService.mapToGameShipEntity(shipId, gamePlayer);

        assertEquals(shipEntity, gameShipEntity.getShip());
        assertEquals(ShipStatus.NOT_SUNK, gameShipEntity.getShipStatus());
        assertEquals(gamePlayer, gameShipEntity.getGamePlayer());
    }

    @Test
    void testUpdateGameShipStatus_ShipIsNull() {
        GamePlayerEntity currentPlayer = new GamePlayerEntity();
        currentPlayer.setGameShips(new ArrayList<>());
        gameShipService.updateGameShipStatus(null, currentPlayer);

        verify(gameShipRepository, never()).save(any());
        assertEquals(0, currentPlayer.getGameShips().size());
    }

    @Test
    void testUpdateGameShipStatus_ShipIsNotSunk() {
    	GamePlayerEntity currentPlayer = new GamePlayerEntity();
        GameShipEntity gameShipEntity = new GameShipEntity();
        gameShipEntity.setShip(new ShipEntity());
        List<GameGridEntity> grids=new ArrayList<>();
        GameGridEntity gameGrid=new GameGridEntity();
        gameGrid.setGridValue(GridValue.NOT_ATTACKED);
        grids.add(gameGrid);
        gameShipEntity.setGameShipGrids(grids);
        currentPlayer.addGameShip(gameShipEntity);

        gameShipService.updateGameShipStatus(gameShipEntity, currentPlayer);

        verify(gameShipRepository, never()).save(any());
        assertEquals(1, currentPlayer.getGameShips().size());
    }

    @Test
    void testUpdateGameShipStatus_ShipIsSunk() {
        GamePlayerEntity currentPlayer = new GamePlayerEntity();
        GameShipEntity gameShipEntity = new GameShipEntity();
        gameShipEntity.setShip(new ShipEntity());
        List<GameGridEntity> grids=new ArrayList<>();
        GameGridEntity gameGrid=new GameGridEntity();
        gameGrid.setGridValue(GridValue.ATTACKED);
        grids.add(gameGrid);
        gameShipEntity.setGameShipGrids(grids);
        currentPlayer.addGameShip(gameShipEntity);

        gameShipService.updateGameShipStatus(gameShipEntity, currentPlayer);

        verify(gameShipRepository, times(1)).save(gameShipEntity);
        assertEquals(ShipStatus.SUNK, gameShipEntity.getShipStatus());
    }
}
