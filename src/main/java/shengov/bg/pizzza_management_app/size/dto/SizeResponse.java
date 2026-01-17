package shengov.bg.pizzza_management_app.size.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SizeResponse(UUID id, String name) {}

