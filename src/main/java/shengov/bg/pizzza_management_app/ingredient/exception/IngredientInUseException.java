package shengov.bg.pizzza_management_app.ingredient.exception;

import java.util.UUID;
import shengov.bg.pizzza_management_app.core.exception.ConflictException;
import shengov.bg.pizzza_management_app.ingredient.constant.IngredientValidationMessages;

public class IngredientInUseException extends ConflictException {

  public IngredientInUseException(UUID id) {
    super(IngredientValidationMessages.IN_USE.formatted(id));
  }
}
