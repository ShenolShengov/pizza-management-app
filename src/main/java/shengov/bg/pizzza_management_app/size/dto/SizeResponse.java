package shengov.bg.pizzza_management_app.size.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Pizza size details")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SizeResponse(
    @Schema(
            description = "Unique identifier of the size",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,
    @Schema(description = "Name of the size", example = "Large") String name) {}
