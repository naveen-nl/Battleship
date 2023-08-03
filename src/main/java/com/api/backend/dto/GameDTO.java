package com.api.backend.dto;

import java.util.Date;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameDTO {

	private UUID gameId;

	private Long winnerId;

	private String gameStatus;

	private Date gameCreationDate;
	@NotNull
	private Long currentPlayerId;
	@NotNull
	private GamePlayerDTO player1;
	@NotNull
	private GamePlayerDTO player2;

}