package shengov.bg.pizzza_management_app.ingredient.dto;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record IngredientRequest(@NotNull @Length(min = 2, max = 50) String name) {}
