package com.api.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.backend.entity.GamePlayerEntity;
import com.api.backend.entity.GameShipEntity;

public interface GameShipRepository extends JpaRepository<GameShipEntity, Long> {
	
	List<GameShipEntity> findBygamePlayer(GamePlayerEntity gamePlayer);

	GameShipEntity findByShip_ShipIdAndGamePlayer(Long shipId, GamePlayerEntity gamePlayer);

}
