package shengov.bg.pizzza_management_app.size.dto;

import static shengov.bg.pizzza_management_app.size.constant.SizeConstants.SIZE_INVALID_NAME_MESSAGE;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SizeRequest(
    @NotBlank @Size(min = 1, max = 15, message = SIZE_INVALID_NAME_MESSAGE) String name) {}
