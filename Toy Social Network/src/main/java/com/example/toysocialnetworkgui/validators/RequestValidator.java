package com.example.toysocialnetworkgui.validators;

import com.example.toysocialnetworkgui.domain.FriendshipRequest;

public class RequestValidator implements Validator<FriendshipRequest> {
    @Override
    public void validate(FriendshipRequest entity) throws ValidationException {
        if (entity.getId().getLeftMember() <= 0L) {
            throw new ValidationException("First id is invalid!\n");
        }
        if (entity.getId().getRightMember() <= 0L) {
            throw new ValidationException("Second id is invalid!\n");
        }
    }
}
