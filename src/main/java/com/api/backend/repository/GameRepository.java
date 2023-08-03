package com.api.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.backend.entity.GameEntity;

public interface GameRepository extends JpaRepository<GameEntity, UUID> {

    

}
