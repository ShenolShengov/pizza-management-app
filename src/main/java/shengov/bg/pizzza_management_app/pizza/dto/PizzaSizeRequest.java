package shengov.bg.pizzza_management_app.pizza.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import shengov.bg.pizzza_management_app.pizza.constants.PizzaConstants;

public record PizzaSizeRequest(
    @NotNull(message = PizzaConstants.SIZE_ID_REQUIRED) UUID id,
    @NotNull(message = PizzaConstants.PRICE_REQUIRED) @DecimalMin(value = "0.01", message = PizzaConstants.PRICE_POSITIVE) BigDecimal price) {}
