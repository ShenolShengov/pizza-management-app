package shengov.bg.pizzza_management_app.ingredient.service.impls;

import static shengov.bg.pizzza_management_app.ingredient.constant.IngredientConstants.SUCCESSFULLY_CREATE_INGREDIENT;

import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientRequest;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientResponse;
import shengov.bg.pizzza_management_app.ingredient.exception.IngredientAlreadyExistsException;
import shengov.bg.pizzza_management_app.ingredient.model.Ingredient;
import shengov.bg.pizzza_management_app.ingredient.repository.IngredientRepository;
import shengov.bg.pizzza_management_app.ingredient.service.IngredientService;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
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
    String message = String.format(SUCCESSFULLY_CREATE_INGREDIENT, saved.getName());
    log.debug(message);
    return new IngredientResponse(
        saved.getId(), saved.getName(), message);
  }

  @Override
  public IngredientResponse update(UUID id, IngredientRequest request) {
    return null;
  }

  @Override
  public void delete(UUID id) {}

  @Override
  public IngredientResponse getById(UUID id) {
    return null;
  }

  @Override
  public Page<IngredientResponse> getAll(Pageable pageable) {
    return null;
  }
}
