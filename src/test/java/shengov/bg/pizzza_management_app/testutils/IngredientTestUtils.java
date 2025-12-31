package shengov.bg.pizzza_management_app.testutils;

import shengov.bg.pizzza_management_app.ingredient.dto.IngredientRequest;
import shengov.bg.pizzza_management_app.ingredient.model.Ingredient;

public final class IngredientTestUtils {

  private IngredientTestUtils() {
    throw new IllegalCallerException("Utils class");
  }

  public static IngredientRequest createTestIngredientRequest(String name) {
    return new IngredientRequest(name);
  }

  public static Ingredient createTestIngredient(IngredientRequest request) {
    Ingredient ingredient = new Ingredient();
    ingredient.setName(request.name());
    return ingredient;
  }
}
