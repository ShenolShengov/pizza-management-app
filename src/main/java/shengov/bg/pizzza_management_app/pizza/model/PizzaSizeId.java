package shengov.bg.pizzza_management_app.pizza.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PizzaSizeId implements Serializable {

    private UUID pizzaId;
    private UUID sizeId;

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        PizzaSizeId that = (PizzaSizeId) object;
        return Objects.equals(pizzaId, that.pizzaId) && Objects.equals(sizeId, that.sizeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pizzaId, sizeId);
    }
}
