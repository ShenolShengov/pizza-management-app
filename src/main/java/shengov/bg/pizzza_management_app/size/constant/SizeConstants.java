package shengov.bg.pizzza_management_app.size.constant;

public final class SizeConstants {

  private SizeConstants() {
    throw new IllegalCallerException("Constant class");
  }

  public static final String SIZE_EXIST = "Size with name %s already exist";

  public static final String INVALID_NAME = "Size name must be between 1 and 15 characters";
}
