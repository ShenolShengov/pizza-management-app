package shengov.bg.pizzza_management_app.ingredient.service.impls;

import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import shengov.bg.pizzza_management_app.core.exception.ResourceNotFoundException;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientRequest;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientResponse;
import shengov.bg.pizzza_management_app.ingredient.exception.IngredientAlreadyExistsException;
import shengov.bg.pizzza_management_app.ingredient.model.Ingredient;
import shengov.bg.pizzza_management_app.ingredient.repository.IngredientRepository;
import shengov.bg.pizzza_management_app.ingredient.service.IngredientService;

@Service
@AllArgsConstructor
@Transactional
public class IngredientServiceImpl implements IngredientService {

  private final IngredientRepository ingredientRepository;

  @Override
  public IngredientResponse create(IngredientRequest request) {
    if (ingredientRepository.existsByNameIgnoreCase(request.name())) {
      throw new IngredientAlreadyExistsException(request.name());
    }
    Ingredient toCreate = new Ingredient();
    toCreate.setName(request.name());
    Ingredient saved = ingredientRepository.save(toCreate);
    return toResponse(saved);
  }

  @Override
  public IngredientResponse update(UUID id, IngredientRequest request) {
    return null;
  }

  @Override
  public void delete(UUID id) {}

  @Override
  public IngredientResponse getById(UUID id) {
    Ingredient ingredient =
        ingredientRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "id", id));
    return toResponse(ingredient);
  }

  private IngredientResponse toResponse(Ingredient ingredient) {
    return new IngredientResponse(ingredient.getId(), ingredient.getName());
  }

  @Override
  public Page<IngredientResponse> getAll(Pageable pageable) {
    return ingredientRepository.findAll(pageable).map(this::toResponse);
  }
}
