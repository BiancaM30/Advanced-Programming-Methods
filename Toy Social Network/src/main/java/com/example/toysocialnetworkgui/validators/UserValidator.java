package com.example.toysocialnetworkgui.validators;

import com.example.toysocialnetworkgui.domain.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
FirstName and LastName can't be null
Names should contain only letters. First letter should be capitalized.
Gender should be man/woman
birthday = ?
location can't be null
email can't be null and should be in the specific format
 */
public class UserValidator implements Validator<User> {
    private void validateNameFormat(String name) throws ValidationException {
        Pattern namePattern = Pattern.compile("^[A-Z]{0,}[a-z]{0,}$");
        Matcher matcher = namePattern.matcher(name);
        if (!matcher.matches())
            throw new ValidationException("Invalid name format!");
    }

    private void validateFirstName(User entity) throws ValidationException {
        if (entity.getFirstName() == "")
            throw new ValidationException("FirstName can't be empty!");

        validateNameFormat(entity.getFirstName());
    }

    private void validateLastName(User entity) throws ValidationException {
        if (entity.getLastName() == "")
            throw new ValidationException("FirstName can't be empty!");

        validateNameFormat(entity.getLastName());
    }

    private void validateGender(User entity) throws ValidationException {
        if (!(entity.getGender().equals("man")) && !(entity.getGender().equals("woman"))) {
            throw new ValidationException("Invalid gender!");
        }
    }

    private void validateLocation(User entity) throws ValidationException {
        if (entity.getLocation() == "")
            throw new ValidationException("Location can't be empty!");
    }

    private void validateEmail(User entity) throws ValidationException {
        if (entity.getEmail() == null)
            throw new ValidationException("Email can't be null!");

        String email = entity.getEmail();
        Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+\\/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
        Matcher matcher = emailPattern.matcher(email);
        if (!matcher.matches())
            throw new ValidationException("Invalid email format!");
    }

    @Override
    public void validate(User entity) throws ValidationException {
        this.validateFirstName(entity);
        this.validateLastName(entity);
        this.validateGender(entity);
        this.validateLocation(entity);
        this.validateEmail(entity);
    }
}

