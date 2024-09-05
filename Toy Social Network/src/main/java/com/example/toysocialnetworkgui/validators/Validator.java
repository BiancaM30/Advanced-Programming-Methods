package com.example.toysocialnetworkgui.validators;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;

}

