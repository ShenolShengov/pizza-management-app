package shengov.bg.pizzza_management_app.size.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SizeRequest(@NotBlank @Size(min = 1, max = 2) String name) {
}
