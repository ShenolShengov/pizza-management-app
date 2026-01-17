package shengov.bg.pizzza_management_app.testutils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientRequest;
import shengov.bg.pizzza_management_app.ingredient.model.IngredientEntity;
import shengov.bg.pizzza_management_app.ingredient.repository.IngredientRepository;

@Component
public final class IngredientTestUtils {

  @Autowired private IngredientRepository ingredientRepository;

  public static IngredientRequest createTestIngredientRequest(String name) {
    return new IngredientRequest(name);
  }

  public static IngredientEntity createTestIngredient(IngredientRequest request) {
    IngredientEntity ingredient = new IngredientEntity();
    ingredient.setName(request.name());
    return ingredient;
  }

  public IngredientEntity saveTestIngredient(IngredientRequest request) {
    return ingredientRepository.save(createTestIngredient(request));
  }

  public void saveIngredients(String[] names) {
    for (String ingredient : names) {
      saveTestIngredient(createTestIngredientRequest(ingredient));
    }
  }
}
