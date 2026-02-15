package shengov.bg.pizzza_management_app.core.exception.handler;

import static shengov.bg.pizzza_management_app.core.exception.constant.ExceptionConstants.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.hibernate.JDBCException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import shengov.bg.pizzza_management_app.core.exception.ApiError;
import shengov.bg.pizzza_management_app.core.exception.ApiException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiError> handleApiException(ApiException ex, HttpServletRequest request) {
    return response(ex.getStatus(), ex.getMessage(), request);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiError> handleAccessDeniedException(HttpServletRequest request) {
    return response(HttpStatus.FORBIDDEN, ACCESS_DENIED_MESSAGE, request);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ApiError> handleNoHandlerFoundException(HttpServletRequest request) {
    return response(HttpStatus.NOT_FOUND, ENDPOINT_NOT_FOUND_MESSAGE, request);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ApiError> handleMethodNotSupportedException(
      HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
    String supportedMethods =
        Optional.ofNullable(ex.getSupportedMethods())
            .map(methods -> ". Supported method(s): " + String.join(", ", methods))
            .orElse("");
    return response(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage() + supportedMethods, request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidationException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    Map<String, String[]> errors =
        ex.getBindingResult().getFieldErrors().stream()
            .collect(
                Collectors.groupingBy(
                    FieldError::getField,
                    Collectors.mapping(
                        f -> f.getDefaultMessage() != null ? f.getDefaultMessage() : "",
                        Collectors.toList())))
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toArray(String[]::new)));
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status)
        .body(ApiError.from(VALIDATION_FAILED_MESSAGE, status, request, errors));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpServletRequest request) {
    return response(HttpStatus.BAD_REQUEST, MALFORMED_JSON_MESSAGE, request);
  }

  @ExceptionHandler(JDBCException.class)
  public ResponseEntity<ApiError> handleConstraintViolationException(HttpServletRequest request) {
    return response(HttpStatus.INTERNAL_SERVER_ERROR, DATABASE_ERROR_MESSAGE, request);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> handleIllegalArgumentException(HttpServletRequest request) {
    return response(HttpStatus.BAD_REQUEST, ILLEGAL_ARGUMENT_MESSAGE, request);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ApiError> handleIllegalStateException(
      IllegalStateException ex, HttpServletRequest request) {
    return response(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiError> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
    String message =
        ex.getRequiredType() == UUID.class ? INVALID_UUID_MESSAGE : ARGUMENT_TYPE_MISMATCH_MESSAGE;
    return response(HttpStatus.BAD_REQUEST, message, request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGenericException(HttpServletRequest request) {
    return response(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MESSAGE, request);
  }

  private ResponseEntity<ApiError> response(
      HttpStatus status, String message, HttpServletRequest request) {
    return ResponseEntity.status(status).body(ApiError.from(message, status, request));
  }
}
