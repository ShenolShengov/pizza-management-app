package shengov.bg.pizzza_management_app.size.exception;

import shengov.bg.pizzza_management_app.core.exception.ConflictException;
import shengov.bg.pizzza_management_app.size.constant.SizeValidationMessages;

public class SizeAlreadyExistsException extends ConflictException {

  public SizeAlreadyExistsException(String name) {
    super(SizeValidationMessages.ALREADY_EXISTS.formatted(name));
  }
}
