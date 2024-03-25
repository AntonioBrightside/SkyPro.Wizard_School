package com.amam.wizardschool.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class AvatarNotFoundException extends Exception {
    public AvatarNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "Аватар с таким ID не найден";
    }
}
