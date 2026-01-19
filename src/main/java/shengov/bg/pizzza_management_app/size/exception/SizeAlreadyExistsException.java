package shengov.bg.pizzza_management_app.size.exception;

import static shengov.bg.pizzza_management_app.size.constant.SizeConstants.SIZE_ALREADY_EXIST_MESSAGE;

public class SizeAlreadyExistsException extends RuntimeException{

    public SizeAlreadyExistsException(String name) {
        super(SIZE_ALREADY_EXIST_MESSAGE.formatted(name));
    }

}
