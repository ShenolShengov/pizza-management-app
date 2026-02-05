package shengov.bg.pizzza_management_app.pizza.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import shengov.bg.pizzza_management_app.pizza.constants.PizzaValidationMessages;

public record PizzaSizeRequest(
    @NotNull(message = PizzaValidationMessages.SIZE_ID_REQUIRED) UUID id,
    @NotNull(message = PizzaValidationMessages.PRICE_REQUIRED) @DecimalMin(value = "0.01", message = PizzaValidationMessages.PRICE_POSITIVE) BigDecimal price) {}
