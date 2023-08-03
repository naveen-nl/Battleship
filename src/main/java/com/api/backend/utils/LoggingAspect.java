package com.api.backend.utils;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.api.backend.exception.BattleshipApplicationException;
import com.api.backend.exception.InvalidPlayerDataException;
import com.api.backend.exception.ValidationException;

/**
 * The LoggingAspect class is an Aspect-oriented Programming (AOP) component that provides logging capabilities
 * for various components in the Battleship API application. It intercepts method executions in controller, service,
 * utils, and repository classes, logs method entry and exit with parameters and return values, and logs execution time.
 */
@Component
@Aspect
public class LoggingAspect {

    private final Logger logger = LogManager.getLogger(LoggingAspect.class);

    /**
     * Logs method entry, parameters, and execution time for all methods in controller, service, utils, and repository
     * classes. Also, logs method exit with the return value if available.
     *
     * @param joinPoint the ProceedingJoinPoint representing the intercepted method execution
     * @return the result of the method execution, if any
     * @throws Throwable if an exception occurs during method execution
     */
    @Around("execution(* com.api.backend.controller.*.*(..)) || " +
            "execution(* com.api.backend.service.*.*(..)) || " +
            "execution(* com.api.backend.utils.*.*(..)) || " +
            "execution(* com.api.backend.repository.*.*(..))")
    public Object logAroundAllMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String data = Arrays.toString(joinPoint.getArgs());

        logger.info("Entering into {}.{}() with param: {}", className, methodName, data);

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception exception) {
            logException(className, methodName, exception);
            throw exception;
        }

        if (result != null) {
            String objectAsString = result.toString();
            logger.info("Exiting {}.{}() with result: {}", className, methodName, objectAsString);
        } else {
            logger.info("Exiting {}.{}() with result: null", className, methodName);
        }

        long endTime = System.currentTimeMillis();
        String executionTime = Long.toString(endTime - startTime);

        logger.info("--- Execution completed in {} ms", executionTime);

        return result;
    }

    /**
     * Logs detailed information about the exception occurred during method execution, including class name,
     * method name, and exception message. For known application-specific exceptions (BattleshipApplicationException,
     * ValidationException, InvalidPlayerDataException), the method re-throws them. For other unhandled exceptions,
     * it throws a RuntimeException wrapping the original exception.
     *
     * @param className the name of the class where the exception occurred
     * @param methodName the name of the method where the exception occurred
     * @param exception the exception that occurred during method execution
     * @throws Exception if the exception is an instance of BattleshipApplicationException, ValidationException,
     *                   or InvalidPlayerDataException
     */
    private void logException(String className, String methodName, Throwable exception) throws Exception {
        logger.error("Error and Exception:\n");
        logger.error("Class Name: " + className);
        logger.error("Method Name: " + methodName);
        logger.error("Exception Message: " + exception.getMessage());

        if (exception instanceof BattleshipApplicationException ||
                exception instanceof ValidationException ||
                exception instanceof InvalidPlayerDataException) {
            throw (BattleshipApplicationException) exception;
        } else {
            throw new RuntimeException("Unhandled exception occurred in " + className + "." + methodName, exception);
        }
    }
}
