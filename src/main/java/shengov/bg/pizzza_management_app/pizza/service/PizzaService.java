package shengov.bg.pizzza_management_app.pizza.service;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaFilterInput;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaRequest;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaResponse;

public interface PizzaService {

  PizzaResponse create(PizzaRequest request);

  PizzaResponse update(UUID id, PizzaRequest request);

  void delete(UUID id);

  PizzaResponse getById(UUID id);

  Page<PizzaResponse> getAll(Pageable pageable);

  Page<PizzaResponse> getAll(PizzaFilterInput filter, Pageable pageable);
}
