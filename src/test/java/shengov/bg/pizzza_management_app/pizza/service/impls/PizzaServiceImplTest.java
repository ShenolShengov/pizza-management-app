package shengov.bg.pizzza_management_app.pizza.service.impls;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
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
import shengov.bg.pizzza_management_app.ingredient.model.IngredientEntity;
import shengov.bg.pizzza_management_app.ingredient.repository.IngredientRepository;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaRequest;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaResponse;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaSizeRequest;
import shengov.bg.pizzza_management_app.pizza.exception.PizzaAlreadyExistsException;
import shengov.bg.pizzza_management_app.pizza.mapper.PizzaMapper;
import shengov.bg.pizzza_management_app.pizza.model.PizzaEntity;
import shengov.bg.pizzza_management_app.pizza.model.PizzaSize;
import shengov.bg.pizzza_management_app.pizza.repository.PizzaRepository;
import shengov.bg.pizzza_management_app.size.model.SizeEntity;
import shengov.bg.pizzza_management_app.size.repository.SizeRepository;

@ExtendWith(MockitoExtension.class)
class PizzaServiceImplTest {

  private static final String TEST_NAME = "Margherita";
  private static final String TEST_DESCRIPTION = "Classic Italian pizza";

  @Mock private PizzaRepository pizzaRepository;
  @Mock private IngredientRepository ingredientRepository;
  @Mock private SizeRepository sizeRepository;
  @Mock private PizzaMapper pizzaMapper;

  @InjectMocks private PizzaServiceImpl toTest;

  private PizzaEntity createTestPizzaEntity() {
    PizzaEntity pizza = new PizzaEntity();
    ReflectionTestUtils.setField(pizza, "id", UUID.randomUUID());
    pizza.setName(TEST_NAME);
    pizza.setDescription(TEST_DESCRIPTION);
    return pizza;
  }

  private IngredientEntity createTestIngredient(String name) {
    IngredientEntity ingredient = new IngredientEntity();
    ReflectionTestUtils.setField(ingredient, "id", UUID.randomUUID());
    ingredient.setName(name);
    return ingredient;
  }

  private SizeEntity createTestSize(String name) {
    SizeEntity size = new SizeEntity();
    ReflectionTestUtils.setField(size, "id", UUID.randomUUID());
    size.setName(name);
    return size;
  }

  private PizzaRequest createTestRequest(List<UUID> ingredientIds, List<PizzaSizeRequest> sizes) {
    return new PizzaRequest(TEST_NAME, TEST_DESCRIPTION, ingredientIds, sizes);
  }

  private PizzaResponse createTestResponse(PizzaEntity entity) {
    return new PizzaResponse(
        entity.getId(), entity.getName(), entity.getDescription(), List.of(), List.of());
  }

  @Nested
  class Create {

    @Test
    void create_ShouldReturnPizzaResponse_WhenNameIsUnique() {
      IngredientEntity ingredient = createTestIngredient("Tomato");
      SizeEntity size = createTestSize("Medium");
      PizzaSizeRequest sizeRequest = new PizzaSizeRequest(size.getId(), BigDecimal.valueOf(9.99));
      PizzaRequest request = createTestRequest(List.of(ingredient.getId()), List.of(sizeRequest));
      PizzaEntity entity = createTestPizzaEntity();
      PizzaResponse expectedResponse = createTestResponse(entity);

      when(pizzaRepository.existsByNameIgnoreCase(TEST_NAME)).thenReturn(false);
      when(pizzaMapper.requestToEntity(request)).thenReturn(entity);
      when(ingredientRepository.findAllById(List.of(ingredient.getId())))
          .thenReturn(List.of(ingredient));
      when(sizeRepository.findAllById(List.of(size.getId()))).thenReturn(List.of(size));
      when(pizzaRepository.save(entity)).thenReturn(entity);
      when(pizzaMapper.entityToResponse(entity)).thenReturn(expectedResponse);

      PizzaResponse response = toTest.create(request);

      assertNotNull(response);
      assertEquals(expectedResponse.id(), response.id());
      assertEquals(expectedResponse.name(), response.name());
      verify(pizzaRepository).save(entity);
    }

    @Test
    void create_ShouldThrowPizzaAlreadyExistsException_WhenNameExists() {
      IngredientEntity ingredient = createTestIngredient("Tomato");
      SizeEntity size = createTestSize("Medium");
      PizzaSizeRequest sizeRequest = new PizzaSizeRequest(size.getId(), BigDecimal.valueOf(9.99));
      PizzaRequest request = createTestRequest(List.of(ingredient.getId()), List.of(sizeRequest));

      when(pizzaRepository.existsByNameIgnoreCase(TEST_NAME)).thenReturn(true);

      assertThrows(PizzaAlreadyExistsException.class, () -> toTest.create(request));

      verify(pizzaRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowResourceNotFoundException_WhenIngredientNotFound() {
      UUID missingIngredientId = UUID.randomUUID();
      SizeEntity size = createTestSize("Medium");
      PizzaSizeRequest sizeRequest = new PizzaSizeRequest(size.getId(), BigDecimal.valueOf(9.99));
      PizzaRequest request = createTestRequest(List.of(missingIngredientId), List.of(sizeRequest));
      PizzaEntity entity = createTestPizzaEntity();

      when(pizzaRepository.existsByNameIgnoreCase(TEST_NAME)).thenReturn(false);
      when(pizzaMapper.requestToEntity(request)).thenReturn(entity);
      when(ingredientRepository.findAllById(List.of(missingIngredientId))).thenReturn(List.of());

      ResourceNotFoundException exception =
          assertThrows(ResourceNotFoundException.class, () -> toTest.create(request));

      assertTrue(exception.getMessage().contains("Ingredients"));
      verify(pizzaRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowResourceNotFoundException_WhenSizeNotFound() {
      IngredientEntity ingredient = createTestIngredient("Tomato");
      UUID missingSizeId = UUID.randomUUID();
      PizzaSizeRequest sizeRequest = new PizzaSizeRequest(missingSizeId, BigDecimal.valueOf(9.99));
      PizzaRequest request = createTestRequest(List.of(ingredient.getId()), List.of(sizeRequest));
      PizzaEntity entity = createTestPizzaEntity();

      when(pizzaRepository.existsByNameIgnoreCase(TEST_NAME)).thenReturn(false);
      when(pizzaMapper.requestToEntity(request)).thenReturn(entity);
      when(ingredientRepository.findAllById(List.of(ingredient.getId())))
          .thenReturn(List.of(ingredient));
      when(sizeRepository.findAllById(List.of(missingSizeId))).thenReturn(List.of());

      ResourceNotFoundException exception =
          assertThrows(ResourceNotFoundException.class, () -> toTest.create(request));

      assertTrue(exception.getMessage().contains("Sizes"));
      verify(pizzaRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowIllegalArgumentException_WhenDuplicateIngredientIds() {
      IngredientEntity ingredient = createTestIngredient("Tomato");
      UUID duplicateId = ingredient.getId();
      SizeEntity size = createTestSize("Medium");
      PizzaSizeRequest sizeRequest = new PizzaSizeRequest(size.getId(), BigDecimal.valueOf(9.99));
      PizzaRequest request =
          createTestRequest(List.of(duplicateId, duplicateId), List.of(sizeRequest));
      PizzaEntity entity = createTestPizzaEntity();

      when(pizzaRepository.existsByNameIgnoreCase(TEST_NAME)).thenReturn(false);
      when(pizzaMapper.requestToEntity(request)).thenReturn(entity);

      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> toTest.create(request));

      assertTrue(exception.getMessage().contains("Duplicate"));
      verify(pizzaRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowIllegalArgumentException_WhenDuplicateSizeIds() {
      IngredientEntity ingredient = createTestIngredient("Tomato");
      SizeEntity size = createTestSize("Medium");
      PizzaSizeRequest sizeRequest1 = new PizzaSizeRequest(size.getId(), BigDecimal.valueOf(9.99));
      PizzaSizeRequest sizeRequest2 = new PizzaSizeRequest(size.getId(), BigDecimal.valueOf(12.99));
      PizzaRequest request =
          createTestRequest(List.of(ingredient.getId()), List.of(sizeRequest1, sizeRequest2));
      PizzaEntity entity = createTestPizzaEntity();

      when(pizzaRepository.existsByNameIgnoreCase(TEST_NAME)).thenReturn(false);
      when(pizzaMapper.requestToEntity(request)).thenReturn(entity);
      when(ingredientRepository.findAllById(List.of(ingredient.getId())))
          .thenReturn(List.of(ingredient));

      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> toTest.create(request));

      assertTrue(exception.getMessage().contains("Duplicate"));
      verify(pizzaRepository, never()).save(any());
    }
  }

  @Nested
  class Update {

    @Test
    void update_ShouldReturnUpdatedPizza_WhenExists() {
      PizzaEntity existingPizza = createTestPizzaEntity();
      IngredientEntity ingredient = createTestIngredient("Mozzarella");
      SizeEntity size = createTestSize("Large");
      PizzaSizeRequest sizeRequest = new PizzaSizeRequest(size.getId(), BigDecimal.valueOf(14.99));
      String newName = "Pepperoni";
      PizzaRequest request =
          new PizzaRequest(
              newName, "Spicy pepperoni pizza", List.of(ingredient.getId()), List.of(sizeRequest));
      PizzaResponse expectedResponse =
          new PizzaResponse(
              existingPizza.getId(), newName, "Spicy pepperoni pizza", List.of(), List.of());

      when(pizzaRepository.findWithDetailsById(existingPizza.getId()))
          .thenReturn(Optional.of(existingPizza));
      when(pizzaRepository.existsByNameIgnoreCase(newName)).thenReturn(false);
      when(ingredientRepository.findAllById(List.of(ingredient.getId())))
          .thenReturn(List.of(ingredient));
      when(sizeRepository.findAllById(List.of(size.getId()))).thenReturn(List.of(size));
      when(pizzaMapper.entityToResponse(existingPizza)).thenReturn(expectedResponse);

      PizzaResponse response = toTest.update(existingPizza.getId(), request);

      assertNotNull(response);
      assertEquals(newName, response.name());
      assertEquals(newName, existingPizza.getName());
      verify(pizzaRepository, never()).save(any());
    }

    @Test
    void update_ShouldNotValidateName_WhenNameUnchanged() {
      PizzaEntity existingPizza = createTestPizzaEntity();
      IngredientEntity ingredient = createTestIngredient("Tomato");
      SizeEntity size = createTestSize("Small");
      PizzaSizeRequest sizeRequest = new PizzaSizeRequest(size.getId(), BigDecimal.valueOf(7.99));
      PizzaRequest request = createTestRequest(List.of(ingredient.getId()), List.of(sizeRequest));
      PizzaResponse expectedResponse = createTestResponse(existingPizza);

      when(pizzaRepository.findWithDetailsById(existingPizza.getId()))
          .thenReturn(Optional.of(existingPizza));
      when(ingredientRepository.findAllById(List.of(ingredient.getId())))
          .thenReturn(List.of(ingredient));
      when(sizeRepository.findAllById(List.of(size.getId()))).thenReturn(List.of(size));
      when(pizzaMapper.entityToResponse(existingPizza)).thenReturn(expectedResponse);

      PizzaResponse response = toTest.update(existingPizza.getId(), request);

      assertNotNull(response);
      verify(pizzaRepository, never()).existsByNameIgnoreCase(any());
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenPizzaNotFound() {
      UUID notExistId = UUID.randomUUID();
      PizzaRequest request =
          createTestRequest(
              List.of(UUID.randomUUID()),
              List.of(new PizzaSizeRequest(UUID.randomUUID(), BigDecimal.valueOf(9.99))));

      when(pizzaRepository.findWithDetailsById(notExistId)).thenReturn(Optional.empty());

      ResourceNotFoundException exception =
          assertThrows(ResourceNotFoundException.class, () -> toTest.update(notExistId, request));

      assertTrue(exception.getMessage().contains("Pizza"));
      assertTrue(exception.getMessage().contains(notExistId.toString()));
    }

    @Test
    void update_ShouldThrowPizzaAlreadyExistsException_WhenNewNameExists() {
      PizzaEntity existingPizza = createTestPizzaEntity();
      String conflictingName = "Hawaiian";
      PizzaRequest request =
          new PizzaRequest(
              conflictingName,
              TEST_DESCRIPTION,
              List.of(UUID.randomUUID()),
              List.of(new PizzaSizeRequest(UUID.randomUUID(), BigDecimal.valueOf(9.99))));

      when(pizzaRepository.findWithDetailsById(existingPizza.getId()))
          .thenReturn(Optional.of(existingPizza));
      when(pizzaRepository.existsByNameIgnoreCase(conflictingName)).thenReturn(true);

      assertThrows(
          PizzaAlreadyExistsException.class, () -> toTest.update(existingPizza.getId(), request));
    }

    @Test
    void update_ShouldUpdateExistingSizePrice_WhenSizeAlreadyAssigned() {
      PizzaEntity existingPizza = createTestPizzaEntity();
      SizeEntity size = createTestSize("Medium");
      PizzaSize existingPizzaSize = new PizzaSize(existingPizza, size, BigDecimal.valueOf(9.99));
      existingPizza.addSize(existingPizzaSize);

      IngredientEntity ingredient = createTestIngredient("Tomato");
      BigDecimal newPrice = BigDecimal.valueOf(12.99);
      PizzaSizeRequest sizeRequest = new PizzaSizeRequest(size.getId(), newPrice);
      PizzaRequest request = createTestRequest(List.of(ingredient.getId()), List.of(sizeRequest));
      PizzaResponse expectedResponse = createTestResponse(existingPizza);

      when(pizzaRepository.findWithDetailsById(existingPizza.getId()))
          .thenReturn(Optional.of(existingPizza));
      when(ingredientRepository.findAllById(List.of(ingredient.getId())))
          .thenReturn(List.of(ingredient));
      when(sizeRepository.findAllById(List.of(size.getId()))).thenReturn(List.of(size));
      when(pizzaMapper.entityToResponse(existingPizza)).thenReturn(expectedResponse);

      toTest.update(existingPizza.getId(), request);

      assertEquals(newPrice, existingPizzaSize.getPrice());
    }
  }

  @Nested
  class Delete {

    @Test
    void delete_ShouldDeletePizza_WhenExists() {
      PizzaEntity pizza = createTestPizzaEntity();

      when(pizzaRepository.findById(pizza.getId())).thenReturn(Optional.of(pizza));

      toTest.delete(pizza.getId());

      verify(pizzaRepository).delete(pizza);
    }

    @Test
    void delete_ShouldThrowResourceNotFoundException_WhenNotExists() {
      UUID notExistId = UUID.randomUUID();

      when(pizzaRepository.findById(notExistId)).thenReturn(Optional.empty());

      assertThrows(ResourceNotFoundException.class, () -> toTest.delete(notExistId));

      verify(pizzaRepository, never()).delete(any(PizzaEntity.class));
    }
  }

  @Nested
  class GetById {

    @Test
    void getById_ShouldReturnPizza_WhenExists() {
      PizzaEntity pizza = createTestPizzaEntity();
      PizzaResponse expectedResponse = createTestResponse(pizza);

      when(pizzaRepository.findWithDetailsById(pizza.getId())).thenReturn(Optional.of(pizza));
      when(pizzaMapper.entityToResponse(pizza)).thenReturn(expectedResponse);

      PizzaResponse response = toTest.getById(pizza.getId());

      assertEquals(expectedResponse.id(), response.id());
      assertEquals(expectedResponse.name(), response.name());
    }

    @Test
    void getById_ShouldThrowResourceNotFoundException_WhenNotExists() {
      UUID notExistId = UUID.randomUUID();

      when(pizzaRepository.findWithDetailsById(notExistId)).thenReturn(Optional.empty());

      ResourceNotFoundException exception =
          assertThrows(ResourceNotFoundException.class, () -> toTest.getById(notExistId));

      assertTrue(exception.getMessage().contains("Pizza"));
      assertTrue(exception.getMessage().contains(notExistId.toString()));
    }
  }

  @Nested
  class GetAll {

    @Test
    void getAll_ShouldReturnCorrectResult() {
      PizzaEntity pizza1 = createTestPizzaEntity();
      PizzaEntity pizza2 = createTestPizzaEntity();
      ReflectionTestUtils.setField(pizza2, "id", UUID.randomUUID());
      pizza2.setName("Pepperoni");
      List<PizzaEntity> pizzas = List.of(pizza1, pizza2);

      PizzaResponse response1 = createTestResponse(pizza1);
      PizzaResponse response2 = createTestResponse(pizza2);

      when(pizzaRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(pizzas));
      when(pizzaMapper.entityToResponse(pizza1)).thenReturn(response1);
      when(pizzaMapper.entityToResponse(pizza2)).thenReturn(response2);

      Page<PizzaResponse> result = toTest.getAll(Pageable.unpaged());

      assertEquals(2, result.getTotalElements());
    }
  }
}
