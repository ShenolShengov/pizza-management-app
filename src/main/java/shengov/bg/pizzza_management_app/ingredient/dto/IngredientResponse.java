package shengov.bg.pizzza_management_app.ingredient.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Ingredient details")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record IngredientResponse(
    @Schema(
            description = "Unique identifier of the ingredient",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,
    @Schema(description = "Name of the ingredient", example = "Mozzarella") String name) {}
