package shengov.bg.pizzza_management_app.pizza.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import shengov.bg.pizzza_management_app.pizza.model.PizzaEntity;

public interface PizzaRepository
    extends JpaRepository<PizzaEntity, UUID>, JpaSpecificationExecutor<PizzaEntity> {
  boolean existsByNameIgnoreCase(String name);

  boolean existsByIngredientsId(UUID ingredientId);

  boolean existsBySizesSizeId(UUID sizeId);

  @EntityGraph(attributePaths = {"ingredients", "sizes"})
  Optional<PizzaEntity> findWithDetailsById(UUID id);
}
