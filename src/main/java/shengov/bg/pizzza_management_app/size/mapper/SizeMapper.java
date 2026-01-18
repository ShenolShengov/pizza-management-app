package shengov.bg.pizzza_management_app.size.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientRequest;
import shengov.bg.pizzza_management_app.size.dto.SizeResponse;
import shengov.bg.pizzza_management_app.size.model.SizeEntity;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SizeMapper {

  SizeEntity requestToEntity(IngredientRequest ingredientRequest);

  SizeResponse entityToResponse(SizeEntity entity);
}
