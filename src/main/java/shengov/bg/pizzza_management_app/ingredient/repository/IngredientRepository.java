package shengov.bg.pizzza_management_app.ingredient.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shengov.bg.pizzza_management_app.ingredient.model.Ingredient;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {}
