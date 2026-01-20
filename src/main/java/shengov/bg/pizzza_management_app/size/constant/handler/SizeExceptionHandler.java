package shengov.bg.pizzza_management_app.size.constant.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shengov.bg.pizzza_management_app.core.exception.ApiError;
import shengov.bg.pizzza_management_app.size.exception.SizeAlreadyExistsException;

@RestControllerAdvice
@Order(1)
public class SizeExceptionHandler {

  @ExceptionHandler(SizeAlreadyExistsException.class)
  public ResponseEntity<ApiError> handleSizeAlreadyExistException(
          SizeAlreadyExistsException ex, HttpServletRequest request) {
    HttpStatus status = HttpStatus.CONFLICT;
    return ResponseEntity.status(status).body(ApiError.from(ex.getMessage(), status, request));
  }
}
