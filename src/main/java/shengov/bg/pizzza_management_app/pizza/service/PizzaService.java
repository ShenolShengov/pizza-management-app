package shengov.bg.pizzza_management_app.pizza.service;

import java.awt.print.Pageable;
import java.util.UUID;
import org.springframework.data.domain.Page;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaResponse;

public interface PizzaService {

  PizzaResponse getById(UUID id);

  Page<PizzaResponse> getAll(Pageable pageable);
}
