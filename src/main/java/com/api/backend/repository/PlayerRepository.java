package com.api.backend.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

import com.api.backend.entity.PlayerEntity;

public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {

	PlayerEntity findByplayerName(String playerName);

	List<PlayerEntity> findAllByIsLocked(boolean b);

}
