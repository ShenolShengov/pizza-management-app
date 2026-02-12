package shengov.bg.pizzza_management_app.pizza.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import shengov.bg.pizzza_management_app.pizza.constants.PizzaValidationMessages;

@ResponseStatus(HttpStatus.CONFLICT)
public class PizzaAlreadyExistException extends RuntimeException {

  public PizzaAlreadyExistException(String name) {
    super(PizzaValidationMessages.PIZZA_EXISTS.formatted(name));
  }
}
