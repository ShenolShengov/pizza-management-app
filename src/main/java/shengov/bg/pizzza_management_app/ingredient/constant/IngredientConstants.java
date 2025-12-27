package shengov.bg.pizzza_management_app.ingredient.constant;

public class IngredientConstants {

  private IngredientConstants() {
    throw new IllegalCallerException("Constant class");
  }

  public static final String SUCCESSFULLY_CREATE_INGREDIENT =
      "Ingredient with name %s is successfully created";

  public static final String INGREDIENT_ALREADY_EXIST_MESSAGE =
      "Ingredient with name %s already exist";
}
