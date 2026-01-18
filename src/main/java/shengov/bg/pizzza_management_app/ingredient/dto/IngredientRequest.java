package shengov.bg.pizzza_management_app.ingredient.dto;

import static shengov.bg.pizzza_management_app.ingredient.constant.IngredientConstants.INVALID_NAME_MESSAGE;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record IngredientRequest(
    @NotBlank @Length(min = 2, max = 50, message = INVALID_NAME_MESSAGE) String name) {}
