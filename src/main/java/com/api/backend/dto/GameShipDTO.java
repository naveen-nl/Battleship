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
public class GameShipDTO {

	private Long gameShipId;

	private ShipDTO ship;
	
	private String shipStatus;
	
	private List<GameGridDTO> gameShipGrids;

}