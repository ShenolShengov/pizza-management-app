package shengov.bg.pizzza_management_app.pizza.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shengov.bg.pizzza_management_app.pizza.model.PizzaEntity;

import java.util.UUID;

public interface PizzaRepository extends JpaRepository<PizzaEntity, UUID> {
}
