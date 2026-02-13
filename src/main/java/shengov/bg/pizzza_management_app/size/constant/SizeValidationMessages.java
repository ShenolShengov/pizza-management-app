package shengov.bg.pizzza_management_app.size.constant;

public final class SizeValidationMessages {

  private SizeValidationMessages() {
    throw new IllegalStateException("Validation messages class");
  }

  public static final String ALREADY_EXISTS = "Size with name '%s' already exists";

  public static final String NAME_REQUIRED = "Size name is required";

  public static final String NAME_SIZE = "Size name must be between 1 and 15 characters";
}
