package com.example.toysocialnetworkgui.validators;

import com.example.toysocialnetworkgui.domain.Event;
import com.example.toysocialnetworkgui.domain.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventValidator implements Validator<Event> {

    private void validateLocation(Event entity) throws ValidationException {
        if (entity.getLocation() == "")
            throw new ValidationException("FirstName can't be empty!");
          }

    private void validateName(Event entity) throws ValidationException {
        if (entity.getName() == "")
            throw new ValidationException("FirstName can't be empty!");
    }


    @Override
    public void validate(Event entity) throws ValidationException {
        this.validateLocation(entity);
        this.validateName(entity);

    }
}


