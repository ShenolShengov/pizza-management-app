package shengov.bg.pizzza_management_app.pizza.service.impls;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import shengov.bg.pizzza_management_app.core.exception.ResourceNotFoundException;
import shengov.bg.pizzza_management_app.core.exception.model.BaseEntity;
import shengov.bg.pizzza_management_app.ingredient.model.IngredientEntity;
import shengov.bg.pizzza_management_app.ingredient.repository.IngredientRepository;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaRequest;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaResponse;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaSizeRequest;
import shengov.bg.pizzza_management_app.pizza.exception.PizzaAlreadyExistException;
import shengov.bg.pizzza_management_app.pizza.mapper.PizzaMapper;
import shengov.bg.pizzza_management_app.pizza.model.PizzaEntity;
import shengov.bg.pizzza_management_app.pizza.model.PizzaSize;
import shengov.bg.pizzza_management_app.pizza.repository.PizzaRepository;
import shengov.bg.pizzza_management_app.pizza.service.PizzaService;
import shengov.bg.pizzza_management_app.size.model.SizeEntity;
import shengov.bg.pizzza_management_app.size.repository.SizeRepository;

@Service
@RequiredArgsConstructor
public class PizzaServiceImpl implements PizzaService {

  private final PizzaRepository pizzaRepository;
  private final IngredientRepository ingredientRepository;
  private final SizeRepository sizeRepository;
  private final PizzaMapper pizzaMapper;

  @Override
  @Transactional
  public PizzaResponse create(PizzaRequest pizzaRequest) {
    if (pizzaRepository.existsByNameIgnoreCase(pizzaRequest.name())) {
      throw new PizzaAlreadyExistException(pizzaRequest.name());
    }

    PizzaEntity pizzaEntity = new PizzaEntity();
    pizzaEntity.setName(pizzaRequest.name());
    pizzaEntity.setDescription(pizzaRequest.description());

    List<IngredientEntity> ingredients = fetchIngredients(pizzaRequest.ingredientIds());
    validateEntityIds(pizzaRequest.ingredientIds(), ingredients, "Ingredients");

    pizzaEntity.getIngredients().addAll(ingredients);

    List<UUID> sizeIds = pizzaRequest.sizes().stream().map(PizzaSizeRequest::id).toList();

    List<SizeEntity> sizes = sizeRepository.findAllById(sizeIds);
    validateEntityIds(pizzaRequest.ingredientIds(), ingredients, "Sizes");

    Map<UUID, SizeEntity> sizeMap =
        sizes.stream().collect(Collectors.toMap(SizeEntity::getId, size -> size));
    for (PizzaSizeRequest sizeRequest : pizzaRequest.sizes()) {
      SizeEntity sizeEntity = sizeMap.get(sizeRequest.id());
      PizzaSize pizzaSize = new PizzaSize(pizzaEntity, sizeEntity, sizeRequest.price());
      pizzaEntity.getSizes().add(pizzaSize);
    }
    PizzaEntity saved = pizzaRepository.save(pizzaEntity);
    return pizzaMapper.entityToResponse(saved);
  }

  private List<IngredientEntity> fetchIngredients(List<UUID> ingredientIds) {
    return ingredientRepository.findAllById(ingredientIds);
  }

  private <T extends BaseEntity> void validateEntityIds(
      List<UUID> requestedIds, List<T> foundEntities, String resourceName) {
    if (foundEntities.size() == requestedIds.size()) {
      return;
    }

    Set<UUID> foundIds = foundEntities.stream().map(BaseEntity::getId).collect(Collectors.toSet());

    List<UUID> missingIds = requestedIds.stream().filter(id -> !foundIds.contains(id)).toList();

    throw new ResourceNotFoundException(
        resourceName,
        "ids",
        missingIds.stream().map(UUID::toString).collect(Collectors.joining(", ")));
  }

  @Override
  public PizzaResponse getById(UUID id) {
    return pizzaRepository
        .findById(id)
        .map(pizzaMapper::entityToResponse)
        .orElseThrow(() -> new ResourceNotFoundException("Pizza", "id", id.toString()));
  }

  @Override
  public Page<PizzaResponse> getAll(Pageable pageable) {
    return pizzaRepository.findAll(pageable).map(pizzaMapper::entityToResponse);
  }
}
