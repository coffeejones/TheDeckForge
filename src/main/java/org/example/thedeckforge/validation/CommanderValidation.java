package org.example.thedeckforge.validation;

import org.example.thedeckforge.entity.Deck;
import org.example.thedeckforge.entity.enums.FormatType;
import org.example.thedeckforge.validation.exceptions.CardValidationException;
import org.springframework.stereotype.Component;

@Component("COMMANDER")
public class CommanderValidation implements ValidationStrategy {
    @Override
    public ValidationType getValidationType() {
        return ValidationType.COMMANDER;
    }

    @Override
    public void validate(Object object) {
        if(!(object instanceof Deck deck)){
            throw new CardValidationException("Invalid Type For Validation");
        }
        if(deck.getFormat() != FormatType.COMMANDER){
            throw new CardValidationException("Format type must be COMMANDER");
        }
        if(deck.getCards().size() < 100){
            throw new CardValidationException("Deck Limit Reached");
        }
    }
}
