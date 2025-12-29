package shengov.bg.pizzza_management_app.ingredient.service.impls;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import shengov.bg.pizzza_management_app.core.exception.ResourceNotFoundException;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientRequest;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientResponse;
import shengov.bg.pizzza_management_app.ingredient.exception.IngredientAlreadyExistsException;
import shengov.bg.pizzza_management_app.ingredient.model.Ingredient;
import shengov.bg.pizzza_management_app.ingredient.repository.IngredientRepository;

@ExtendWith(MockitoExtension.class)
class IngredientServiceImplTest {

  private final String TEST_NAME = "Tomato";

  @Mock private IngredientRepository ingredientRepository;

  @InjectMocks private IngredientServiceImpl toTest;

  private Ingredient createTestIngredient(String name) {
    Ingredient ingredient = new Ingredient();
    ReflectionTestUtils.setField(ingredient, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(ingredient, "name", name);
    return ingredient;
  }

  private Ingredient createTestIngredient() {
    return createTestIngredient(TEST_NAME);
  }

  private IngredientRequest createTestIngredientRequest() {
    return new IngredientRequest(TEST_NAME);
  }

  @Test
  void create_ShouldSaveIngredient_WhenNameIsUnique() {

    Ingredient savedIngredient = createTestIngredient();
    IngredientRequest request = createTestIngredientRequest();

    when(ingredientRepository.existsByNameIgnoreCase(savedIngredient.getName())).thenReturn(false);
    when(ingredientRepository.save(any(Ingredient.class))).thenReturn(savedIngredient);

    IngredientResponse response = toTest.create(request);

    assertNotNull(response);
    assertEquals(savedIngredient.getName(), response.name());
    assertEquals(savedIngredient.getId(), response.id());
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

  @Test
  void getById_ShouldThrowException_WhenIngredientNotExist() {
    UUID notExistId = UUID.randomUUID();
    when(ingredientRepository.findById(notExistId)).thenReturn(Optional.empty());

    ResourceNotFoundException exception =
        assertThrows(ResourceNotFoundException.class, () -> toTest.getById(notExistId));

    assertTrue(exception.getMessage().contains(notExistId.toString()));
    assertTrue(exception.getMessage().contains("Ingredient"));
  }

  @Test
  void getById_ShouldReturnIngredient_WhenExist() {

    Ingredient ingredient = createTestIngredient();

    when(ingredientRepository.findById(ingredient.getId())).thenReturn(Optional.of(ingredient));

    IngredientResponse response = toTest.getById(ingredient.getId());

    assertEquals(response.id(), ingredient.getId());
    assertEquals(response.name(), ingredient.getName());
  }

  @Test
  void getAll_ShouldReturn_CorrectResult() {
    List<Ingredient> testIngredients =
        List.of(
            createTestIngredient(),
            createTestIngredient("Cheese"),
            createTestIngredient("Cucumber"));

    when(ingredientRepository.findAll(any(Pageable.class)))
        .thenReturn(new PageImpl<>(testIngredients));

    Page<IngredientResponse> all = toTest.getAll(Pageable.unpaged());

    assertEquals(all.getTotalElements(), testIngredients.size());
  }
}
