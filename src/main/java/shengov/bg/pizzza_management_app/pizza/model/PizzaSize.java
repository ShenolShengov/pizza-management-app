package shengov.bg.pizzza_management_app.pizza.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import shengov.bg.pizzza_management_app.size.model.SizeEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "pizza_sizes")
@Getter
@Setter
public class PizzaSize {

    @EmbeddedId
    private PizzaSizeId pizzaSizeId;

    @ManyToOne
    @MapsId("pizzaId")
    private PizzaEntity pizza;

    @ManyToOne
    @MapsId("sizeId")
    private SizeEntity size;

    @Column(nullable = false)
    private BigDecimal price;
}
