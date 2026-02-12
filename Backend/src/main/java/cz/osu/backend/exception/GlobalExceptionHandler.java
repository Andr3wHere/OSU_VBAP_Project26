package cz.osu.backend.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.PersistenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validace selhala", errors);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(ResourceNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, "Tato HTTP metoda není pro daný endpoint povolena.", null);
    }

    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(HttpClientErrorException.BadRequest ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonParseException(HttpMessageNotReadableException ex) {
        String errorMessage = "Špatný formát dat v požadavku.";
        if (ex.getCause() instanceof InvalidFormatException invalidFormatException) {
            if (invalidFormatException.getTargetType().isEnum()) {
                String fieldName = invalidFormatException.getPath().get(0).getFieldName();

                String allowedValues = Arrays.toString(invalidFormatException.getTargetType().getEnumConstants());

                errorMessage = String.format("Neplatná hodnota pro pole '%s'. Povolené hodnoty jsou: %s", fieldName, allowedValues);
            }
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Přístup odepřen: Nemáte dostatečná oprávnění.", null);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Chyba autentizace: " + ex.getMessage(), null);
    }

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<Map<String, Object>> handlePersistenceException(PersistenceException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Chyba persistence: " + ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Došlo k interní chybě serveru.", null);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message, Object details) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);
        if (details != null) response.put("details", details);

        return new ResponseEntity<>(response, status);
    }
}
