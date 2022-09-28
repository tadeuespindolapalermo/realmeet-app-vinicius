package br.com.sw2you.realmeet.exception;

import br.com.sw2you.realmeet.validator.ValidationError;
import br.com.sw2you.realmeet.validator.ValidationErrors;
import br.com.sw2you.realmeet.validator.ValidatorConstants;

public class InvalidOrderByFieldException extends InvalidRequestException {

    public InvalidOrderByFieldException() {
        super(
            new ValidationError(ValidatorConstants.ORDER_BY, ValidatorConstants.ORDER_BY + ValidatorConstants.INVALID)
        );
    }
}
