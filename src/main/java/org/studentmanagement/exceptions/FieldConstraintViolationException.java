package org.studentmanagement.exceptions;

import lombok.Getter;
import lombok.experimental.StandardException;

@StandardException
@Getter
public class FieldConstraintViolationException extends Exception {
    private String[] messages;
    public FieldConstraintViolationException(String[] messages) {
        super();
        this.messages = messages;
    }

    @Override
    public String getMessage() {
        throw new UnsupportedOperationException();
    }
}
