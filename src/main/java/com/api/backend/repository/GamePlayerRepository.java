package com.api.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.backend.entity.GamePlayerEntity;

public interface GamePlayerRepository extends JpaRepository<GamePlayerEntity, Long> {

}
