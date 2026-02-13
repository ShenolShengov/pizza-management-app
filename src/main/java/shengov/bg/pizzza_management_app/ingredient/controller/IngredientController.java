package shengov.bg.pizzza_management_app.ingredient.controller;

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

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController {

  private final IngredientService ingredientService;

  @GetMapping
  public ResponseEntity<PagedModel<IngredientResponse>> getAll(@PageableDefault Pageable pageable) {
    PagedModel<IngredientResponse> response = new PagedModel<>(ingredientService.getAll(pageable));
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<IngredientResponse> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(ingredientService.getById(id));
  }

  @PostMapping
  public ResponseEntity<IngredientResponse> create(@RequestBody @Valid IngredientRequest request) {
    IngredientResponse response = ingredientService.create(request);
    return ResponseEntity.created(
            ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(response.id()))
        .body(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<IngredientResponse> update(
      @RequestBody @Valid IngredientRequest request, @PathVariable UUID id) {
    IngredientResponse response = ingredientService.update(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    ingredientService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
