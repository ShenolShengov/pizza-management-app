package shengov.bg.pizzza_management_app.ingredient.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientRequest;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientResponse;
import shengov.bg.pizzza_management_app.ingredient.service.IngredientService;

@Tag(name = "Ingredients", description = "Manage pizza ingredients")
@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController {

  private final IngredientService ingredientService;

  @Operation(
      summary = "Get all ingredients",
      description = "Returns a paginated list of all ingredients")
  @ApiResponse(responseCode = "200", description = "Ingredients retrieved successfully")
  @GetMapping
  public ResponseEntity<PagedModel<IngredientResponse>> getAll(@PageableDefault Pageable pageable) {
    PagedModel<IngredientResponse> response = new PagedModel<>(ingredientService.getAll(pageable));
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Get ingredient by ID",
      description = "Returns a single ingredient by its UUID")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Ingredient found"),
    @ApiResponse(responseCode = "404", description = "Ingredient not found")
  })
  @GetMapping("/{id}")
  public ResponseEntity<IngredientResponse> getById(
      @Parameter(description = "UUID of the ingredient") @PathVariable UUID id) {
    return ResponseEntity.ok(ingredientService.getById(id));
  }

  @Operation(
      summary = "Create ingredient",
      description = "Creates a new ingredient. Requires ADMIN role.")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Ingredient created successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request body"),
    @ApiResponse(responseCode = "409", description = "Ingredient with this name already exists")
  })
  @PostMapping
  public ResponseEntity<IngredientResponse> create(@RequestBody @Valid IngredientRequest request) {
    IngredientResponse response = ingredientService.create(request);
    return ResponseEntity.created(
            ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(response.id()))
        .body(response);
  }

  @Operation(
      summary = "Update ingredient",
      description = "Updates an existing ingredient by its UUID. Requires ADMIN role.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Ingredient updated successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request body"),
    @ApiResponse(responseCode = "404", description = "Ingredient not found"),
    @ApiResponse(responseCode = "409", description = "Ingredient with this name already exists")
  })
  @PutMapping("/{id}")
  public ResponseEntity<IngredientResponse> update(
      @RequestBody @Valid IngredientRequest request,
      @Parameter(description = "UUID of the ingredient to update") @PathVariable UUID id) {
    IngredientResponse response = ingredientService.update(id, request);
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Delete ingredient",
      description = "Deletes an ingredient by its UUID. Requires ADMIN role.")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Ingredient deleted successfully"),
    @ApiResponse(responseCode = "404", description = "Ingredient not found")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @Parameter(description = "UUID of the ingredient to delete") @PathVariable UUID id) {
    ingredientService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
