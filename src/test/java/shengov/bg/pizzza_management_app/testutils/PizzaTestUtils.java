package shengov.bg.pizzza_management_app.testutils;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shengov.bg.pizzza_management_app.ingredient.model.IngredientEntity;
import shengov.bg.pizzza_management_app.ingredient.repository.IngredientRepository;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaRequest;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaSizeRequest;
import shengov.bg.pizzza_management_app.pizza.model.PizzaEntity;
import shengov.bg.pizzza_management_app.pizza.model.PizzaSize;
import shengov.bg.pizzza_management_app.pizza.repository.PizzaRepository;
import shengov.bg.pizzza_management_app.size.model.SizeEntity;
import shengov.bg.pizzza_management_app.size.repository.SizeRepository;

@Component
public final class PizzaTestUtils {

  @Autowired private PizzaRepository pizzaRepository;
  @Autowired private IngredientRepository ingredientRepository;
  @Autowired private SizeRepository sizeRepository;

  public IngredientEntity saveIngredient(String name) {
    IngredientEntity ingredient = new IngredientEntity();
    ingredient.setName(name);
    return ingredientRepository.save(ingredient);
  }

  public SizeEntity saveSize(String name) {
    SizeEntity size = new SizeEntity();
    size.setName(name);
    return sizeRepository.save(size);
  }

  public PizzaEntity savePizza(
      String name,
      List<IngredientEntity> ingredients,
      List<SizeEntity> sizes,
      List<BigDecimal> prices) {
    PizzaEntity pizza = new PizzaEntity();
    pizza.setName(name);
    pizza.addIngredients(ingredients);
    for (int i = 0; i < sizes.size(); i++) {
      pizza.addSize(new PizzaSize(pizza, sizes.get(i), prices.get(i)));
    }
    return pizzaRepository.save(pizza);
  }

  public static PizzaRequest createTestPizzaRequest(
      String name, String description, List<UUID> ingredientIds, List<PizzaSizeRequest> sizes) {
    return new PizzaRequest(name, description, ingredientIds, sizes);
  }

  public static PizzaSizeRequest createSizeRequest(UUID sizeId, BigDecimal price) {
    return new PizzaSizeRequest(sizeId, price);
  }
}
