package shengov.bg.pizzza_management_app.pizza.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import shengov.bg.pizzza_management_app.pizza.constant.PizzaValidationMessages;

@Schema(description = "Size and price entry for a pizza")
public record PizzaSizeRequest(
    @Schema(description = "UUID of the size", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @NotNull(message = PizzaValidationMessages.SIZE_ID_REQUIRED) UUID id,
    @Schema(description = "Price for this size", example = "12.99")
        @NotNull(message = PizzaValidationMessages.PRICE_REQUIRED) @DecimalMin(value = "0.01", message = PizzaValidationMessages.PRICE_POSITIVE) BigDecimal price) {}
