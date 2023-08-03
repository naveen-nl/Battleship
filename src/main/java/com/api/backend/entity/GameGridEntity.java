
package com.api.backend.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

@Data
@Entity
@ToString(exclude = {"gamePlayer", "gameShip"})
@Table(name = "GameGrid")
public class GameGridEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long gameGridId;
	private int rowIndex;
	private int columnIndex;

	@Enumerated(EnumType.STRING)
	private GridValue gridValue;

	@ManyToOne
	@JoinColumn(name = "gamePlayerId", nullable = false)
	private GamePlayerEntity gamePlayer;

	@ManyToOne
	@JoinColumn(name = "gameShipId", nullable = true)
	private GameShipEntity gameShip;

}
