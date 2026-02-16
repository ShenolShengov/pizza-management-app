package shengov.bg.pizzza_management_app.pizza.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import shengov.bg.pizzza_management_app.config.BaseIntegrationTest;
import shengov.bg.pizzza_management_app.ingredient.model.IngredientEntity;
import shengov.bg.pizzza_management_app.pizza.model.PizzaEntity;
import shengov.bg.pizzza_management_app.size.model.SizeEntity;
import shengov.bg.pizzza_management_app.testutils.PizzaTestUtils;

@DisplayName("Pizza GraphQL controller integration tests")
class PizzaGraphQlControllerIT extends BaseIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private PizzaTestUtils pizzaTestUtils;

  private HttpGraphQlTester graphQlTester;

  @BeforeEach
  void setUp() {
    graphQlTester =
        HttpGraphQlTester.create(MockMvcWebTestClient.bindTo(mockMvc).baseUrl("/graphql").build());
  }

  @Nested
  class PizzasQueryTests {

    @Test
    @DisplayName("pizzas -> 401 when user is not authenticated")
    void pizzas_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
      mockMvc
          .perform(
              post("/graphql")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{\"query\":\"{ pizzas { content { id } } }\"}"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("pizzas -> returns all pizzas when no filter")
    void pizzas_ShouldReturnAllPizzas_WhenNoFilter() {
      IngredientEntity ingredient = pizzaTestUtils.saveIngredient("Tomato");
      SizeEntity size = pizzaTestUtils.saveSize("Medium");
      pizzaTestUtils.savePizza(
          "Margherita", List.of(ingredient), List.of(size), List.of(BigDecimal.valueOf(9.99)));
      pizzaTestUtils.savePizza(
          "Pepperoni", List.of(ingredient), List.of(size), List.of(BigDecimal.valueOf(12.99)));

      graphQlTester
          .document("{ pizzas { content { id name } totalElements totalPages } }")
          .execute()
          .path("pizzas.totalElements")
          .entity(Integer.class)
          .isEqualTo(2);
    }

    @Test
    @WithMockUser
    @DisplayName("pizzas -> returns matching pizzas when filtered by name")
    void pizzas_ShouldReturnMatchingPizzas_WhenFilteredByName() {
      IngredientEntity ingredient = pizzaTestUtils.saveIngredient("Tomato");
      SizeEntity size = pizzaTestUtils.saveSize("Medium");
      pizzaTestUtils.savePizza(
          "Margherita", List.of(ingredient), List.of(size), List.of(BigDecimal.valueOf(9.99)));
      pizzaTestUtils.savePizza(
          "Pepperoni", List.of(ingredient), List.of(size), List.of(BigDecimal.valueOf(12.99)));

      graphQlTester
          .document(
              """
              { pizzas(filter: { name: "margh" }) {
                content { name }
                totalElements
              } }
              """)
          .execute()
          .path("pizzas.totalElements")
          .entity(Integer.class)
          .isEqualTo(1);

      graphQlTester
          .document(
              """
              { pizzas(filter: { name: "margh" }) {
                content { name }
              } }
              """)
          .execute()
          .path("pizzas.content[0].name")
          .entity(String.class)
          .isEqualTo("Margherita");
    }

    @Test
    @WithMockUser
    @DisplayName("pizzas -> returns only pizzas with all listed ingredients")
    void pizzas_ShouldReturnOnlyPizzasWithAllIngredients_WhenFilteredByIngredientNames() {
      IngredientEntity mozzarella = pizzaTestUtils.saveIngredient("Mozzarella");
      IngredientEntity pepperoni = pizzaTestUtils.saveIngredient("Pepperoni");
      SizeEntity size = pizzaTestUtils.saveSize("Medium");

      pizzaTestUtils.savePizza(
          "Margherita", List.of(mozzarella), List.of(size), List.of(BigDecimal.valueOf(9.99)));
      pizzaTestUtils.savePizza(
          "Pepperoni Pizza",
          List.of(mozzarella, pepperoni),
          List.of(size),
          List.of(BigDecimal.valueOf(12.99)));

      graphQlTester
          .document(
              """
              { pizzas(filter: { ingredientNames: ["Mozzarella", "Pepperoni"] }) {
                content { name }
                totalElements
              } }
              """)
          .execute()
          .path("pizzas.totalElements")
          .entity(Integer.class)
          .isEqualTo(1);

      graphQlTester
          .document(
              """
              { pizzas(filter: { ingredientNames: ["Mozzarella", "Pepperoni"] }) {
                content { name }
              } }
              """)
          .execute()
          .path("pizzas.content[0].name")
          .entity(String.class)
          .isEqualTo("Pepperoni Pizza");
    }

    @Test
    @WithMockUser
    @DisplayName("pizzas -> returns pizzas matching size name and price range")
    void pizzas_ShouldReturnMatchingPizzas_WhenFilteredBySizeAndPrice() {
      IngredientEntity ingredient = pizzaTestUtils.saveIngredient("Tomato");
      SizeEntity large = pizzaTestUtils.saveSize("Large");
      SizeEntity small = pizzaTestUtils.saveSize("Small");

      pizzaTestUtils.savePizza(
          "Expensive Large",
          List.of(ingredient),
          List.of(large),
          List.of(BigDecimal.valueOf(25.00)));
      pizzaTestUtils.savePizza(
          "Cheap Small", List.of(ingredient), List.of(small), List.of(BigDecimal.valueOf(5.00)));

      graphQlTester
          .document(
              """
              { pizzas(filter: { sizeName: "Large", minPrice: 20.0, maxPrice: 30.0 }) {
                content { name }
                totalElements
              } }
              """)
          .execute()
          .path("pizzas.totalElements")
          .entity(Integer.class)
          .isEqualTo(1);

      graphQlTester
          .document(
              """
              { pizzas(filter: { sizeName: "Large", minPrice: 20.0, maxPrice: 30.0 }) {
                content { name }
              } }
              """)
          .execute()
          .path("pizzas.content[0].name")
          .entity(String.class)
          .isEqualTo("Expensive Large");
    }
  }

  @Nested
  class PizzaQueryTests {

    @Test
    @DisplayName("pizza -> 401 when user is not authenticated")
    void pizza_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
      mockMvc
          .perform(
              post("/graphql")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      "{\"query\":\"{ pizza(id: \\\"00000000-0000-0000-0000-000000000000\\\") { id } }\"}"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("pizza -> returns pizza when it exists")
    void pizza_ShouldReturnPizza_WhenExists() {
      IngredientEntity ingredient = pizzaTestUtils.saveIngredient("Tomato");
      SizeEntity size = pizzaTestUtils.saveSize("Medium");
      PizzaEntity pizza =
          pizzaTestUtils.savePizza(
              "Margherita", List.of(ingredient), List.of(size), List.of(BigDecimal.valueOf(9.99)));

      graphQlTester
          .document(
              """
              { pizza(id: "%s") { id name ingredients { name } sizes { name price } } }
              """
                  .formatted(pizza.getId()))
          .execute()
          .path("pizza.name")
          .entity(String.class)
          .isEqualTo("Margherita");
    }

    @Test
    @WithMockUser
    @DisplayName("pizza -> returns GraphQL error when pizza not found")
    void pizza_ShouldReturnError_WhenNotFound() {
      graphQlTester
          .document(
              """
              { pizza(id: "00000000-0000-0000-0000-000000000000") { id name } }
              """)
          .execute()
          .errors()
          .satisfy(errors -> assertThat(errors).isNotEmpty());
    }
  }
}
