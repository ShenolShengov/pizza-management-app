package shengov.bg.pizzza_management_app.ingredient.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import shengov.bg.pizzza_management_app.ingredient.constant.IngredientConstants;

@ResponseStatus(HttpStatus.CONFLICT)
public class IngredientAlreadyExistsException extends RuntimeException {

  public IngredientAlreadyExistsException(String name) {
    super(IngredientConstants.INGREDIENT_EXISTS.formatted(name));
  }
}
