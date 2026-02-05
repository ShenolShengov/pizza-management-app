package shengov.bg.pizzza_management_app.pizza.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import shengov.bg.pizzza_management_app.pizza.constants.PizzaValidationMessages;

public record PizzaRequest(
    @NotBlank(message = PizzaValidationMessages.NAME_REQUIRED) @Size(min = 3, max = 40, message = PizzaValidationMessages.NAME_SIZE) String name,
    @Size(max = 200, message = PizzaValidationMessages.DESCRIPTION_SIZE) String description,
    @NotEmpty(message = PizzaValidationMessages.INGREDIENTS_REQUIRED) List<UUID> ingredientIds,
    @NotEmpty(message = PizzaValidationMessages.SIZES_REQUIRED) List<@Valid PizzaSizeRequest> sizes) {}
