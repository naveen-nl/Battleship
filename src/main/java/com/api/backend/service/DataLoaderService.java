package com.api.backend.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.backend.entity.PlayerEntity;
import com.api.backend.entity.ShipEntity;
import com.api.backend.repository.PlayerRepository;
import com.api.backend.repository.ShipRepository;
import com.api.backend.utils.Constants;

@Service
public class DataLoaderService {

	private ShipRepository shipRepository;
	private Environment env;
	private PlayerRepository playerRepository;

	@Autowired
	public DataLoaderService(ShipRepository shipRepository, Environment env, PlayerRepository playerRepository) {

		this.shipRepository = shipRepository;
		this.env = env;
		this.playerRepository = playerRepository;
	}

	@Transactional
	public void loadDataFromFile() {
		try {
			InputStream inputStream = getClass().getResourceAsStream(Constants.SHIP_DATA_FILE_PATH);
			if (inputStream != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				String line;
				while ((line = reader.readLine()) != null) {
					String[] data = line.split(";");
					if (data.length == 2) {
						ShipEntity shipEntity = new ShipEntity();
						shipEntity.setShipName(data[0]);
						shipEntity.setShipLength(Integer.parseInt(data[1]));
						shipRepository.save(shipEntity);
					}
				}
				reader.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(env.getProperty("DataloaderService.INCORRECT_FORMAT"));
		}

	}

	@Transactional
	public void loadSomePlayers() {
		List<PlayerEntity> players = new ArrayList<>();
		PlayerEntity p1 = new PlayerEntity();
		p1.setPlayerName("JohnA1");
		p1.setIsLocked(false);
		p1.setIsPlaying(false);
		players.add(p1);
		PlayerEntity p2 = new PlayerEntity();
		p2.setPlayerName("JohnA2");
		p2.setIsLocked(false);
		p2.setIsPlaying(false);
		players.add(p2);
		PlayerEntity p3 = new PlayerEntity();
		p3.setPlayerName("Dream1");
		p3.setIsLocked(false);
		p3.setIsPlaying(false);
		players.add(p3);
		PlayerEntity p4 = new PlayerEntity();
		p4.setPlayerName("Dream2");
		p4.setIsLocked(false);
		p4.setIsPlaying(false);
		players.add(p4);
		playerRepository.saveAll(players);

	}

	public void makePlayer1Winner() {
		//TODO : Create game logic to test behavior of game completion and winner
	}
	
}