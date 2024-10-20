package org.studentmanagement.services;

import org.springframework.stereotype.Service;
import org.studentmanagement.data.enums.RoleEnum;

@Service
public interface RoleService {
    RoleEnum[] getRoles();
}
