package shengov.bg.pizzza_management_app.pizza.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaRequest;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaResponse;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaSizeResponse;
import shengov.bg.pizzza_management_app.pizza.model.PizzaEntity;
import shengov.bg.pizzza_management_app.pizza.model.PizzaSize;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PizzaMapper {

  @Mapping(target = "sizes", ignore = true)
  PizzaEntity requestToEntity(PizzaRequest request);

  PizzaResponse entityToResponse(PizzaEntity entity);

  @Mapping(target = "id", source = "size.id")
  @Mapping(target = "name", source = "size.name")
  @Mapping(target = "price", source = "price")
  PizzaSizeResponse pizzaSizeToResponse(PizzaSize pizzaSize);
}
