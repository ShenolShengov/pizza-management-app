package shengov.bg.pizzza_management_app.pizza.service;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaResponse;

public interface PizzaService {

  PizzaResponse getById(UUID id);

  Page<PizzaResponse> getAll(Pageable pageable);
}
