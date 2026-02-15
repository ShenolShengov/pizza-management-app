package shengov.bg.pizzza_management_app.pizza.exception;

import shengov.bg.pizzza_management_app.core.exception.ConflictException;
import shengov.bg.pizzza_management_app.pizza.constant.PizzaValidationMessages;

public class PizzaAlreadyExistsException extends ConflictException {

  public PizzaAlreadyExistsException(String name) {
    super(PizzaValidationMessages.ALREADY_EXISTS.formatted(name));
  }
}
