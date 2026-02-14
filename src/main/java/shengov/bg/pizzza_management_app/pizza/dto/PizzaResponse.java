package shengov.bg.pizzza_management_app.pizza.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientResponse;

@Schema(description = "Pizza details including ingredients and sizes")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PizzaResponse(
    @Schema(
            description = "Unique identifier of the pizza",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,
    @Schema(description = "Name of the pizza", example = "Margherita") String name,
    @Schema(
            description = "Short description of the pizza",
            example = "Classic tomato and mozzarella pizza")
        String description,
    @Schema(description = "List of ingredients") List<IngredientResponse> ingredients,
    @Schema(description = "List of available sizes with prices") List<PizzaSizeResponse> sizes) {}
