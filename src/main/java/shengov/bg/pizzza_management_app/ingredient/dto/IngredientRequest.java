package shengov.bg.pizzza_management_app.ingredient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static shengov.bg.pizzza_management_app.ingredient.constant.IngredientConstants.INGREDIENT_INVALID_NAME_MESSAGE;

public record IngredientRequest(
    @NotBlank @Size(min = 2, max = 50, message = INGREDIENT_INVALID_NAME_MESSAGE) String name) {}
