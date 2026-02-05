package shengov.bg.pizzza_management_app.size.exception;

import static shengov.bg.pizzza_management_app.size.constant.SizeValidationMessages.SIZE_EXIST;

public class SizeAlreadyExistsException extends RuntimeException {

  public SizeAlreadyExistsException(String name) {
    super(SIZE_EXIST.formatted(name));
  }
}
