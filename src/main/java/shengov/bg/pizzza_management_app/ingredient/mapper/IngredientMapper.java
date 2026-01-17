package shengov.bg.pizzza_management_app.ingredient.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientRequest;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientResponse;
import shengov.bg.pizzza_management_app.ingredient.model.IngredientEntity;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IngredientMapper {

  IngredientEntity requestToIngredient(IngredientRequest request);

  IngredientResponse ingredientToResponse(IngredientEntity ingredient);
}
