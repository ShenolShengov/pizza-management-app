package shengov.bg.pizzza_management_app.pizza.dto;

import java.util.List;
import org.springframework.data.domain.Page;

public record PizzaPageResponse(
    List<PizzaResponse> content, long totalElements, int totalPages, int page, int size) {

  public static PizzaPageResponse from(Page<PizzaResponse> pageResult) {
    return new PizzaPageResponse(
        pageResult.getContent(),
        pageResult.getTotalElements(),
        pageResult.getTotalPages(),
        pageResult.getNumber(),
        pageResult.getSize());
  }
}
