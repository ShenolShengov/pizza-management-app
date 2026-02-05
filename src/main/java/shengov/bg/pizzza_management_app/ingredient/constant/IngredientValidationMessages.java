package shengov.bg.pizzza_management_app.ingredient.constant;

public final class IngredientValidationMessages {

  private IngredientValidationMessages() {
    throw new IllegalCallerException("Validation messages class");
  }

  public static final String INGREDIENT_EXISTS = "Ingredient with name %s already exist";

  public static final String NAME_REQUIRED = "Ingredient name is required";

  public static final String NAME_SIZE = "Ingredient name must be between 2 and 50 characters";
}
