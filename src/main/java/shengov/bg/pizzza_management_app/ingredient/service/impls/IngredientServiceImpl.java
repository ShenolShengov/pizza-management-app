package shengov.bg.pizzza_management_app.ingredient.service.impls;

import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import shengov.bg.pizzza_management_app.core.exception.ResourceNotFoundException;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientRequest;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientResponse;
import shengov.bg.pizzza_management_app.ingredient.exception.IngredientAlreadyExistsException;
import shengov.bg.pizzza_management_app.ingredient.exception.IngredientInUseException;
import shengov.bg.pizzza_management_app.ingredient.mapper.IngredientMapper;
import shengov.bg.pizzza_management_app.ingredient.model.IngredientEntity;
import shengov.bg.pizzza_management_app.ingredient.repository.IngredientRepository;
import shengov.bg.pizzza_management_app.ingredient.service.IngredientService;
import shengov.bg.pizzza_management_app.pizza.repository.PizzaRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class IngredientServiceImpl implements IngredientService {

  private final IngredientRepository ingredientRepository;
  private final PizzaRepository pizzaRepository;
  private final IngredientMapper mapper;

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public IngredientResponse create(IngredientRequest request) {
    validateUniqueName(request.name());
    IngredientEntity toCreate = mapper.requestToEntity(request);
    IngredientEntity saved = ingredientRepository.save(toCreate);
    return mapper.entityToResponse(saved);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public IngredientResponse update(UUID id, IngredientRequest request) {
    IngredientEntity toUpdate = byId(id);
    if (!request.name().equalsIgnoreCase(toUpdate.getName())) validateUniqueName(request.name());
    toUpdate.setName(request.name());
    return mapper.entityToResponse(toUpdate);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(UUID id) {
    if (pizzaRepository.existsByIngredientsId(id)) {
      throw new IngredientInUseException(id);
    }
    ingredientRepository.delete(byId(id));
  }

  @Override
  public IngredientResponse getById(UUID id) {
    return mapper.entityToResponse(byId(id));
  }

  @Override
  public Page<IngredientResponse> getAll(Pageable pageable) {
    return ingredientRepository.findAll(pageable).map(mapper::entityToResponse);
  }

  private IngredientEntity byId(UUID id) {
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
