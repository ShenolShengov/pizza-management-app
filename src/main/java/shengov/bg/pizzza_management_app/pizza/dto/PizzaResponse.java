package shengov.bg.pizzza_management_app.pizza.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.UUID;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientResponse;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PizzaResponse(
    UUID id,
    String name,
    String description,
    List<IngredientResponse> ingredients,
    List<PizzaSizeResponse> sizes) {}
