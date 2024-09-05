package com.example.toysocialnetworkgui.validators;

import com.example.toysocialnetworkgui.domain.Friendship;

public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship entity) throws ValidationException {
        if (entity.getId().getLeftMember() <= 0L) {
            throw new ValidationException("First id is invalid!\n");
        }
        if (entity.getId().getRightMember() <= 0L) {
            throw new ValidationException("Second id is invalid!\n");
        }
    }
}
