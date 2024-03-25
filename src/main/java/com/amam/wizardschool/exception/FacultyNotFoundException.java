package com.amam.wizardschool.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class FacultyNotFoundException extends RuntimeException {
    public FacultyNotFoundException(String string) {
        super(string);
    }

    @Override
    public String getMessage() {
        return "Факультет с таким ID не найден";
    }
}
