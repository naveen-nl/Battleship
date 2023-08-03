
package com.api.backend.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.ToString;

@Data
@Entity
@ToString(exclude = {"gamePlayers"})
@Table(name = "Player")
public class PlayerEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long playerId;
	@Column(unique = true, nullable = false)
	private String playerName;
	private Boolean isLocked;
	private Boolean isPlaying;

	@OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties("player")
	private List<GamePlayerEntity> gamePlayers = new ArrayList<>();
}
