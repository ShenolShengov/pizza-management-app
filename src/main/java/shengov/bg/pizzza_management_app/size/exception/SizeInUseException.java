package shengov.bg.pizzza_management_app.size.exception;

import java.util.UUID;
import shengov.bg.pizzza_management_app.core.exception.ConflictException;
import shengov.bg.pizzza_management_app.size.constant.SizeValidationMessages;

public class SizeInUseException extends ConflictException {

  public SizeInUseException(UUID id) {
    super(SizeValidationMessages.IN_USE.formatted(id));
  }
}
