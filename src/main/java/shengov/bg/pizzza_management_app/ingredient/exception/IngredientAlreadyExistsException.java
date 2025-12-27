package shengov.bg.pizzza_management_app.ingredient.exception;

import static shengov.bg.pizzza_management_app.ingredient.constant.IngredientConstants.INGREDIENT_ALREADY_EXIST_MESSAGE;

public class IngredientAlreadyExistsException extends RuntimeException {

    public IngredientAlreadyExistsException(String name) {
        super(INGREDIENT_ALREADY_EXIST_MESSAGE.formatted(name));
    }
}
