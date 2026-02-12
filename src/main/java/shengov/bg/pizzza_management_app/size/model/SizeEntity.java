package shengov.bg.pizzza_management_app.size.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import shengov.bg.pizzza_management_app.core.model.BaseEntity;

@Entity
@Table(name = "size")
@Getter
@Setter
public class SizeEntity extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String name;
}
