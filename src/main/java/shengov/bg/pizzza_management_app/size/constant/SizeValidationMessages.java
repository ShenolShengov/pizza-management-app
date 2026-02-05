package shengov.bg.pizzza_management_app.size.constant;

public final class SizeValidationMessages {

  private SizeValidationMessages() {
    throw new IllegalCallerException("Validation messages class");
  }

  public static final String SIZE_EXIST = "Size with name %s already exist";

  public static final String NAME_REQUIRED = "Size name is required";

  public static final String NAME_SIZE = "Size name must be between 1 and 15 characters";
}
