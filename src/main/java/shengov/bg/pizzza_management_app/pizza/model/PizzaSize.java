package shengov.bg.pizzza_management_app.pizza.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import shengov.bg.pizzza_management_app.size.model.SizeEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "pizza_sizes")
@Getter
public class PizzaSize {

    @EmbeddedId
    private PizzaSizeId pizzaSizeId;

    @ManyToOne
    @MapsId("pizzaId")
    @Setter
    private PizzaEntity pizza;

    @ManyToOne
    @MapsId("sizeId")
    @Setter
    private SizeEntity size;

    @Column(nullable = false)
    @Setter
    private BigDecimal price;

    public PizzaSize(PizzaEntity pizzaEntity, SizeEntity sizeEntity, BigDecimal price) {
        this.pizza = pizzaEntity;
        this.size = sizeEntity;
        this.price = price;
        this.pizzaSizeId = new PizzaSizeId(pizzaEntity.getId(), sizeEntity.getId());
    }
}
