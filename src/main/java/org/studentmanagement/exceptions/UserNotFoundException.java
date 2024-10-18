package org.studentmanagement.exceptions;

import lombok.experimental.StandardException;

@StandardException
public class UserNotFoundException extends Exception {
    public UserNotFoundException() {
        super("User not found");
    }
}
