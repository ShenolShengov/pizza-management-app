package shengov.bg.pizzza_management_app.pizza.service;

import org.springframework.data.domain.Page;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaResponse;

import java.awt.print.Pageable;
import java.util.UUID;

public interface PizzaService {

    PizzaResponse getById(UUID id);

    Page<PizzaResponse> getAll(Pageable pageable);
}
