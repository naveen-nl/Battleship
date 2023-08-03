
package com.api.backend.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.ToString;

@Data
@Entity
@ToString(exclude = {"gameShips", "gameBoard"})
@Table(name = "GamePlayer")
public class GamePlayerEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long gamePlayerId;

	@ManyToOne
	@JoinColumn(name = "playerId", nullable = false)
	private PlayerEntity player;

	@OneToMany(mappedBy = "gamePlayer", cascade = CascadeType.ALL)
	@JsonIgnoreProperties("gamePlayer")
	private List<GameShipEntity> gameShips;

	@OneToMany(mappedBy = "gamePlayer", cascade = CascadeType.ALL)
	@JsonIgnoreProperties("gamePlayer")
	private List<GameGridEntity> gameBoard;

	// helpers for bidirectional relationships
	public void addGameShip(GameShipEntity gameShip) {
		if (gameShips == null) {
			gameShips = new ArrayList<>();
		}
		gameShips.add(gameShip);
		gameShip.setGamePlayer(this);
	}

	public void removeGameShip(GameShipEntity gameShip) {
		gameShips.remove(gameShip);
		gameShip.setGamePlayer(this);
	}

	public void addGameBoard(GameGridEntity gameGrid) {
		if (gameBoard == null) {
			gameBoard = new ArrayList<>();
		}
		gameBoard.add(gameGrid);
		gameGrid.setGamePlayer(this);
	}

	public void removeGameBoard(GameGridEntity gameGrid) {
		gameBoard.remove(gameGrid);
		gameGrid.setGamePlayer(this);
	}

	// Mapping between game ship and game grid

	public void mapGameShipToGameGrid(GameShipEntity gameShipEntity, GameGridEntity gameGridEntity) {
		if (gameShipEntity != null) {
			gameShipEntity.getGameShipGrids().add(gameGridEntity);
		}
	}

}
