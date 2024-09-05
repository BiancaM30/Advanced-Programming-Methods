package com.example.toysocialnetworkgui.validators;

import com.example.toysocialnetworkgui.domain.Message;

public class MessageValidator implements Validator<Message>{
    @Override
    public void validate(Message mess) throws ValidationException
    {
        if(mess.getId()<=0L)
            throw new ValidationException("Id is invalid!");
        if(mess.getTo().size()<1)
            throw new ValidationException("List should contain at least one user!");
        if(mess.getMessage().isEmpty())
            throw new ValidationException("Message should not be empty!");
    }
}
