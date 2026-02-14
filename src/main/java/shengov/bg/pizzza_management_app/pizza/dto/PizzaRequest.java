package shengov.bg.pizzza_management_app.pizza.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import shengov.bg.pizzza_management_app.pizza.constant.PizzaValidationMessages;

@Schema(description = "Request body for creating or updating a pizza")
public record PizzaRequest(
    @Schema(
            description = "Unique name of the pizza",
            example = "Margherita",
            minLength = 3,
            maxLength = 40)
        @NotBlank(message = PizzaValidationMessages.NAME_REQUIRED) @Size(min = 3, max = 40, message = PizzaValidationMessages.NAME_SIZE) String name,
    @Schema(
            description = "Short description of the pizza",
            example = "Classic tomato and mozzarella pizza",
            maxLength = 200)
        @Size(max = 200, message = PizzaValidationMessages.DESCRIPTION_SIZE) String description,
    @Schema(
            description = "List of ingredient UUIDs to include in the pizza",
            example = "[\"3fa85f64-5717-4562-b3fc-2c963f66afa6\"]")
        @NotEmpty(message = PizzaValidationMessages.INGREDIENTS_REQUIRED) List<UUID> ingredientIds,
    @Schema(description = "List of sizes with their prices")
        @NotEmpty(message = PizzaValidationMessages.SIZES_REQUIRED) List<@Valid PizzaSizeRequest> sizes) {}
