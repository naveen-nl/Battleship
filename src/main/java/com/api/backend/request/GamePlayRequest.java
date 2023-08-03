package com.api.backend.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GamePlayRequest {

	private String gameId;
	private Long currentPlayer;
	private GameShipLocationRequest attackedGrid;
}