package shengov.bg.pizzza_management_app.ingredient.constant;

public final class IngredientValidationMessages {

  private IngredientValidationMessages() {
    throw new IllegalStateException("Validation messages class");
  }

  public static final String ALREADY_EXISTS = "Ingredient with name '%s' already exists";

  public static final String IN_USE =
      "Ingredient with id '%s' is used by one or more pizzas and cannot be deleted";

  public static final String NAME_REQUIRED = "Ingredient name is required";

  public static final String NAME_SIZE = "Ingredient name must be between 2 and 50 characters";
}
