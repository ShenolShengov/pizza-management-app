package shengov.bg.pizzza_management_app.pizza.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Pizzas", description = "Manage pizzas with their ingredients and sizes")
@RestController
@RequestMapping("/api/pizzas")
@RequiredArgsConstructor
public class PizzaController {

  private final PizzaService pizzaService;

  @Operation(
      summary = "Get pizza by ID",
      description = "Returns a single pizza with its ingredients and sizes by UUID")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Pizza found"),
    @ApiResponse(responseCode = "404", description = "Pizza not found")
  })
  @GetMapping("/{id}")
  public ResponseEntity<PizzaResponse> getById(
      @Parameter(description = "UUID of the pizza") @PathVariable UUID id) {
    return ResponseEntity.ok(pizzaService.getById(id));
  }

  @Operation(summary = "Get all pizzas", description = "Returns a paginated list of all pizzas")
  @ApiResponse(responseCode = "200", description = "Pizzas retrieved successfully")
  @GetMapping
  public ResponseEntity<PagedModel<PizzaResponse>> getAll(@PageableDefault Pageable pageable) {
    Page<PizzaResponse> response = pizzaService.getAll(pageable);
    return ResponseEntity.ok(new PagedModel<>(response));
  }

  @Operation(
      summary = "Create pizza",
      description = "Creates a new pizza with ingredients and sizes. Requires ADMIN role.")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Pizza created successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request body"),
    @ApiResponse(responseCode = "409", description = "Pizza with this name already exists")
  })
  @PostMapping
  public ResponseEntity<PizzaResponse> create(@RequestBody @Valid PizzaRequest request) {
    PizzaResponse response = pizzaService.create(request);
    return ResponseEntity.created(
            ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(response.id()))
        .body(response);
  }

  @Operation(
      summary = "Update pizza",
      description = "Updates an existing pizza by its UUID. Requires ADMIN role.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Pizza updated successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request body"),
    @ApiResponse(responseCode = "404", description = "Pizza not found"),
    @ApiResponse(responseCode = "409", description = "Pizza with this name already exists")
  })
  @PutMapping("/{id}")
  public ResponseEntity<PizzaResponse> update(
      @RequestBody @Valid PizzaRequest request,
      @Parameter(description = "UUID of the pizza to update") @PathVariable UUID id) {
    PizzaResponse response = pizzaService.update(id, request);
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Delete pizza",
      description = "Deletes a pizza by its UUID. Requires ADMIN role.")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Pizza deleted successfully"),
    @ApiResponse(responseCode = "404", description = "Pizza not found")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @Parameter(description = "UUID of the pizza to delete") @PathVariable UUID id) {
    pizzaService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
