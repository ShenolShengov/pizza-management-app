package shengov.bg.pizzza_management_app.ingredient.constant;

public final class IngredientConstants {

  private IngredientConstants() {
    throw new IllegalCallerException("Constant class");
  }

  public static final String INGREDIENT_EXISTS = "Ingredient with name %s already exist";

  public static final String INVALID_NAME = "Ingredient name must be between 2 and 50 characters";
}
