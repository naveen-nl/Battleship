package com.api.backend.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameShipRequest {

	private Long shipId;

	private List<GameShipLocationRequest> gameShipLocations;

}