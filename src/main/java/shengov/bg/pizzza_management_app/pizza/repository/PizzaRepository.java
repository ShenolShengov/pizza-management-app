package shengov.bg.pizzza_management_app.pizza.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import shengov.bg.pizzza_management_app.pizza.model.PizzaEntity;

public interface PizzaRepository extends JpaRepository<PizzaEntity, UUID> {
  boolean existsByNameIgnoreCase(String name);
  @EntityGraph(attributePaths = {"ingredients", "sizes", ""})
  Optional<PizzaEntity> findWithDetailsById(UUID id);
}
