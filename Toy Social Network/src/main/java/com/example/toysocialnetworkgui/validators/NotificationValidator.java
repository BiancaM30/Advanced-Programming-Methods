package com.example.toysocialnetworkgui.validators;

import com.example.toysocialnetworkgui.domain.Notification;

public class NotificationValidator implements Validator<Notification> {
    @Override
    public void validate(Notification entity) throws ValidationException {
        if (entity.getId().getLeftMember() <= 0L) {
            throw new ValidationException("First id is invalid!\n");
        }
        if (entity.getId().getRightMember() <= 0L) {
            throw new ValidationException("Second id is invalid!\n");
        }
    }
}
