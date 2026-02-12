package shengov.bg.pizzza_management_app.pizza.constants;

public final class PizzaValidationMessages {

  private PizzaValidationMessages() {
    throw new IllegalStateException("Validation messages class");
  }

  public static final String PIZZA_EXISTS = "Ingredient with name %s already exist";

  public static final String NAME_REQUIRED = "Pizza name is required";

  public static final String NAME_SIZE = "Pizza name must be between 3 and 40 characters";

  public static final String DESCRIPTION_SIZE = "Description cannot exceed 200 characters";

  public static final String INGREDIENTS_REQUIRED = "Pizza must have at least one ingredient";

  public static final String SIZES_REQUIRED = "Pizza must have at least one size";

  public static final String SIZE_ID_REQUIRED = "Size ID is required";

  public static final String PRICE_REQUIRED = "Price is required";

  public static final String PRICE_POSITIVE = "Price must be greater than 0";
}
