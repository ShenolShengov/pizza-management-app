package shengov.bg.pizzza_management_app.ingredient.exception;

import shengov.bg.pizzza_management_app.core.exception.ConflictException;
import shengov.bg.pizzza_management_app.ingredient.constant.IngredientValidationMessages;

public class IngredientAlreadyExistsException extends ConflictException {

  public IngredientAlreadyExistsException(String name) {
    super(IngredientValidationMessages.ALREADY_EXISTS.formatted(name));
  }
}
