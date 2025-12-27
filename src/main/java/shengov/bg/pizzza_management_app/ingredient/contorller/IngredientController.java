package shengov.bg.pizzza_management_app.ingredient.contorller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientRequest;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientResponse;
import shengov.bg.pizzza_management_app.ingredient.service.IngredientService;

@RestController
@RequestMapping("/api/ingredients")
@AllArgsConstructor
public class IngredientController {

  private final IngredientService ingredientService;

  @PostMapping
  public ResponseEntity<IngredientResponse> create(@RequestBody @Valid IngredientRequest request) {
    return ResponseEntity.ok(ingredientService.create(request));
  }
}
