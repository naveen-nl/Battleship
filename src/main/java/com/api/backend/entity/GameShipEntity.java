
package com.api.backend.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@ToString(exclude = {"gameShipGrids", "gamePlayer"})
@Table(name = "GameShip")
public class GameShipEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long gameShipId;

	@ManyToOne
	@JoinColumn(name = "shipId", nullable = false)
	private ShipEntity ship;

	@OneToMany(mappedBy = "gameShip", cascade = CascadeType.ALL)
	@JsonIgnoreProperties("gameShip")
	private List<GameGridEntity> gameShipGrids;

	@Enumerated(EnumType.STRING)
	private ShipStatus shipStatus;

	@ManyToOne
	@JoinColumn(name = "gamePlayerid", nullable = false)
	private GamePlayerEntity gamePlayer;

	// helpers for bidirectional relationships
	public void addGameShipGrid(GameGridEntity gameGrid) {
		if (gameShipGrids == null) {
			gameShipGrids = new ArrayList<>();
		}
		gameShipGrids.add(gameGrid);
		gameGrid.setGameShip(this);
	}

	public void removeGameShip(GameGridEntity gameGrid) {
		gameShipGrids.remove(gameGrid);
		gameGrid.setGameShip(this);
	}
}
