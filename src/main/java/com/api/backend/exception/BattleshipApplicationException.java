package com.api.backend.exception;


/**
 * The BattleshipApplicationException class represents a custom runtime exception specific to the Battleship API application.
 * It is used to handle application-level exceptions and errors that may occur during the execution of the application.
 * This exception provides a meaningful error message to describe the nature of the exception.
 * The class extends the RuntimeException class, making it an unchecked exception.
 */
public class BattleshipApplicationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new BattleshipApplicationException with the specified error message.
	 *
	 * @param message the error message that describes the exception
	 */
	public BattleshipApplicationException(String message) {
		super(message);
	}
}
