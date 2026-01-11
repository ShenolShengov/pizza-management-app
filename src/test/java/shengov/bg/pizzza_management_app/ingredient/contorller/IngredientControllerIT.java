package shengov.bg.pizzza_management_app.ingredient.contorller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shengov.bg.pizzza_management_app.testutils.IngredientTestUtils.createTestIngredientRequest;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import shengov.bg.pizzza_management_app.config.BaseIntegrationTest;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientRequest;
import shengov.bg.pizzza_management_app.ingredient.model.Ingredient;
import shengov.bg.pizzza_management_app.ingredient.repository.IngredientRepository;
import shengov.bg.pizzza_management_app.testutils.IngredientTestUtils;
import shengov.bg.pizzza_management_app.testutils.MockMvcTestUtils;

class IngredientControllerIT extends BaseIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private MockMvcTestUtils mockMvcTestUtils;
  @Autowired private IngredientRepository ingredientRepository;
  @Autowired private IngredientTestUtils ingredientTestUtils;

  private static final String INGREDIENT_ENDPOINT = "/api/ingredients";
  private static final String INGREDIENT_BY_ID_ENDPOINT = "/api/ingredients/%s";
  private static final String TEST_NAME = "Cheese";
  private static final String TEST_UPDATE_NAME = "Cheese";
  private static final String NOT_VALID_NAME = "T";

  @Test
  @DisplayName("POST api/ingredients -> 401 when user is not authenticated")
  void create_ShouldReturnUnauthorized_WhenIsNotAuthenticated() throws Exception {
    IngredientRequest request = createTestIngredientRequest(TEST_NAME);
    mockMvcTestUtils.performPost(INGREDIENT_ENDPOINT, request).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser
  @DisplayName("POST api/ingredients -> 403 when user is not admin")
  void create_ShouldReturnForbidden_WhenIsNotAdmin() throws Exception {
    IngredientRequest request = createTestIngredientRequest(TEST_NAME);
    mockMvcTestUtils.performPost(INGREDIENT_ENDPOINT, request).andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  @DisplayName("POST api/ingredients -> 201 when ingredient is created successfully")
  void create_ShouldReturnCreated_WhenIsAdmin() throws Exception {
    IngredientRequest request = createTestIngredientRequest(TEST_NAME);
    mockMvcTestUtils
        .performPost(INGREDIENT_ENDPOINT, request)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name", equalTo(request.name())));
    assertTrue(
        ingredientRepository.findAll().stream()
            .anyMatch(i -> i.getName().equalsIgnoreCase(request.name())));
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  @DisplayName("POST api/ingredients -> 409 when ingredient with that name already exist")
  void create_ShouldReturnConflict_WhenIngredientAlreadyExist() throws Exception {
    IngredientRequest request = createTestIngredientRequest(TEST_NAME);
    ingredientTestUtils.saveTestIngredient(createTestIngredientRequest(TEST_NAME));

    mockMvcTestUtils
        .performPost(INGREDIENT_ENDPOINT, request)
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status", equalTo(409)))
        .andExpect(jsonPath("$.error", equalTo("Conflict")));
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  @DisplayName("POST api/ingredients -> 400 when data is not valid")
  void create_ShouldReturnBadRequest_WhenDataIsNotValid() throws Exception {
    IngredientRequest request = createTestIngredientRequest(NOT_VALID_NAME);

    mockMvcTestUtils
        .performPost(INGREDIENT_ENDPOINT, request)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", equalTo(400)))
        .andExpect(jsonPath("$.error", equalTo("Bad Request")))
        .andExpect(jsonPath("$.errors").isMap())
        .andExpect(jsonPath("$.errors.name").exists());
  }

  @Test
  @DisplayName("PUT api/ingredients/{id} -> 401 when user is not authenticated")
  void update_ShouldReturnUnauthorized_WhenIsNotAuthenticated() throws Exception {
    IngredientRequest request = createTestIngredientRequest(TEST_NAME);
    Ingredient ingredient = ingredientTestUtils.saveTestIngredient(request);
    mockMvcTestUtils
        .performPut(
            INGREDIENT_BY_ID_ENDPOINT.formatted(ingredient.getId()),
            createTestIngredientRequest(TEST_UPDATE_NAME))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser
  @DisplayName("PUT api/ingredients/{id} -> 403 when user is not admin")
  void update_ShouldReturnForbidden_WhenIsNotAdmin() throws Exception {
    IngredientRequest request = createTestIngredientRequest(TEST_NAME);
    Ingredient ingredient = ingredientTestUtils.saveTestIngredient(request);
    mockMvcTestUtils
        .performPut(
            INGREDIENT_BY_ID_ENDPOINT.formatted(ingredient.getId()),
            createTestIngredientRequest(TEST_UPDATE_NAME))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  @DisplayName("PUT api/ingredients/{id} -> 400 when user data is not valid")
  void update_ShouldReturnBadRequest_WhenDataIsNotValid() throws Exception {
    final String INVALID_NAME = "m";
    IngredientRequest request = createTestIngredientRequest(TEST_NAME);
    Ingredient ingredient = ingredientTestUtils.saveTestIngredient(request);
    mockMvcTestUtils
        .performPut(
            INGREDIENT_BY_ID_ENDPOINT.formatted(ingredient.getId()),
            createTestIngredientRequest(INVALID_NAME))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", equalTo(400)))
        .andExpect(jsonPath("$.error", equalTo("Bad Request")))
        .andExpect(jsonPath("$.errors").isMap())
        .andExpect(jsonPath("$.errors.name").exists());
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  @DisplayName("PUT api/ingredients/{id} -> 409 when name is already occupied")
  void update_ShouldReturnConflict_WhenNameIsAlreadyOccupied() throws Exception {
    final String UPDATED_NAME = "Bread";
    IngredientRequest request = createTestIngredientRequest(TEST_NAME);
    IngredientRequest updateRequest = createTestIngredientRequest(UPDATED_NAME);
    Ingredient ingredient = ingredientTestUtils.saveTestIngredient(request);
    ingredientTestUtils.saveTestIngredient(updateRequest);
    mockMvcTestUtils
        .performPut(INGREDIENT_BY_ID_ENDPOINT.formatted(ingredient.getId()), updateRequest)
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status", equalTo(409)))
        .andExpect(jsonPath("$.error", equalTo("Conflict")));
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  @DisplayName("PUT api/ingredients/{id} -> 200 when is updated successfully")
  void update_ShouldReturnOkay_WhenUpdateSuccessfully() throws Exception {
    final String UPDATED_NAME = "Bread";
    IngredientRequest request = createTestIngredientRequest(TEST_NAME);
    IngredientRequest updateRequest = createTestIngredientRequest(UPDATED_NAME);
    Ingredient ingredient = ingredientTestUtils.saveTestIngredient(request);
    mockMvcTestUtils
        .performPut(INGREDIENT_BY_ID_ENDPOINT.formatted(ingredient.getId()), updateRequest)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", equalTo(UPDATED_NAME)));
    ingredientRepository.existsByNameIgnoreCase(UPDATED_NAME);
  }

  @Test
  @WithMockUser
  @DisplayName("GET api/ingredients/{id} -> 404 when ingredient not exist")
  void getById_ShouldReturnNotFound_WhenNotExist() throws Exception {
    mockMvcTestUtils
        .performGet(INGREDIENT_BY_ID_ENDPOINT.formatted(UUID.randomUUID().toString()))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser
  @DisplayName("GET api/ingredients/{id} -> 200 when ingredient exist")
  void getById_ShouldReturnIngredient_WhenExist() throws Exception {
    Ingredient ingredient =
        ingredientTestUtils.saveTestIngredient(createTestIngredientRequest(TEST_NAME));
    mockMvcTestUtils
        .performGet(INGREDIENT_BY_ID_ENDPOINT.formatted(ingredient.getId().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", equalTo(ingredient.getId().toString())))
        .andExpect(jsonPath("$.name", equalTo(ingredient.getName())));
  }

  @Test
  @WithMockUser
  @DisplayName("GET api/ingredients")
  void getAll_ShouldReturnCorrectIngredients() throws Exception {
    String[] ingredients = {"Cheese", "Bread", "Tomato", "Cucumber", "Onion"};
    ingredientTestUtils.saveIngredients(ingredients);
    mockMvc
        .perform(get(INGREDIENT_ENDPOINT))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(ingredients.length)))
        .andExpect(jsonPath("$.page.totalElements", equalTo(ingredients.length)));
  }

  @Test
  @DisplayName("DELETE api/ingredients/{id} -> 401 when user is not authenticated")
  void delete_ShouldReturnUnauthorized_WhenIsNotAuthenticated() throws Exception {
    IngredientRequest request = createTestIngredientRequest(TEST_NAME);
    Ingredient ingredient = ingredientTestUtils.saveTestIngredient(request);
    mockMvcTestUtils
        .performDelete(INGREDIENT_BY_ID_ENDPOINT.formatted(ingredient.getId()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser
  @DisplayName("DELETE api/ingredients/{id} -> 403 when user is not admin")
  void delete_ShouldReturnForbidden_WhenIsNotAdmin() throws Exception {
    IngredientRequest request = createTestIngredientRequest(TEST_NAME);
    Ingredient ingredient = ingredientTestUtils.saveTestIngredient(request);
    mockMvcTestUtils
        .performDelete(INGREDIENT_BY_ID_ENDPOINT.formatted(ingredient.getId()))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  @DisplayName("DELETE api/ingredients/{id} -> 404 when ingredient is not found")
  void delete_ShouldReturnNotFound_WhenNotExist() throws Exception {
    mockMvcTestUtils
        .performDelete(INGREDIENT_BY_ID_ENDPOINT.formatted(UUID.randomUUID().toString()))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  @DisplayName("DELETE api/ingredients/{id} -> 404 when ingredient is not found")
  void delete_ShouldDelete_WhenExist() throws Exception {
    IngredientRequest request = createTestIngredientRequest(TEST_NAME);
    Ingredient ingredient = ingredientTestUtils.saveTestIngredient(request);
    mockMvcTestUtils
        .performDelete(INGREDIENT_BY_ID_ENDPOINT.formatted(ingredient.getId()))
        .andExpect(status().isNoContent());
    assertFalse(ingredientRepository.existsByNameIgnoreCase(ingredient.getName()));
  }
}
