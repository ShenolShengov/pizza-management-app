package shengov.bg.pizzza_management_app.pizza.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PizzaSizeResponse(UUID id, String name, BigDecimal price) {}
