package shengov.bg.pizzza_management_app.ingredient.contorller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientRequest;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientResponse;
import shengov.bg.pizzza_management_app.ingredient.service.IngredientService;

@RestController
@RequestMapping("/api/ingredients")
@AllArgsConstructor
public class IngredientController {

  private final IngredientService ingredientService;

  @GetMapping("/{id}")
  public ResponseEntity<IngredientResponse> byId(@PathVariable @NotNull UUID id) {
    return ResponseEntity.ok(ingredientService.getById(id));
  }

  @PostMapping
  public ResponseEntity<IngredientResponse> create(@RequestBody @Valid IngredientRequest request) {
    IngredientResponse response = ingredientService.create(request);
    return ResponseEntity.created(
            ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(response.id()))
        .body(response);
  }
}
