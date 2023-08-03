package com.api.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.backend.entity.GameGridEntity;
import com.api.backend.entity.GamePlayerEntity;

public interface GameGridRepository extends JpaRepository<GameGridEntity, Long> {
	 
	GameGridEntity findByRowIndexAndColumnIndexAndGamePlayer(int rowIndex, int columnIndex,
			GamePlayerEntity gamePlayer);

}
