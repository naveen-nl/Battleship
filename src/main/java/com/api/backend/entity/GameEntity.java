
package com.api.backend.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

@Data
@Entity
@ToString(exclude = {"player1", "player2", "currentPlayer", "winner"})
@Table(name = "Game")
public class GameEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(columnDefinition = "BINARY(16)")
	private UUID gameId;

	@OneToOne
	@JoinColumn(name = "player1_id")
	private GamePlayerEntity player1;

	@OneToOne
	@JoinColumn(name = "player2_id")
	private GamePlayerEntity player2;

	@OneToOne
	@JoinColumn(name = "currentPlayer_Id")
	private GamePlayerEntity currentPlayer;

	@OneToOne
	@JoinColumn(name = "winner_Id")
	private GamePlayerEntity winner;

	@Enumerated(EnumType.STRING)
	private GameStatus gameStatus;

	private Date gameCreationDate = new Date();

	public GamePlayerEntity getOpponent() {
		if (player1.equals(currentPlayer)) {
			return player2;
		} else if (player2.equals(currentPlayer)) {
			return player1;
		}
		return null;
	}
}
