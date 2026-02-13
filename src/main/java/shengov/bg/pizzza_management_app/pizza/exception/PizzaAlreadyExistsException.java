package shengov.bg.pizzza_management_app.pizza.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import shengov.bg.pizzza_management_app.pizza.constant.PizzaValidationMessages;

@ResponseStatus(HttpStatus.CONFLICT)
public class PizzaAlreadyExistsException extends RuntimeException {

  public PizzaAlreadyExistsException(String name) {
    super(PizzaValidationMessages.ALREADY_EXISTS.formatted(name));
  }
}
