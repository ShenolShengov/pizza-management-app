package shengov.bg.pizzza_management_app.pizza.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaRequest;
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

  @PostMapping
  public ResponseEntity<PizzaResponse> create(@RequestBody @Valid PizzaRequest request) {
    PizzaResponse response = pizzaService.create(request);
    return ResponseEntity.created(
            ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(response.id()))
        .body(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<PizzaResponse> update(
      @RequestBody @Valid PizzaRequest request, @PathVariable UUID id) {
    PizzaResponse response = pizzaService.update(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    pizzaService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
