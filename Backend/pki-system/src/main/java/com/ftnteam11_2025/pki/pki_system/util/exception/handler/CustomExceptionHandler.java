package com.ftnteam11_2025.pki.pki_system.util.exception.handler;

import com.ftnteam11_2025.pki.pki_system.util.dto.ErrorResponseDTO;
import com.ftnteam11_2025.pki.pki_system.util.exception.InvalidRequestError;
import com.ftnteam11_2025.pki.pki_system.util.exception.NotFoundError;
import com.ftnteam11_2025.pki.pki_system.util.exception.ServerError;
import com.ftnteam11_2025.pki.pki_system.util.exception.UnauthorizedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;

@ControllerAdvice
public class CustomExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(NotFoundError.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFoundError(NotFoundError error) {
        logger.warn("NotFoundError: {}", error.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), error.getMessage())
        );
    }

    @ExceptionHandler(InvalidRequestError.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidRequestError(InvalidRequestError error) {
        logger.warn("InvalidRequestError: {}", error.getMessage());
        return ResponseEntity.unprocessableEntity().body(new ErrorResponseDTO(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                error.getMessage(),
                error.getErrors() != null ? error.getErrors() : Collections.emptyMap()
        ));
    }

    @ExceptionHandler(UnauthorizedError.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDeniedException(RuntimeException exception) {
        logger.warn("AccessDenied: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                new ErrorResponseDTO(HttpStatus.FORBIDDEN.value(), exception.getMessage())
        );
    }

    @ExceptionHandler(ServerError.class)
    public ResponseEntity<ErrorResponseDTO> handleServerError(ServerError error) {
        int statusCode = (error.getCode() != null && error.getCode() >= 100 && error.getCode() <= 599)
                ? error.getCode() : HttpStatus.INTERNAL_SERVER_ERROR.value();
        logger.error("ServerError: {}", error.getMessage());
        return ResponseEntity.status(statusCode).body(
                new ErrorResponseDTO(statusCode, error.getMessage())
        );
    }
}
