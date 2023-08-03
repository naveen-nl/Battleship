package com.api.backend.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GamePlayResponse {
	private String gameId;
	private Long gameWinnerId;
	private GameShipResponse gameShipHitOrSunked;

}