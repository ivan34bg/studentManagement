package org.studentmanagement.exceptions;

import lombok.experimental.StandardException;

@StandardException
public class RoleRequirementViolationException extends Exception {
    public RoleRequirementViolationException(String message) { super(message); }
}
