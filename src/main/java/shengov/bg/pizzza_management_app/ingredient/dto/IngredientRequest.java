package shengov.bg.pizzza_management_app.ingredient.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import static shengov.bg.pizzza_management_app.ingredient.constant.IngredientConstants.INVALID_NAME_MESSAGE;

public record IngredientRequest(
    @NotBlank @Length(min = 2, max = 50, message = INVALID_NAME_MESSAGE) String name) {}
