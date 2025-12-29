package shengov.bg.pizzza_management_app.ingredient.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record IngredientResponse(UUID id, String name) {}
