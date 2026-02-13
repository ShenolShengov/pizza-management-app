package shengov.bg.pizzza_management_app.size.exception;

import static shengov.bg.pizzza_management_app.size.constant.SizeValidationMessages.ALREADY_EXISTS;

public class SizeAlreadyExistsException extends RuntimeException {

  public SizeAlreadyExistsException(String name) {
    super(ALREADY_EXISTS.formatted(name));
  }
}
