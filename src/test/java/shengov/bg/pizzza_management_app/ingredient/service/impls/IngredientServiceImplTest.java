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
import shengov.bg.pizzza_management_app.ingredient.exception.IngredientInUseException;
import shengov.bg.pizzza_management_app.ingredient.mapper.IngredientMapper;
import shengov.bg.pizzza_management_app.ingredient.model.IngredientEntity;
import shengov.bg.pizzza_management_app.ingredient.repository.IngredientRepository;
import shengov.bg.pizzza_management_app.pizza.repository.PizzaRepository;

@ExtendWith(MockitoExtension.class)
class IngredientServiceImplTest {

  private final String TEST_NAME = "Tomato";

  @Mock private IngredientRepository ingredientRepository;
  @Mock private PizzaRepository pizzaRepository;
  @Mock private IngredientMapper mapper;

  @InjectMocks private IngredientServiceImpl toTest;

  private IngredientEntity createTestIngredient(String name) {
    IngredientEntity ingredient = new IngredientEntity();
    ReflectionTestUtils.setField(ingredient, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(ingredient, "name", name);
    return ingredient;
  }

  private IngredientEntity createTestIngredient() {
    return createTestIngredient(TEST_NAME);
  }

  private IngredientRequest createTestIngredientRequest() {
    return new IngredientRequest(TEST_NAME);
  }

  @Test
  void create_ShouldSaveIngredient_WhenNameIsUnique() {

    IngredientEntity savedIngredient = createTestIngredient();
    IngredientRequest request = createTestIngredientRequest();

    IngredientResponse expectedResponse =
        new IngredientResponse(savedIngredient.getId(), savedIngredient.getName());
    when(ingredientRepository.existsByNameIgnoreCase(savedIngredient.getName())).thenReturn(false);
    when(mapper.requestToEntity(request)).thenReturn(savedIngredient);
    when(ingredientRepository.save(any(IngredientEntity.class))).thenReturn(savedIngredient);
    when(mapper.entityToResponse(savedIngredient)).thenReturn(expectedResponse);

    IngredientResponse response = toTest.create(request);

    assertNotNull(response);
    assertEquals(savedIngredient.getName(), response.name());
    assertEquals(savedIngredient.getId(), response.id());
    verify(ingredientRepository, times(1)).save(any(IngredientEntity.class));
  }

  @Test
  void create_ShouldThrowException_WhenIngredientNameAlreadyExists() {

    IngredientRequest request = createTestIngredientRequest();

    when(ingredientRepository.existsByNameIgnoreCase(TEST_NAME)).thenReturn(true);

    IngredientAlreadyExistsException exception =
        assertThrows(IngredientAlreadyExistsException.class, () -> toTest.create(request));

    assertTrue(exception.getMessage().contains(TEST_NAME));

    verify(ingredientRepository, never()).save(any(IngredientEntity.class));
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

    IngredientEntity ingredient = createTestIngredient();

    IngredientResponse expectedResponse =
        new IngredientResponse(ingredient.getId(), ingredient.getName());
    when(ingredientRepository.findById(ingredient.getId())).thenReturn(Optional.of(ingredient));
    when(mapper.entityToResponse(ingredient)).thenReturn(expectedResponse);

    IngredientResponse response = toTest.getById(ingredient.getId());

    assertEquals(response.id(), ingredient.getId());
    assertEquals(response.name(), ingredient.getName());
  }

  @Test
  void getAll_ShouldReturn_CorrectResult() {
    List<IngredientEntity> testIngredients =
        List.of(
            createTestIngredient(),
            createTestIngredient("Cheese"),
            createTestIngredient("Cucumber"));

    when(ingredientRepository.findAll(any(Pageable.class)))
        .thenReturn(new PageImpl<>(testIngredients));
    when(mapper.entityToResponse(any(IngredientEntity.class)))
        .thenAnswer(
            inv -> {
              IngredientEntity e = inv.getArgument(0);
              return new IngredientResponse(e.getId(), e.getName());
            });

    Page<IngredientResponse> all = toTest.getAll(Pageable.unpaged());

    assertEquals(all.getTotalElements(), testIngredients.size());
  }

  @Test
  void update_ShouldThrow_WhenIngredientNotExist() {
    UUID notExistId = UUID.randomUUID();
    when(ingredientRepository.findById(notExistId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> toTest.update(notExistId, createTestIngredientRequest()));
  }

  @Test
  void update_ShouldUpdate_WhenIngredientExist() {
    String name = "Pepperoni";
    IngredientEntity ingredientToUpdate = createTestIngredient(name);
    IngredientRequest request = createTestIngredientRequest();
    when(ingredientRepository.findById(ingredientToUpdate.getId()))
        .thenReturn(Optional.of(ingredientToUpdate));
    when(mapper.entityToResponse(ingredientToUpdate))
        .thenAnswer(
            inv -> {
              IngredientEntity e = inv.getArgument(0);
              return new IngredientResponse(e.getId(), e.getName());
            });

    IngredientResponse response = toTest.update(ingredientToUpdate.getId(), request);

    assertEquals(response.name(), TEST_NAME);
    assertEquals(ingredientToUpdate.getName(), TEST_NAME);
  }

  @Test
  void delete_ShouldThrow_WhenIngredientNotExist() {
    UUID notExistId = UUID.randomUUID();
    when(pizzaRepository.existsByIngredientsId(notExistId)).thenReturn(false);
    when(ingredientRepository.findById(notExistId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> toTest.delete(notExistId));

    verify(ingredientRepository, never()).delete(any(IngredientEntity.class));
  }

  @Test
  void delete_ShouldThrow_WhenIngredientIsUsedByPizza() {
    IngredientEntity ingredient = createTestIngredient();
    when(pizzaRepository.existsByIngredientsId(ingredient.getId())).thenReturn(true);

    assertThrows(IngredientInUseException.class, () -> toTest.delete(ingredient.getId()));

    verify(ingredientRepository, never()).delete(any(IngredientEntity.class));
  }

  @Test
  void delete_ShouldDelete_WhenIngredientExist() {
    IngredientEntity ingredient = createTestIngredient();
    when(pizzaRepository.existsByIngredientsId(ingredient.getId())).thenReturn(false);
    when(ingredientRepository.findById(ingredient.getId())).thenReturn(Optional.of(ingredient));

    toTest.delete(ingredient.getId());

    verify(ingredientRepository, times(1)).delete(ingredient);
  }
}
