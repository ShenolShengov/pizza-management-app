package shengov.bg.pizzza_management_app.pizza.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaResponse;
import shengov.bg.pizzza_management_app.pizza.service.PizzaService;

@RestController
@RequestMapping("/api/pizzas")
@RequiredArgsConstructor
public class PizzaController {

  private final PizzaService pizzaService;

  @GetMapping("{id}")
  public ResponseEntity<PizzaResponse> byId(@PathVariable UUID id) {
    return ResponseEntity.ok(pizzaService.getById(id));
  }

  @GetMapping
  public ResponseEntity<PagedModel<PizzaResponse>> all(@PageableDefault Pageable pageable) {
    Page<PizzaResponse> response = pizzaService.getAll(pageable);
    return ResponseEntity.ok(new PagedModel<>(response));
  }
}
