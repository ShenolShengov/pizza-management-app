package shengov.bg.pizzza_management_app.pizza.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shengov.bg.pizzza_management_app.testutils.PizzaTestUtils.createSizeRequest;
import static shengov.bg.pizzza_management_app.testutils.PizzaTestUtils.createTestPizzaRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import shengov.bg.pizzza_management_app.config.BaseIntegrationTest;
import shengov.bg.pizzza_management_app.ingredient.model.IngredientEntity;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaRequest;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaSizeRequest;
import shengov.bg.pizzza_management_app.pizza.model.PizzaEntity;
import shengov.bg.pizzza_management_app.pizza.repository.PizzaRepository;
import shengov.bg.pizzza_management_app.size.model.SizeEntity;
import shengov.bg.pizzza_management_app.testutils.MockMvcTestUtils;
import shengov.bg.pizzza_management_app.testutils.PizzaTestUtils;

@DisplayName("Pizza controller integration tests")
class PizzaControllerIT extends BaseIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private MockMvcTestUtils mockMvcTestUtils;
  @Autowired private PizzaRepository pizzaRepository;
  @Autowired private PizzaTestUtils pizzaTestUtils;

  private static final String PIZZA_ENDPOINT = "/api/pizzas";
  private static final String PIZZA_BY_ID_ENDPOINT = "/api/pizzas/%s";
  private static final String TEST_NAME = "Margherita";
  private static final String TEST_DESCRIPTION = "Classic Italian pizza";

  private PizzaRequest validRequest(List<UUID> ingredientIds, List<PizzaSizeRequest> sizes) {
    return createTestPizzaRequest(TEST_NAME, TEST_DESCRIPTION, ingredientIds, sizes);
  }

  @Nested
  class CreateTests {

    @Test
    @DisplayName("POST api/pizzas -> 401 when user is not authenticated")
    void create_ShouldReturnUnauthorized_WhenIsNotAuthenticated() throws Exception {
      IngredientEntity ingredient = pizzaTestUtils.saveIngredient("Tomato");
      SizeEntity size = pizzaTestUtils.saveSize("Medium");
      PizzaRequest request =
          validRequest(
              List.of(ingredient.getId()),
              List.of(createSizeRequest(size.getId(), BigDecimal.valueOf(9.99))));

      mockMvcTestUtils.performPost(PIZZA_ENDPOINT, request).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("POST api/pizzas -> 403 when user is not admin")
    void create_ShouldReturnForbidden_WhenIsNotAdmin() throws Exception {
      IngredientEntity ingredient = pizzaTestUtils.saveIngredient("Tomato");
      SizeEntity size = pizzaTestUtils.saveSize("Medium");
      PizzaRequest request =
          validRequest(
              List.of(ingredient.getId()),
              List.of(createSizeRequest(size.getId(), BigDecimal.valueOf(9.99))));

      mockMvcTestUtils.performPost(PIZZA_ENDPOINT, request).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("POST api/pizzas -> 201 when pizza is created successfully")
    void create_ShouldReturnCreated_WhenIsAdmin() throws Exception {
      IngredientEntity ingredient = pizzaTestUtils.saveIngredient("Tomato");
      SizeEntity size = pizzaTestUtils.saveSize("Medium");
      PizzaRequest request =
          validRequest(
              List.of(ingredient.getId()),
              List.of(createSizeRequest(size.getId(), BigDecimal.valueOf(9.99))));

      mockMvcTestUtils
          .performPost(PIZZA_ENDPOINT, request)
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.name", equalTo(TEST_NAME)))
          .andExpect(jsonPath("$.description", equalTo(TEST_DESCRIPTION)));

      assertTrue(pizzaRepository.existsByNameIgnoreCase(TEST_NAME));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("POST api/pizzas -> 409 when pizza with that name already exists")
    void create_ShouldReturnConflict_WhenPizzaAlreadyExists() throws Exception {
      IngredientEntity ingredient = pizzaTestUtils.saveIngredient("Tomato");
      SizeEntity size = pizzaTestUtils.saveSize("Medium");
      pizzaTestUtils.savePizza(
          TEST_NAME, List.of(ingredient), List.of(size), List.of(BigDecimal.valueOf(9.99)));

      PizzaRequest request =
          validRequest(
              List.of(ingredient.getId()),
              List.of(createSizeRequest(size.getId(), BigDecimal.valueOf(9.99))));

      mockMvcTestUtils
          .performPost(PIZZA_ENDPOINT, request)
          .andExpect(status().isConflict())
          .andExpect(jsonPath("$.status", equalTo(409)))
          .andExpect(jsonPath("$.error", equalTo("Conflict")));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("POST api/pizzas -> 400 when name is blank")
    void create_ShouldReturnBadRequest_WhenNameIsBlank() throws Exception {
      IngredientEntity ingredient = pizzaTestUtils.saveIngredient("Tomato");
      SizeEntity size = pizzaTestUtils.saveSize("Medium");
      PizzaRequest request =
          createTestPizzaRequest(
              "",
              TEST_DESCRIPTION,
              List.of(ingredient.getId()),
              List.of(createSizeRequest(size.getId(), BigDecimal.valueOf(9.99))));

      mockMvcTestUtils
          .performPost(PIZZA_ENDPOINT, request)
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status", equalTo(400)))
          .andExpect(jsonPath("$.error", equalTo("Bad Request")))
          .andExpect(jsonPath("$.errors").isMap())
          .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("POST api/pizzas -> 400 when ingredients list is empty")
    void create_ShouldReturnBadRequest_WhenIngredientsEmpty() throws Exception {
      SizeEntity size = pizzaTestUtils.saveSize("Medium");
      PizzaRequest request =
          createTestPizzaRequest(
              TEST_NAME,
              TEST_DESCRIPTION,
              List.of(),
              List.of(createSizeRequest(size.getId(), BigDecimal.valueOf(9.99))));

      mockMvcTestUtils
          .performPost(PIZZA_ENDPOINT, request)
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.errors.ingredientIds").exists());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("POST api/pizzas -> 400 when sizes list is empty")
    void create_ShouldReturnBadRequest_WhenSizesEmpty() throws Exception {
      IngredientEntity ingredient = pizzaTestUtils.saveIngredient("Tomato");
      PizzaRequest request =
          createTestPizzaRequest(
              TEST_NAME, TEST_DESCRIPTION, List.of(ingredient.getId()), List.of());

      mockMvcTestUtils
          .performPost(PIZZA_ENDPOINT, request)
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.errors.sizes").exists());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("POST api/pizzas -> 404 when ingredient not found")
    void create_ShouldReturnNotFound_WhenIngredientNotFound() throws Exception {
      SizeEntity size = pizzaTestUtils.saveSize("Medium");
      PizzaRequest request =
          createTestPizzaRequest(
              TEST_NAME,
              TEST_DESCRIPTION,
              List.of(UUID.randomUUID()),
              List.of(createSizeRequest(size.getId(), BigDecimal.valueOf(9.99))));

      mockMvcTestUtils.performPost(PIZZA_ENDPOINT, request).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("POST api/pizzas -> 404 when size not found")
    void create_ShouldReturnNotFound_WhenSizeNotFound() throws Exception {
      IngredientEntity ingredient = pizzaTestUtils.saveIngredient("Tomato");
      PizzaRequest request =
          createTestPizzaRequest(
              TEST_NAME,
              TEST_DESCRIPTION,
              List.of(ingredient.getId()),
              List.of(createSizeRequest(UUID.randomUUID(), BigDecimal.valueOf(9.99))));

      mockMvcTestUtils.performPost(PIZZA_ENDPOINT, request).andExpect(status().isNotFound());
    }
  }

  @Nested
  class UpdateTests {

    @Test
    @DisplayName("PUT api/pizzas/{id} -> 401 when user is not authenticated")
    void update_ShouldReturnUnauthorized_WhenIsNotAuthenticated() throws Exception {
      IngredientEntity ingredient = pizzaTestUtils.saveIngredient("Tomato");
      SizeEntity size = pizzaTestUtils.saveSize("Medium");
      PizzaEntity pizza =
          pizzaTestUtils.savePizza(
              TEST_NAME, List.of(ingredient), List.of(size), List.of(BigDecimal.valueOf(9.99)));

      PizzaRequest request =
          validRequest(
              List.of(ingredient.getId()),
              List.of(createSizeRequest(size.getId(), BigDecimal.valueOf(12.99))));

      mockMvcTestUtils
          .performPut(PIZZA_BY_ID_ENDPOINT.formatted(pizza.getId()), request)
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("PUT api/pizzas/{id} -> 403 when user is not admin")
    void update_ShouldReturnForbidden_WhenIsNotAdmin() throws Exception {
      IngredientEntity ingredient = pizzaTestUtils.saveIngredient("Tomato");
      SizeEntity size = pizzaTestUtils.saveSize("Medium");
      PizzaEntity pizza =
          pizzaTestUtils.savePizza(
              TEST_NAME, List.of(ingredient), List.of(size), List.of(BigDecimal.valueOf(9.99)));

      PizzaRequest request =
          validRequest(
              List.of(ingredient.getId()),
              List.of(createSizeRequest(size.getId(), BigDecimal.valueOf(12.99))));

      mockMvcTestUtils
          .performPut(PIZZA_BY_ID_ENDPOINT.formatted(pizza.getId()), request)
          .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("PUT api/pizzas/{id} -> 200 when updated successfully")
    void update_ShouldReturnOk_WhenUpdateSuccessful() throws Exception {
      IngredientEntity ingredient = pizzaTestUtils.saveIngredient("Tomato");
      SizeEntity size = pizzaTestUtils.saveSize("Medium");
      PizzaEntity pizza =
          pizzaTestUtils.savePizza(
              TEST_NAME, List.of(ingredient), List.of(size), List.of(BigDecimal.valueOf(9.99)));

      String updatedName = "Pepperoni";
      PizzaRequest request =
          createTestPizzaRequest(
              updatedName,
              "Spicy pepperoni pizza",
              List.of(ingredient.getId()),
              List.of(createSizeRequest(size.getId(), BigDecimal.valueOf(14.99))));

      mockMvcTestUtils
          .performPut(PIZZA_BY_ID_ENDPOINT.formatted(pizza.getId()), request)
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.name", equalTo(updatedName)));

      assertTrue(pizzaRepository.existsByNameIgnoreCase(updatedName));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("PUT api/pizzas/{id} -> 400 when data is not valid")
    void update_ShouldReturnBadRequest_WhenDataIsNotValid() throws Exception {
      IngredientEntity ingredient = pizzaTestUtils.saveIngredient("Tomato");
      SizeEntity size = pizzaTestUtils.saveSize("Medium");
      PizzaEntity pizza =
          pizzaTestUtils.savePizza(
              TEST_NAME, List.of(ingredient), List.of(size), List.of(BigDecimal.valueOf(9.99)));

      PizzaRequest request =
          createTestPizzaRequest(
              "AB",
              TEST_DESCRIPTION,
              List.of(ingredient.getId()),
              List.of(createSizeRequest(size.getId(), BigDecimal.valueOf(9.99))));

      mockMvcTestUtils
          .performPut(PIZZA_BY_ID_ENDPOINT.formatted(pizza.getId()), request)
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status", equalTo(400)))
          .andExpect(jsonPath("$.error", equalTo("Bad Request")))
          .andExpect(jsonPath("$.errors").isMap())
          .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("PUT api/pizzas/{id} -> 409 when name is already occupied")
    void update_ShouldReturnConflict_WhenNameIsAlreadyOccupied() throws Exception {
      IngredientEntity ingredient = pizzaTestUtils.saveIngredient("Tomato");
      SizeEntity size = pizzaTestUtils.saveSize("Medium");
      PizzaEntity pizza =
          pizzaTestUtils.savePizza(
              TEST_NAME, List.of(ingredient), List.of(size), List.of(BigDecimal.valueOf(9.99)));
      pizzaTestUtils.savePizza(
          "Hawaiian", List.of(ingredient), List.of(size), List.of(BigDecimal.valueOf(11.99)));

      PizzaRequest request =
          createTestPizzaRequest(
              "Hawaiian",
              TEST_DESCRIPTION,
              List.of(ingredient.getId()),
              List.of(createSizeRequest(size.getId(), BigDecimal.valueOf(9.99))));

      mockMvcTestUtils
          .performPut(PIZZA_BY_ID_ENDPOINT.formatted(pizza.getId()), request)
          .andExpect(status().isConflict())
          .andExpect(jsonPath("$.status", equalTo(409)))
          .andExpect(jsonPath("$.error", equalTo("Conflict")));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("PUT api/pizzas/{id} -> 404 when pizza not found")
    void update_ShouldReturnNotFound_WhenPizzaNotFound() throws Exception {
      IngredientEntity ingredient = pizzaTestUtils.saveIngredient("Tomato");
      SizeEntity size = pizzaTestUtils.saveSize("Medium");
      PizzaRequest request =
          validRequest(
              List.of(ingredient.getId()),
              List.of(createSizeRequest(size.getId(), BigDecimal.valueOf(9.99))));

      mockMvcTestUtils
          .performPut(PIZZA_BY_ID_ENDPOINT.formatted(UUID.randomUUID()), request)
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  class GetByIdTests {

    @Test
    @DisplayName("GET api/pizzas/{id} -> 401 when user is not authenticated")
    void getById_ShouldReturnUnauthorized_WhenUserIsNotAuthenticated() throws Exception {
      IngredientEntity ingredient = pizzaTestUtils.saveIngredient("Tomato");
      SizeEntity size = pizzaTestUtils.saveSize("Medium");
      PizzaEntity pizza =
          pizzaTestUtils.savePizza(
              TEST_NAME, List.of(ingredient), List.of(size), List.of(BigDecimal.valueOf(9.99)));

      mockMvcTestUtils
          .performGet(PIZZA_BY_ID_ENDPOINT.formatted(pizza.getId()))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("GET api/pizzas/{id} -> 404 when pizza not found")
    void getById_ShouldReturnNotFound_WhenNotExist() throws Exception {
      mockMvcTestUtils
          .performGet(PIZZA_BY_ID_ENDPOINT.formatted(UUID.randomUUID()))
          .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("GET api/pizzas/{id} -> 200 when pizza exists")
    void getById_ShouldReturnPizza_WhenExist() throws Exception {
      IngredientEntity ingredient = pizzaTestUtils.saveIngredient("Tomato");
      SizeEntity size = pizzaTestUtils.saveSize("Medium");
      PizzaEntity pizza =
          pizzaTestUtils.savePizza(
              TEST_NAME, List.of(ingredient), List.of(size), List.of(BigDecimal.valueOf(9.99)));

      mockMvcTestUtils
          .performGet(PIZZA_BY_ID_ENDPOINT.formatted(pizza.getId()))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id", equalTo(pizza.getId().toString())))
          .andExpect(jsonPath("$.name", equalTo(TEST_NAME)));
    }
  }

  @Nested
  class GetAllTests {

    @Test
    @DisplayName("GET api/pizzas -> 401 when user is not authenticated")
    void getAll_ShouldReturnUnauthorized_WhenUserIsNotAuthenticated() throws Exception {
      mockMvcTestUtils.performGet(PIZZA_ENDPOINT).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("GET api/pizzas -> 200 with correct results")
    void getAll_ShouldReturnCorrectPizzas() throws Exception {
      IngredientEntity ingredient = pizzaTestUtils.saveIngredient("Tomato");
      SizeEntity size = pizzaTestUtils.saveSize("Medium");
      pizzaTestUtils.savePizza(
          "Margherita", List.of(ingredient), List.of(size), List.of(BigDecimal.valueOf(9.99)));
      pizzaTestUtils.savePizza(
          "Pepperoni", List.of(ingredient), List.of(size), List.of(BigDecimal.valueOf(12.99)));

      mockMvc
          .perform(get(PIZZA_ENDPOINT))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content", hasSize(2)))
          .andExpect(jsonPath("$.page.totalElements", equalTo(2)));
    }
  }

  @Nested
  class DeleteTests {

    @Test
    @DisplayName("DELETE api/pizzas/{id} -> 401 when user is not authenticated")
    void delete_ShouldReturnUnauthorized_WhenIsNotAuthenticated() throws Exception {
      IngredientEntity ingredient = pizzaTestUtils.saveIngredient("Tomato");
      SizeEntity size = pizzaTestUtils.saveSize("Medium");
      PizzaEntity pizza =
          pizzaTestUtils.savePizza(
              TEST_NAME, List.of(ingredient), List.of(size), List.of(BigDecimal.valueOf(9.99)));

      mockMvcTestUtils
          .performDelete(PIZZA_BY_ID_ENDPOINT.formatted(pizza.getId()))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE api/pizzas/{id} -> 403 when user is not admin")
    void delete_ShouldReturnForbidden_WhenIsNotAdmin() throws Exception {
      IngredientEntity ingredient = pizzaTestUtils.saveIngredient("Tomato");
      SizeEntity size = pizzaTestUtils.saveSize("Medium");
      PizzaEntity pizza =
          pizzaTestUtils.savePizza(
              TEST_NAME, List.of(ingredient), List.of(size), List.of(BigDecimal.valueOf(9.99)));

      mockMvcTestUtils
          .performDelete(PIZZA_BY_ID_ENDPOINT.formatted(pizza.getId()))
          .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("DELETE api/pizzas/{id} -> 404 when pizza is not found")
    void delete_ShouldReturnNotFound_WhenNotExist() throws Exception {
      mockMvcTestUtils
          .performDelete(PIZZA_BY_ID_ENDPOINT.formatted(UUID.randomUUID()))
          .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("DELETE api/pizzas/{id} -> 204 when pizza is deleted")
    void delete_ShouldDelete_WhenExist() throws Exception {
      IngredientEntity ingredient = pizzaTestUtils.saveIngredient("Tomato");
      SizeEntity size = pizzaTestUtils.saveSize("Medium");
      PizzaEntity pizza =
          pizzaTestUtils.savePizza(
              TEST_NAME, List.of(ingredient), List.of(size), List.of(BigDecimal.valueOf(9.99)));

      mockMvcTestUtils
          .performDelete(PIZZA_BY_ID_ENDPOINT.formatted(pizza.getId()))
          .andExpect(status().isNoContent());

      assertFalse(pizzaRepository.existsByNameIgnoreCase(TEST_NAME));
    }
  }
}
