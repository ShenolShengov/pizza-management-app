package shengov.bg.pizzza_management_app.pizza.service.impls;

import java.awt.print.Pageable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import shengov.bg.pizzza_management_app.core.exception.ResourceNotFoundException;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaResponse;
import shengov.bg.pizzza_management_app.pizza.mapper.PizzaMapper;
import shengov.bg.pizzza_management_app.pizza.repository.PizzaRepository;
import shengov.bg.pizzza_management_app.pizza.service.PizzaService;

@Service
@AllArgsConstructor
public class PizzaServiceImpl implements PizzaService {

  private final PizzaRepository pizzaRepository;
  private final PizzaMapper pizzaMapper;

  @Override
  public PizzaResponse getById(UUID id) {
    return pizzaRepository
        .findById(id)
        .map(pizzaMapper::entityToResponse)
        .orElseThrow(() -> new ResourceNotFoundException("Pizza", "id", id.toString()));
  }

  @Override
  public Page<PizzaResponse> getAll(Pageable pageable) {
    return null;
  }
}
