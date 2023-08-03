package com.api.backend;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.PropertySource;

import com.api.backend.service.DataLoaderService;



/**
 * The BattleshipApplication class is the entry point of the Battleship API application.
 * It initializes and runs the Spring Boot application, and also loads ship information and some players' data.
 * The application is enabled with caching and reads configuration from the "configuration.properties" file.
 */
@SpringBootApplication
@EnableCaching
@PropertySource("classpath:configuration.properties")
public class BattleshipApplication {
	
	private static final Logger logger = LogManager.getLogger(BattleshipApplication.class);
	
	private DataLoaderService dataloaderService;
	

	/**
	 * Instantiates a new BattleshipApplication with the DataLoaderService.
	 *
	 * @param dataloaderService the DataLoaderService to load ship information and player data
	 */
	public BattleshipApplication(DataLoaderService dataloaderService) {
		this.dataloaderService = dataloaderService;
	}

	/**
	 * The main method to start the Battleship API application.
	 *
	 * @param args the command-line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(BattleshipApplication.class, args);
		logger.info("Started the springboot application");

	}
	
	/**
	 * Load ship info and some player data after the application context is initialized.
	 * This method is annotated with @PostConstruct and runs automatically after the application starts.
	 */
	@PostConstruct
	public void loadShipInfo() {
		dataloaderService.loadDataFromFile();
		dataloaderService.loadSomePlayers();
	}
}
