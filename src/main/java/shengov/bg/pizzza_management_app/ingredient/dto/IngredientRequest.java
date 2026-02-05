package shengov.bg.pizzza_management_app.ingredient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import shengov.bg.pizzza_management_app.ingredient.constant.IngredientConstants;

public record IngredientRequest(
    @NotBlank @Size(min = 2, max = 50, message = IngredientConstants.INVALID_NAME) String name) {}
