package org.example.thedeckforge.validation;

import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.entity.enums.Roles;
import org.example.thedeckforge.validation.exceptions.ValidationException;
import org.springframework.stereotype.Component;

@Component("ADMIN")
public class AdminRoleValidation implements ValidationStrategy{
    @Override
    public ValidationType getValidationType() {
        return ValidationType.ADMINROLE;
    }

    @Override
    public void validate(Object object) {
        if(!(object instanceof User user)){
            throw new ValidationException("Invalid validation type chosen");
        }
        if(!(user.getAuthority().getRole() == Roles.ADMIN)){
            throw new ValidationException("Invalid User role");
        }
    }
}
