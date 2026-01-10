package shengov.bg.pizzza_management_app.testutils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientRequest;
import shengov.bg.pizzza_management_app.ingredient.model.Ingredient;
import shengov.bg.pizzza_management_app.ingredient.repository.IngredientRepository;

@Component
public final class IngredientTestUtils {

  @Autowired private IngredientRepository ingredientRepository;

  public static IngredientRequest createTestIngredientRequest(String name) {
    return new IngredientRequest(name);
  }

  public static Ingredient createTestIngredient(IngredientRequest request) {
    Ingredient ingredient = new Ingredient();
    ingredient.setName(request.name());
    return ingredient;
  }

  public Ingredient saveTestIngredient(IngredientRequest request) {
    return ingredientRepository.save(createTestIngredient(request));
  }

  public void saveIngredients(String[] names) {
    for (String ingredient : names) {
      saveTestIngredient(createTestIngredientRequest(ingredient));
    }
  }
}
