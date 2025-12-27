package shengov.bg.pizzza_management_app.ingredient.dto;

import java.util.UUID;

public record IngredientResponse(UUID id, String name, String message) {
}
