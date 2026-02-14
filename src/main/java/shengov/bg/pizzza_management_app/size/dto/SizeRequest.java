package shengov.bg.pizzza_management_app.size.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import shengov.bg.pizzza_management_app.size.constant.SizeValidationMessages;

@Schema(description = "Request body for creating or updating a pizza size")
public record SizeRequest(
    @Schema(
            description = "Unique name of the size",
            example = "Large",
            minLength = 1,
            maxLength = 15)
        @NotBlank(message = SizeValidationMessages.NAME_REQUIRED) @Size(min = 1, max = 15, message = SizeValidationMessages.NAME_SIZE) String name) {}
