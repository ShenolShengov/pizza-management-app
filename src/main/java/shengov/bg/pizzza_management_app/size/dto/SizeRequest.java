package shengov.bg.pizzza_management_app.size.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import shengov.bg.pizzza_management_app.ingredient.constant.IngredientConstants;

public record SizeRequest(
    @NotBlank @Size(min = 1, max = 15, message = IngredientConstants.INVALID_NAME) String name) {}
