package com.api.backend.response;

import com.api.backend.dto.GamePlayerDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameCreationResponse {

	private String gameId;
	private GamePlayerDTO player1;
	private GamePlayerDTO player2;
}