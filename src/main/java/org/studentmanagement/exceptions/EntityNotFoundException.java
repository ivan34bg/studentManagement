package org.studentmanagement.exceptions;

import lombok.experimental.StandardException;

@StandardException
public class EntityNotFoundException extends Exception {
    public EntityNotFoundException() { super(); }
}
