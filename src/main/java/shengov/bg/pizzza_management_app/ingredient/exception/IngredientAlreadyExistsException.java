package shengov.bg.pizzza_management_app.ingredient.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import shengov.bg.pizzza_management_app.ingredient.constant.IngredientValidationMessages;

@ResponseStatus(HttpStatus.CONFLICT)
public class IngredientAlreadyExistsException extends RuntimeException {

  public IngredientAlreadyExistsException(String name) {
    super(IngredientValidationMessages.ALREADY_EXISTS.formatted(name));
  }
}
