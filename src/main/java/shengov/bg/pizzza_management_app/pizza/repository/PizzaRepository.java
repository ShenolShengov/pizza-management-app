package shengov.bg.pizzza_management_app.pizza.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import shengov.bg.pizzza_management_app.pizza.model.PizzaEntity;

public interface PizzaRepository extends JpaRepository<PizzaEntity, UUID> {}
