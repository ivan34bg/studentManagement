package org.studentmanagement.exceptions;

import lombok.experimental.StandardException;

@StandardException
public class UserEntityUniqueConstraintViolationException extends Exception {
    public UserEntityUniqueConstraintViolationException() { super(); }
}
