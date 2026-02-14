package shengov.bg.pizzza_management_app.ingredient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import shengov.bg.pizzza_management_app.ingredient.constant.IngredientValidationMessages;

@Schema(description = "Request body for creating or updating an ingredient")
public record IngredientRequest(
    @Schema(
            description = "Unique name of the ingredient",
            example = "Mozzarella",
            minLength = 2,
            maxLength = 50)
        @NotBlank(message = IngredientValidationMessages.NAME_REQUIRED) @Size(min = 2, max = 50, message = IngredientValidationMessages.NAME_SIZE) String name) {}
