package shengov.bg.pizzza_management_app.ingredient.service.impls;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientRequest;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientResponse;
import shengov.bg.pizzza_management_app.ingredient.exception.IngredientAlreadyExistsException;
import shengov.bg.pizzza_management_app.ingredient.model.Ingredient;
import shengov.bg.pizzza_management_app.ingredient.repository.IngredientRepository;

@ExtendWith(MockitoExtension.class)
class IngredientServiceImplTest {

  private final UUID TEST_ID = UUID.randomUUID();
  private final String TEST_NAME = "Tomato";

  @Mock private IngredientRepository ingredientRepository;

  @InjectMocks private IngredientServiceImpl toTest;

  private Ingredient createTestIngredient() {
    Ingredient ingredient = new Ingredient();
    ReflectionTestUtils.setField(ingredient, "id", TEST_ID);
    ReflectionTestUtils.setField(ingredient, "name", TEST_NAME);
    return ingredient;
  }

  private IngredientRequest createTestIngredientRequest() {
    return new IngredientRequest(TEST_NAME);
  }

  @Test
  void create_ShouldSaveIngredient_WhenNameIsUnique() {

    Ingredient savedIngredient = createTestIngredient();
    IngredientRequest request = createTestIngredientRequest();

    when(ingredientRepository.existsByNameIgnoreCase(TEST_NAME)).thenReturn(false);
    when(ingredientRepository.save(any(Ingredient.class))).thenReturn(savedIngredient);

    IngredientResponse response = toTest.create(request);

    assertNotNull(response);
    assertEquals(TEST_NAME, response.name());
    assertEquals(TEST_ID, response.id());
    verify(ingredientRepository, times(1)).save(any(Ingredient.class));
  }

  @Test
  void create_ShouldThrowException_WhenIngredientNameAlreadyExists() {

    IngredientRequest request = createTestIngredientRequest();

    when(ingredientRepository.existsByNameIgnoreCase(TEST_NAME)).thenReturn(true);

    IngredientAlreadyExistsException exception =
        assertThrows(IngredientAlreadyExistsException.class, () -> toTest.create(request));

    assertTrue(exception.getMessage().contains(TEST_NAME));

    verify(ingredientRepository, never()).save(any(Ingredient.class));
  }
}
