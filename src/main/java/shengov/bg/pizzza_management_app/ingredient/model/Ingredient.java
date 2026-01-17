package shengov.bg.pizzza_management_app.ingredient.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import shengov.bg.pizzza_management_app.core.exception.model.BaseEntity;

@Entity
@Table(name = "ingredients")
@Getter
@Setter
public class Ingredient extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String name;
}
