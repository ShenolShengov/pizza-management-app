package shengov.bg.pizzza_management_app.size.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shengov.bg.pizzza_management_app.testutils.SizeTestUtils.createTestSizeRequest;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import shengov.bg.pizzza_management_app.config.BaseIntegrationTest;
import shengov.bg.pizzza_management_app.size.dto.SizeRequest;
import shengov.bg.pizzza_management_app.size.model.SizeEntity;
import shengov.bg.pizzza_management_app.size.repository.SizeRepository;
import shengov.bg.pizzza_management_app.testutils.MockMvcTestUtils;
import shengov.bg.pizzza_management_app.testutils.SizeTestUtils;

@DisplayName("Size controller integration tests")
class SizeControllerIT extends BaseIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private MockMvcTestUtils mockMvcTestUtils;
  @Autowired private SizeRepository sizeRepository;
  @Autowired private SizeTestUtils sizeTestUtils;

  private static final String SIZE_ENDPOINT = "/api/sizes";
  private static final String SIZE_BY_ID_ENDPOINT = "/api/sizes/%s";
  private static final String TEST_NAME = "Large";
  private static final String TEST_UPDATE_NAME = "Extra Large";
  private static final String NOT_VALID_NAME = "A".repeat(16);

  @Nested
  class CreateTests {

    @Test
    @DisplayName("POST /api/sizes -> 401 when user is not authenticated")
    void create_ShouldReturnUnauthorized_WhenIsNotAuthenticated() throws Exception {
      SizeRequest request = createTestSizeRequest(TEST_NAME);
      mockMvcTestUtils.performPost(SIZE_ENDPOINT, request).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/sizes -> 403 when user is not admin")
    void create_ShouldReturnForbidden_WhenIsNotAdmin() throws Exception {
      SizeRequest request = createTestSizeRequest(TEST_NAME);
      mockMvcTestUtils.performPost(SIZE_ENDPOINT, request).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("POST /api/sizes -> 201 when size is created successfully")
    void create_ShouldReturnCreated_WhenIsAdmin() throws Exception {
      SizeRequest request = createTestSizeRequest(TEST_NAME);
      mockMvcTestUtils
          .performPost(SIZE_ENDPOINT, request)
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.name", equalTo(request.name())));
      assertTrue(
          sizeRepository.findAll().stream()
              .anyMatch(s -> s.getName().equalsIgnoreCase(request.name())));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("POST /api/sizes -> 409 when size with that name already exists")
    void create_ShouldReturnConflict_WhenSizeAlreadyExists() throws Exception {
      SizeRequest request = createTestSizeRequest(TEST_NAME);
      sizeTestUtils.saveTestSize(createTestSizeRequest(TEST_NAME));

      mockMvcTestUtils
          .performPost(SIZE_ENDPOINT, request)
          .andExpect(status().isConflict())
          .andExpect(jsonPath("$.status", equalTo(409)))
          .andExpect(jsonPath("$.error", equalTo("Conflict")));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("POST /api/sizes -> 400 when data is not valid")
    void create_ShouldReturnBadRequest_WhenDataIsNotValid() throws Exception {
      SizeRequest request = createTestSizeRequest(NOT_VALID_NAME);

      mockMvcTestUtils
          .performPost(SIZE_ENDPOINT, request)
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status", equalTo(400)))
          .andExpect(jsonPath("$.error", equalTo("Bad Request")))
          .andExpect(jsonPath("$.errors").isMap())
          .andExpect(jsonPath("$.errors.name").exists());
    }
  }

  @Nested
  class UpdateTests {

    @Test
    @DisplayName("PUT /api/sizes/{id} -> 401 when user is not authenticated")
    void update_ShouldReturnUnauthorized_WhenIsNotAuthenticated() throws Exception {
      SizeEntity size = sizeTestUtils.saveTestSize(createTestSizeRequest(TEST_NAME));
      mockMvcTestUtils
          .performPut(
              SIZE_BY_ID_ENDPOINT.formatted(size.getId()), createTestSizeRequest(TEST_UPDATE_NAME))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/sizes/{id} -> 403 when user is not admin")
    void update_ShouldReturnForbidden_WhenIsNotAdmin() throws Exception {
      SizeEntity size = sizeTestUtils.saveTestSize(createTestSizeRequest(TEST_NAME));
      mockMvcTestUtils
          .performPut(
              SIZE_BY_ID_ENDPOINT.formatted(size.getId()), createTestSizeRequest(TEST_UPDATE_NAME))
          .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("PUT /api/sizes/{id} -> 400 when data is not valid")
    void update_ShouldReturnBadRequest_WhenDataIsNotValid() throws Exception {
      SizeEntity size = sizeTestUtils.saveTestSize(createTestSizeRequest(TEST_NAME));
      mockMvcTestUtils
          .performPut(SIZE_BY_ID_ENDPOINT.formatted(size.getId()), createTestSizeRequest(""))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status", equalTo(400)))
          .andExpect(jsonPath("$.error", equalTo("Bad Request")))
          .andExpect(jsonPath("$.errors").isMap())
          .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("PUT /api/sizes/{id} -> 404 when size does not exist")
    void update_ShouldReturnNotFound_WhenSizeNotExist() throws Exception {
      mockMvcTestUtils
          .performPut(
              SIZE_BY_ID_ENDPOINT.formatted(UUID.randomUUID()),
              createTestSizeRequest(TEST_UPDATE_NAME))
          .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("PUT /api/sizes/{id} -> 409 when name is already occupied")
    void update_ShouldReturnConflict_WhenNameIsAlreadyOccupied() throws Exception {
      SizeEntity size = sizeTestUtils.saveTestSize(createTestSizeRequest(TEST_NAME));
      sizeTestUtils.saveTestSize(createTestSizeRequest(TEST_UPDATE_NAME));

      mockMvcTestUtils
          .performPut(
              SIZE_BY_ID_ENDPOINT.formatted(size.getId()), createTestSizeRequest(TEST_UPDATE_NAME))
          .andExpect(status().isConflict())
          .andExpect(jsonPath("$.status", equalTo(409)))
          .andExpect(jsonPath("$.error", equalTo("Conflict")));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("PUT /api/sizes/{id} -> 200 when updated successfully")
    void update_ShouldReturnOk_WhenUpdateSuccessfully() throws Exception {
      SizeEntity size = sizeTestUtils.saveTestSize(createTestSizeRequest(TEST_NAME));

      mockMvcTestUtils
          .performPut(
              SIZE_BY_ID_ENDPOINT.formatted(size.getId()), createTestSizeRequest(TEST_UPDATE_NAME))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.name", equalTo(TEST_UPDATE_NAME)));
    }
  }

  @Nested
  class GetByIdTests {

    @Test
    @DisplayName("GET /api/sizes/{id} -> 401 when user is not authenticated")
    void getById_ShouldReturnUnauthorized_WhenUserIsNotAuthenticated() throws Exception {
      SizeEntity size = sizeTestUtils.saveTestSize(createTestSizeRequest(TEST_NAME));
      mockMvcTestUtils
          .performGet(SIZE_BY_ID_ENDPOINT.formatted(size.getId()))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/sizes/{id} -> 404 when size does not exist")
    void getById_ShouldReturnNotFound_WhenNotExist() throws Exception {
      mockMvcTestUtils
          .performGet(SIZE_BY_ID_ENDPOINT.formatted(UUID.randomUUID()))
          .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/sizes/{id} -> 200 when size exists")
    void getById_ShouldReturnSize_WhenExists() throws Exception {
      SizeEntity size = sizeTestUtils.saveTestSize(createTestSizeRequest(TEST_NAME));
      mockMvcTestUtils
          .performGet(SIZE_BY_ID_ENDPOINT.formatted(size.getId()))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id", equalTo(size.getId().toString())))
          .andExpect(jsonPath("$.name", equalTo(size.getName())));
    }
  }

  @Nested
  class GetAllTests {

    @Test
    @DisplayName("GET /api/sizes -> 401 when user is not authenticated")
    void getAll_ShouldReturnUnauthorized_WhenUserIsNotAuthenticated() throws Exception {
      mockMvcTestUtils.performGet(SIZE_ENDPOINT).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/sizes -> 200 with correct sizes")
    void getAll_ShouldReturnCorrectSizes() throws Exception {
      String[] names = {"Small", "Medium", "Large", "Extra Large"};
      sizeTestUtils.saveSizes(names);
      mockMvc
          .perform(get(SIZE_ENDPOINT))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content", hasSize(names.length)))
          .andExpect(jsonPath("$.page.totalElements", equalTo(names.length)));
    }
  }

  @Nested
  class DeleteTests {

    @Test
    @DisplayName("DELETE /api/sizes/{id} -> 401 when user is not authenticated")
    void delete_ShouldReturnUnauthorized_WhenIsNotAuthenticated() throws Exception {
      SizeEntity size = sizeTestUtils.saveTestSize(createTestSizeRequest(TEST_NAME));
      mockMvcTestUtils
          .performDelete(SIZE_BY_ID_ENDPOINT.formatted(size.getId()))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/sizes/{id} -> 403 when user is not admin")
    void delete_ShouldReturnForbidden_WhenIsNotAdmin() throws Exception {
      SizeEntity size = sizeTestUtils.saveTestSize(createTestSizeRequest(TEST_NAME));
      mockMvcTestUtils
          .performDelete(SIZE_BY_ID_ENDPOINT.formatted(size.getId()))
          .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("DELETE /api/sizes/{id} -> 404 when size does not exist")
    void delete_ShouldReturnNotFound_WhenNotExist() throws Exception {
      mockMvcTestUtils
          .performDelete(SIZE_BY_ID_ENDPOINT.formatted(UUID.randomUUID()))
          .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("DELETE /api/sizes/{id} -> 204 when deleted successfully")
    void delete_ShouldDelete_WhenExists() throws Exception {
      SizeEntity size = sizeTestUtils.saveTestSize(createTestSizeRequest(TEST_NAME));
      mockMvcTestUtils
          .performDelete(SIZE_BY_ID_ENDPOINT.formatted(size.getId()))
          .andExpect(status().isNoContent());
      assertFalse(sizeRepository.existsByNameIgnoreCase(size.getName()));
    }
  }
}
