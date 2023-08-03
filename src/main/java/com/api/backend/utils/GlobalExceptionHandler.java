package com.api.backend.utils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.api.backend.exception.BattleshipApplicationException;
import com.api.backend.exception.InvalidPlayerDataException;
import com.api.backend.exception.ValidationException;
import com.api.backend.response.ErrorResponse;

/**
 * GlobalExceptionHandler is a centralized exception handling class for the Battleship API application.
 * It intercepts and handles various types of exceptions that may occur during the execution of API endpoints.
 * It provides meaningful error responses with appropriate HTTP status codes.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

	/**
	 * Handles the UsernameNotFoundException and returns an ErrorResponse with a "Bad Request" status.
	 *
	 * @param ex the UsernameNotFoundException to handle
	 * @return the ErrorResponse containing the error message
	 */
	@ExceptionHandler(UsernameNotFoundException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleUsernameNotFoundException(UsernameNotFoundException ex) {
		return new ErrorResponse("Invalid Authentication User", Collections.singletonList(ex.getMessage()));
	}

	/**
	 * Handles the MethodArgumentNotValidException and returns an ErrorResponse with a "Bad Request" status.
	 *
	 * @param ex the MethodArgumentNotValidException to handle
	 * @return the ErrorResponse containing the error messages from field validation errors
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		BindingResult result = ex.getBindingResult();
		List<FieldError> fieldErrors = result.getFieldErrors();
		List<String> errorMessages = fieldErrors.stream().map(FieldError::getDefaultMessage)
				.collect(Collectors.toList());

		return new ErrorResponse("Input Param Validation Failed.", errorMessages);
	}

	/**
	 * Handles the IllegalArgumentException and returns an ErrorResponse with a "Bad Request" status.
	 *
	 * @param ex the IllegalArgumentException to handle
	 * @return the ErrorResponse containing the error message
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
		return new ErrorResponse("Invalid Input Provided.", Collections.singletonList(ex.getMessage()));
	}

	/**
	 * Handles the InvalidPlayerDataException and returns an ErrorResponse.
	 *
	 * @param ex the InvalidPlayerDataException to handle
	 * @return the ErrorResponse containing the error message
	 */
	@ExceptionHandler(InvalidPlayerDataException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleInvalidPlayerDataException(InvalidPlayerDataException ex) {
		return new ErrorResponse("Invalid Player Data.", Collections.singletonList(ex.getMessage()));
	}

	/**
	 * Handles the ValidationException and returns an ErrorResponse.
	 *
	 * @param ex the ValidationException to handle
	 * @return the ErrorResponse containing the error message
	 */
	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleValidationException(final ValidationException ex) {
		return new ErrorResponse("Validation Failed.", Collections.singletonList(ex.getMessage()));
	}

	/**
	 * Handles the BattleshipApplicationException and returns an ErrorResponse with a "Bad Request" status.
	 *
	 * @param ex the BattleshipApplicationException to handle
	 * @return the ErrorResponse containing the error message
	 */
	@ExceptionHandler(BattleshipApplicationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleBattleshipApplicationException(BattleshipApplicationException ex) {
		return new ErrorResponse("BattleshipApplication Failed.", Collections.singletonList(ex.getMessage()));
	}

	/**
	 * Handles any other unexpected exception and returns an ErrorResponse with a "Internal Server Error" status.
	 *
	 * @param ex the unexpected exception to handle
	 * @return the ErrorResponse containing the error message
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handleInternalServerError(Exception ex) {
		return new ErrorResponse("Internal Server Error Occurred.", Collections.singletonList(ex.getMessage()));
	}
}
