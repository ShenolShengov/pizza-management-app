package shengov.bg.pizzza_management_app.pizza.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import shengov.bg.pizzza_management_app.pizza.constants.PizzaConstants;

public record PizzaRequest(
    @NotBlank(message = PizzaConstants.NAME_REQUIRED) @Size(min = 3, max = 40, message = PizzaConstants.NAME_SIZE) String name,
    @Size(max = 200, message = PizzaConstants.DESCRIPTION_SIZE) String description,
    @NotEmpty(message = PizzaConstants.INGREDIENTS_REQUIRED) List<UUID> ingredientIds,
    @NotEmpty(message = PizzaConstants.SIZES_REQUIRED) List<@Valid PizzaSizeRequest> sizes) {}
