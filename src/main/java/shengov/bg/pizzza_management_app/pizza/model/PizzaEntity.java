package shengov.bg.pizzza_management_app.pizza.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import shengov.bg.pizzza_management_app.core.exception.model.BaseEntity;
import shengov.bg.pizzza_management_app.ingredient.model.IngredientEntity;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pizzas")
@Getter
public class PizzaEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    @Setter
    private String name;

    @Column
    @Setter
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "pizza_ingredients",
            joinColumns = @JoinColumn(name = "pizza_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private Set<IngredientEntity> ingredients;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pizza", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PizzaSize> sizes;

    public PizzaEntity() {
        this.ingredients = new HashSet<>();
        this.sizes = new HashSet<>();
    }

}
