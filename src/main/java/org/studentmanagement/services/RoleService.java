package org.studentmanagement.services;

import org.springframework.stereotype.Service;
import org.studentmanagement.data.enums.RoleEnum;
import org.studentmanagement.exceptions.EntityNotFoundException;

@Service
public interface RoleService {
    RoleEnum[] getRoles();
    RoleEnum getRole(String roleName) throws EntityNotFoundException;
}
