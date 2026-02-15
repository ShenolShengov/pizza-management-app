package shengov.bg.pizzza_management_app.core.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends ApiException {

  public ConflictException(String message) {
    super(HttpStatus.CONFLICT, message);
  }
}
