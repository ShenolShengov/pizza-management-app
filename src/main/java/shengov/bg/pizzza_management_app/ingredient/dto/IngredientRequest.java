package shengov.bg.pizzza_management_app.ingredient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import shengov.bg.pizzza_management_app.ingredient.constant.IngredientValidationMessages;

public record IngredientRequest(
    @NotBlank(message = IngredientValidationMessages.NAME_REQUIRED) @Size(min = 2, max = 50, message = IngredientValidationMessages.NAME_SIZE) String name) {}
