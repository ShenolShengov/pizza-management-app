package shengov.bg.pizzza_management_app.ingredient.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import shengov.bg.pizzza_management_app.core.model.BaseEntity;

@Entity
@Table(name = "ingredients")
@Getter
@Setter
public class IngredientEntity extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String name;
}
