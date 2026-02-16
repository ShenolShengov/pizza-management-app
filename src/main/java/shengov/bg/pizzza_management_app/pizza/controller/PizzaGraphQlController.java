package shengov.bg.pizzza_management_app.pizza.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaFilterInput;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaPageResponse;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaResponse;
import shengov.bg.pizzza_management_app.pizza.service.PizzaService;

@Controller
@RequiredArgsConstructor
public class PizzaGraphQlController {

  private final PizzaService pizzaService;

  @QueryMapping
  public PizzaPageResponse pizzas(
      @Argument PizzaFilterInput filter, @Argument int page, @Argument int size) {
    Pageable pageable = PageRequest.of(page, size);
    return PizzaPageResponse.from(pizzaService.getAll(filter, pageable));
  }

  @QueryMapping
  public PizzaResponse pizza(@Argument UUID id) {
    return pizzaService.getById(id);
  }
}
