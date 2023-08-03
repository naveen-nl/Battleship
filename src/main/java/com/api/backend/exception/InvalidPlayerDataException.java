package com.api.backend.exception;


/**
 * The InvalidPlayerDataException class represents a custom runtime exception specific to the Battleship API application.
 * It is a sub-class of the BattleshipApplicationException and is used to handle exceptions related to invalid player data.
 * This exception is thrown when there is an issue with the player data provided during the execution of the application.
 * The class extends the BattleshipApplicationException, which, in turn, extends the RuntimeException class, making it an unchecked exception.
 */
public class InvalidPlayerDataException extends BattleshipApplicationException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructs a new InvalidPlayerDataException with the specified error message.
     *
     * @param message the error message that describes the exception
     */
    public InvalidPlayerDataException(String message) {
        super(message);
    }
}
