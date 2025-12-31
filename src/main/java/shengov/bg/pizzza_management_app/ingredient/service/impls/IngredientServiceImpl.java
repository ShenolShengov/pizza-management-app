package shengov.bg.pizzza_management_app.ingredient.service.impls;

import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
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
  @PreAuthorize("hasRole('ADMIN')")
  public IngredientResponse create(IngredientRequest request) {
    validateUniqueName(request.name());
    Ingredient toCreate = new Ingredient();
    toCreate.setName(request.name());
    Ingredient saved = ingredientRepository.save(toCreate);
    return toResponse(saved);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public IngredientResponse update(UUID id, IngredientRequest request) {
    Ingredient toUpdate = byId(id);
    if (!request.name().equalsIgnoreCase(toUpdate.getName())) validateUniqueName(request.name());
    toUpdate.setName(request.name());
    return toResponse(toUpdate);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(UUID id) {
    ingredientRepository.delete(byId(id));
  }

  @Override
  public IngredientResponse getById(UUID id) {
    return toResponse(byId(id));
  }

  @Override
  public Page<IngredientResponse> getAll(Pageable pageable) {
    return ingredientRepository.findAll(pageable).map(this::toResponse);
  }

  private IngredientResponse toResponse(Ingredient ingredient) {
    return new IngredientResponse(ingredient.getId(), ingredient.getName());
  }

  private Ingredient byId(UUID id) {
    return ingredientRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "id", id));
  }

  private void validateUniqueName(String name) {
    if (ingredientRepository.existsByNameIgnoreCase(name)) {
      throw new IngredientAlreadyExistsException(name);
    }
  }
}
