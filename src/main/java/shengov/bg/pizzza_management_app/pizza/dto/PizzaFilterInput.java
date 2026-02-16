package shengov.bg.pizzza_management_app.pizza.dto;

import java.math.BigDecimal;
import java.util.List;

public record PizzaFilterInput(
    String name,
    String description,
    List<String> ingredientNames,
    String sizeName,
    BigDecimal minPrice,
    BigDecimal maxPrice) {}
