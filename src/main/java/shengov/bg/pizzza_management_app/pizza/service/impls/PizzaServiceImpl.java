package shengov.bg.pizzza_management_app.pizza.service.impls;

import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shengov.bg.pizzza_management_app.core.exception.ResourceNotFoundException;
import shengov.bg.pizzza_management_app.core.model.BaseEntity;
import shengov.bg.pizzza_management_app.ingredient.model.IngredientEntity;
import shengov.bg.pizzza_management_app.ingredient.repository.IngredientRepository;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaFilterInput;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaRequest;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaResponse;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaSizeRequest;
import shengov.bg.pizzza_management_app.pizza.exception.PizzaAlreadyExistsException;
import shengov.bg.pizzza_management_app.pizza.mapper.PizzaMapper;
import shengov.bg.pizzza_management_app.pizza.model.PizzaEntity;
import shengov.bg.pizzza_management_app.pizza.model.PizzaSize;
import shengov.bg.pizzza_management_app.pizza.repository.PizzaRepository;
import shengov.bg.pizzza_management_app.pizza.repository.PizzaSpecification;
import shengov.bg.pizzza_management_app.pizza.service.PizzaService;
import shengov.bg.pizzza_management_app.size.model.SizeEntity;
import shengov.bg.pizzza_management_app.size.repository.SizeRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PizzaServiceImpl implements PizzaService {

  private final PizzaRepository pizzaRepository;
  private final IngredientRepository ingredientRepository;
  private final SizeRepository sizeRepository;
  private final PizzaMapper pizzaMapper;

  @Override
  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public PizzaResponse create(PizzaRequest request) {
    validateUniqueName(request.name());

    PizzaEntity pizzaEntity = pizzaMapper.requestToEntity(request);
    updateIngredients(pizzaEntity, request.ingredientIds());
    updateSizes(pizzaEntity, request.sizes());

    PizzaEntity saved = pizzaRepository.save(pizzaEntity);
    return pizzaMapper.entityToResponse(saved);
  }

  @Override
  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public PizzaResponse update(UUID id, PizzaRequest request) {
    PizzaEntity pizzaEntity = byIdWithDetails(id);
    if (!request.name().equalsIgnoreCase(pizzaEntity.getName())) {
      validateUniqueName(request.name());
    }

    pizzaEntity.setName(request.name());
    pizzaEntity.setDescription(request.description());

    updateIngredients(pizzaEntity, request.ingredientIds());
    updateSizes(pizzaEntity, request.sizes());

    return pizzaMapper.entityToResponse(pizzaEntity);
  }

  @Override
  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(UUID id) {
    PizzaEntity pizzaEntity = byId(id);
    pizzaRepository.delete(pizzaEntity);
  }

  @Override
  public PizzaResponse getById(UUID id) {
    return pizzaMapper.entityToResponse(byIdWithDetails(id));
  }

  @Override
  public Page<PizzaResponse> getAll(Pageable pageable) {
    return pizzaRepository.findAll(pageable).map(pizzaMapper::entityToResponse);
  }

  @Override
  public Page<PizzaResponse> getAll(PizzaFilterInput filter, Pageable pageable) {
    return pizzaRepository
        .findAll(PizzaSpecification.from(filter), pageable)
        .map(pizzaMapper::entityToResponse);
  }

  private PizzaEntity byId(UUID id) {
    return pizzaRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Pizza", "id", id.toString()));
  }

  private PizzaEntity byIdWithDetails(UUID id) {
    return pizzaRepository
        .findWithDetailsById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Pizza", "id", id.toString()));
  }

  private void validateUniqueName(String name) {
    if (pizzaRepository.existsByNameIgnoreCase(name)) {
      throw new PizzaAlreadyExistsException(name);
    }
  }

  private <T extends BaseEntity> void validateEntityIds(
      Collection<UUID> requestedIds, Collection<T> foundEntities, String resourceName) {

    long distinctCount = requestedIds.stream().distinct().count();
    if (requestedIds.size() != distinctCount) {
      throw new IllegalArgumentException("Duplicate " + resourceName.toLowerCase() + " in request");
    }

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

  private void updateIngredients(PizzaEntity pizzaEntity, List<UUID> ingredientIds) {
    List<IngredientEntity> ingredients = fetchIngredients(ingredientIds);
    validateEntityIds(ingredientIds, ingredients, "Ingredients");
    pizzaEntity.clearIngredients();
    pizzaEntity.addIngredients(ingredients);
  }

  private void updateSizes(PizzaEntity pizzaEntity, List<PizzaSizeRequest> sizes) {
    List<UUID> sizeIds = sizes.stream().map(PizzaSizeRequest::id).toList();
    Map<UUID, SizeEntity> sizesById = fetchSizesAsMap(sizeIds);
    validateEntityIds(sizeIds, sizesById.values(), "Sizes");

    List<PizzaSize> removedSizes =
        pizzaEntity.getSizes().stream()
            .filter(ps -> !sizeIds.contains(ps.getSize().getId()))
            .toList();
    pizzaEntity.removeSizes(removedSizes);

    Map<UUID, PizzaSize> currentSizes =
        pizzaEntity.getSizes().stream()
            .collect(Collectors.toMap(ps -> ps.getSize().getId(), ps -> ps));

    for (PizzaSizeRequest sizeRequest : sizes) {
      PizzaSize existing = currentSizes.get(sizeRequest.id());

      if (existing != null) {
        existing.setPrice(sizeRequest.price());
      } else {
        SizeEntity sizeEntity = sizesById.get(sizeRequest.id());
        pizzaEntity.addSize(new PizzaSize(pizzaEntity, sizeEntity, sizeRequest.price()));
      }
    }
  }

  private List<IngredientEntity> fetchIngredients(List<UUID> ingredientIds) {
    return ingredientRepository.findAllById(ingredientIds);
  }

  private Map<UUID, SizeEntity> fetchSizesAsMap(List<UUID> sizeIds) {
    return sizeRepository.findAllById(sizeIds).stream()
        .collect(Collectors.toMap(SizeEntity::getId, s -> s));
  }
}
