package shengov.bg.pizzza_management_app.ingredient.contorller;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shengov.bg.pizzza_management_app.testutils.IngredientTestUtils.createTestIngredient;
import static shengov.bg.pizzza_management_app.testutils.IngredientTestUtils.createTestIngredientRequest;
import static shengov.bg.pizzza_management_app.testutils.ObjectMapperTestUtils.toJson;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import shengov.bg.pizzza_management_app.config.BaseIntegrationTest;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientRequest;
import shengov.bg.pizzza_management_app.ingredient.repository.IngredientRepository;
import shengov.bg.pizzza_management_app.testutils.MockMvcTestUtils;

class IngredientControllerIT extends BaseIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private MockMvcTestUtils mockMvcTestUtils;
  @Autowired private IngredientRepository ingredientRepository;

  private static final String INGREDIENT_ENDPOINT = "/api/ingredients";
  private static final String TEST_NAME = "Cheese";
  private static final String NOT_VALID_NAME = "T";

  @Test
  @DisplayName("POST api/ingredients -> 401 when user is not authenticated")
  void create_ShouldReturnUnauthorized_WhenIsNotAuthenticated() throws Exception {
    IngredientRequest request = createTestIngredientRequest(TEST_NAME);
    mockMvc
        .perform(
            post(INGREDIENT_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toJson(request)))
        .andExpect(status().isUnauthorized());
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
    ingredientRepository.save(createTestIngredient(request));

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
    ingredientRepository.save(createTestIngredient(request));

    mockMvcTestUtils
        .performPost(INGREDIENT_ENDPOINT, request)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", equalTo(400)))
        .andExpect(jsonPath("$.error", equalTo("Bad Request")))
        .andExpect(jsonPath("$.errors").isMap())
        .andExpect(jsonPath("$.errors.name").exists());
  }
}
