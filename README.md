# Battleship API

The Battleship API is a backend application that provides functionalities to manage and play the game of Battleship. This API allows players to join the game, create new games, perform game play turns, and retrieve game information. It also provides information about available ships that can be used in the game.

## H2 Database Link

- [H2 Database Console](http://localhost:8080/h2-console/)

## API Endpoints

- Join Game API: [http://localhost:8080/game/v1/joingame](http://localhost:8080/game/v1/joingame)
- Get Available Players API: [http://localhost:8080/game/v1/availableplayers](http://localhost:8080/game/v1/availableplayers)
- Create Game API: [http://localhost:8080/game/v1/creategame](http://localhost:8080/game/v1/creategame)
- Fetch Game Details API: [http://localhost:8080/game/v1/{gameId}](http://localhost:8080/game/v1/{gameId})
- Game Play API: [http://localhost:8080/game/v1/gameplay](http://localhost:8080/game/v1/gameplay)
- Get Ships API: [http://localhost:8080/game/v1/ships](http://localhost:8080/game/v1/ships)

## Swagger UI

To explore and test the APIs, you can use the Swagger UI:

- [Swagger UI](http://localhost:8080/swagger-ui.html)

## JaCoCo report

JaCoCo code coverage report location:

- \target\site\jacoco\index.html

## Actuator URL

To monitor and manage the application and provide insights into health and performance, the available endpoints include:

- Health: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health) - Provides application health information.
- Info: [http://localhost:8080/actuator/info](http://localhost:8080/actuator/info) - Provides custom application information.
- Metrics: [http://localhost:8080/actuator/metrics](http://localhost:8080/actuator/metrics) - Exposes various application metrics.
- Environment: [http://localhost:8080/actuator/env](http://localhost:8080/actuator/env) - Exposes the application's environment properties.
- Mappings: [http://localhost:8080/actuator/mappings](http://localhost:8080/actuator/mappings) - Exposes details of the Spring MVC mappings.
- Beans: [http://localhost:8080/actuator/beans](http://localhost:8080/actuator/beans) - Exposes details of Spring beans.
- Trace: [http://localhost:8080/actuator/trace](http://localhost:8080/actuator/trace) - Provides tracing information.

## Build and Run

To build and run the Battleship API application, follow these steps:

1. Make sure you have Java 11 or later installed on your system.
2. Clone the repository to your local machine.
3. Navigate to the project directory.
4. Build the application using the following command:
           mvnw clean install
5. Run the application using the following command:
           mvnw spring-boot:run
6. The application will be accessible at [http://localhost:8080](http://localhost:8080).
7. Run the following command to execute unit tests 
           mvnw test
8. Run the following command to generate JaCoCo report 
           mvnw jacoco:report