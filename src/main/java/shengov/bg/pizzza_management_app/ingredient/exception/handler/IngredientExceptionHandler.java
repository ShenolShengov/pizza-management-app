package shengov.bg.pizzza_management_app.ingredient.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shengov.bg.pizzza_management_app.core.exception.ApiError;
import shengov.bg.pizzza_management_app.ingredient.exception.IngredientAlreadyExistsException;

@RestControllerAdvice
public class IngredientExceptionHandler {

  @ExceptionHandler(IngredientAlreadyExistsException.class)
  public ApiError handleIngredientAlreadyExistException(
      IngredientAlreadyExistsException ex, HttpServletRequest request) {
    HttpStatus status = HttpStatus.CONFLICT;
    return ApiError.from(ex.getMessage(), status, request);
  }
}
