package com.api.backend.exception;
/**
 * The ValidationException class represents a custom runtime exception specific to the Battleship API application.
 * It is a sub-class of the BattleshipApplicationException and is used to handle validation-related exceptions.
 * This exception is thrown when there is an issue with the validation of certain data during the execution of the application.
 * The class extends the BattleshipApplicationException, which, in turn, extends the RuntimeException class, making it an unchecked exception.
 */
public class ValidationException extends BattleshipApplicationException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructs a new ValidationException with the specified error message.
     *
     * @param message the error message that describes the exception
     */
    public ValidationException(String message) {
        super(message);
    }
}
