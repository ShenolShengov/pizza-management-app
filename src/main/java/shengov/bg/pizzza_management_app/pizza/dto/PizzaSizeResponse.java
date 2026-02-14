package shengov.bg.pizzza_management_app.pizza.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Size and price details for a pizza")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PizzaSizeResponse(
    @Schema(
            description = "Unique identifier of the size",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,
    @Schema(description = "Name of the size", example = "Large") String name,
    @Schema(description = "Price for this size", example = "12.99") BigDecimal price) {}
