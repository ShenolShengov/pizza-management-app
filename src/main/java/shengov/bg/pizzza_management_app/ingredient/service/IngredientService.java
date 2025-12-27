package shengov.bg.pizzza_management_app.ingredient.service;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientRequest;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientResponse;

public interface IngredientService {
  IngredientResponse create(IngredientRequest request);

  IngredientResponse update(UUID id, IngredientRequest request);

  void delete(UUID id);

  IngredientResponse getById(UUID id);

  Page<IngredientResponse> getAll(Pageable pageable);
}
