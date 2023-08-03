package com.api.backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GamePlayerDTO {

	private Long gamePlayerId;

	private PlayerDTO player;

	private List<GameShipDTO> gameShips;

	private List<GameGridDTO> gameBoard;

}