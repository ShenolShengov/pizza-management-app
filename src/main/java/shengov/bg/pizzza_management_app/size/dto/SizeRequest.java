package shengov.bg.pizzza_management_app.size.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import shengov.bg.pizzza_management_app.size.constant.SizeValidationMessages;

public record SizeRequest(
    @NotBlank(message = SizeValidationMessages.NAME_REQUIRED) @Size(min = 1, max = 15, message = SizeValidationMessages.NAME_SIZE) String name) {}
