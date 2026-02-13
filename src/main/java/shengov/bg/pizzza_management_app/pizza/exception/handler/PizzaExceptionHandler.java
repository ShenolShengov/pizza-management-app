package shengov.bg.pizzza_management_app.pizza.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shengov.bg.pizzza_management_app.core.exception.ApiError;
import shengov.bg.pizzza_management_app.pizza.exception.PizzaAlreadyExistException;

@RestControllerAdvice
@Order(1)
public class PizzaExceptionHandler {

  @ExceptionHandler(PizzaAlreadyExistException.class)
  public ResponseEntity<ApiError> handlePizzaAlreadyExistException(
      PizzaAlreadyExistException ex, HttpServletRequest request) {
    HttpStatus status = HttpStatus.CONFLICT;
    return ResponseEntity.status(status).body(ApiError.from(ex.getMessage(), status, request));
  }
}
