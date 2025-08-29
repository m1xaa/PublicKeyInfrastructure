package com.ftnteam11_2025.pki.pki_system.util.exception.handler;

import com.ftnteam11_2025.pki.pki_system.util.dto.ErrorResponseDTO;
import com.ftnteam11_2025.pki.pki_system.util.exception.UnauthenticatedError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UnauthenticatedError.class)
    public ResponseEntity<Map<String, Object>> handleUnauthenticatedError(UnauthenticatedError ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "message", ex.getMessage(),
                        "code", 401
                ));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException exception,
            @NonNull final HttpHeaders headers,
            @NonNull final HttpStatusCode status,
            @NonNull final WebRequest request
    ) {
        logger.warn("MethodArgumentNotValidException: {}", exception.getMessage());

        final ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                HttpStatus.UNPROCESSABLE_ENTITY.value(), "Validation failed"
        );

        for (final FieldError error : exception.getBindingResult().getFieldErrors()) {
            errorResponseDTO.addError(error.getField(), error.getDefaultMessage());
        }
        for (final ObjectError error : exception.getBindingResult().getGlobalErrors()) {
            errorResponseDTO.addError(error.getObjectName(), error.getDefaultMessage());
        }

        return handleExceptionInternal(exception, errorResponseDTO, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolationException(ConstraintViolationException exception) {
        logger.warn("ConstraintViolationException: {}", exception.getMessage());

        Map<String, String> errors = exception.getConstraintViolations().stream()
                .collect(Collectors.toMap(violation ->
                        violation.getPropertyPath().toString(), ConstraintViolation::getMessage
                ));

        return ResponseEntity.unprocessableEntity().body(new ErrorResponseDTO(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Validation failed",
                errors
        ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(AuthenticationException exception) {
        logger.warn("AuthenticationException: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponseDTO(HttpStatus.UNAUTHORIZED.value(), exception.getMessage())
        );
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ErrorResponseDTO> handleAccessDeniedException(RuntimeException exception) {
        logger.warn("AccessDenied: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                new ErrorResponseDTO(HttpStatus.FORBIDDEN.value(), exception.getMessage())
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            final HttpMessageNotReadableException exception,
            @NonNull final HttpHeaders headers,
            @NonNull final HttpStatusCode status,
            @NonNull final WebRequest request
    ) {
        logger.warn("HttpMessageNotReadableException: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), exception.getMessage())
        );
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            final TypeMismatchException exception,
            @NonNull final HttpHeaders headers,
            @NonNull final HttpStatusCode status,
            @NonNull final WebRequest request
    ) {
        logger.warn("TypeMismatchException: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), exception.getMessage())
        );
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(
            final MissingServletRequestPartException exception,
            @NonNull final HttpHeaders headers,
            @NonNull final HttpStatusCode status,
            @NonNull final WebRequest request
    ) {
        logger.warn("MissingServletRequestPartException: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), exception.getMessage())
        );
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            final MissingServletRequestParameterException exception,
            @NonNull final HttpHeaders headers,
            @NonNull final HttpStatusCode status,
            @NonNull final WebRequest request
    ) {
        logger.warn("MissingServletRequestParameterException: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), exception.getMessage())
        );
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException exception) {
        logger.warn("MethodArgumentTypeMismatchException: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), exception.getMessage())
        );
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            final NoHandlerFoundException exception,
            @NonNull final HttpHeaders headers,
            @NonNull final HttpStatusCode status,
            @NonNull final WebRequest request
    ) {
        logger.warn("NoHandlerFoundException: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), exception.getMessage())
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            final HttpRequestMethodNotSupportedException exception,
            @NonNull final HttpHeaders headers,
            @NonNull final HttpStatusCode status,
            @NonNull final WebRequest request
    ) {
        logger.warn("HttpRequestMethodNotSupportedException: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
                new ErrorResponseDTO(HttpStatus.METHOD_NOT_ALLOWED.value(), exception.getMessage())
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            final HttpMediaTypeNotSupportedException exception,
            @NonNull final HttpHeaders headers,
            @NonNull final HttpStatusCode status,
            @NonNull final WebRequest request
    ) {
        logger.warn("HttpMediaTypeNotSupportedException: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(
                new ErrorResponseDTO(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), exception.getMessage())
        );
    }

    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
            @NonNull final MaxUploadSizeExceededException exception,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        logger.warn("MaxUploadSizeExceededException: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(
                new ErrorResponseDTO(
                        HttpStatus.PAYLOAD_TOO_LARGE.value(),
                        "Uploaded file size exceeds the allowed limit. Please upload a smaller file."
                )
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleAllExceptions(RuntimeException exception) {
        logger.error("Unhandled exception: {}", exception.getMessage(), exception);
        return ResponseEntity.internalServerError().body(
                new ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error")
        );
    }
}
