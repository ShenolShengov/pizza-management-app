package shengov.bg.pizzza_management_app.ingredient.exception;

import static shengov.bg.pizzza_management_app.ingredient.constant.IngredientConstants.INGREDIENT_ALREADY_EXIST_MESSAGE;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class IngredientAlreadyExistsException extends RuntimeException {

  public IngredientAlreadyExistsException(String name) {
    super(INGREDIENT_ALREADY_EXIST_MESSAGE.formatted(name));
  }
}
