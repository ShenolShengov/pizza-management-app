package shengov.bg.pizzza_management_app.pizza.model;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import shengov.bg.pizzza_management_app.core.exception.model.BaseEntity;
import shengov.bg.pizzza_management_app.ingredient.model.IngredientEntity;

@Entity
@Table(name = "pizzas")
public class PizzaEntity extends BaseEntity {

  @Column(nullable = false, unique = true)
  @Getter
  @Setter
  private String name;

  @Column @Getter @Setter private String description;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "pizza_ingredients",
      joinColumns = @JoinColumn(name = "pizza_id"),
      inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
  @BatchSize(size = 20)
  private Set<IngredientEntity> ingredients;

  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "pizza",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @BatchSize(size = 20)
  private Set<PizzaSize> sizes;

  public void addIngredients(Collection<IngredientEntity> ingredients) {
    this.ingredients.addAll(ingredients);
  }

  public void clearIngredients() {
    this.ingredients.clear();
  }

  public Set<PizzaSize> getSizes() {
    return Collections.unmodifiableSet(sizes);
  }

  public void addSize(PizzaSize pizzaSize) {
    this.sizes.add(pizzaSize);
  }

  public void removeSizes(Collection<PizzaSize> pizzaSizes) {
    this.sizes.removeAll(pizzaSizes);
  }

  public PizzaEntity() {
    this.ingredients = new HashSet<>();
    this.sizes = new HashSet<>();
  }
}
