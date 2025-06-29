package org.studentmanagement.services.implementations;

import org.springframework.stereotype.Service;
import org.studentmanagement.data.enums.RoleEnum;
import org.studentmanagement.exceptions.EntityNotFoundException;
import org.studentmanagement.services.RoleService;

@Service
public class RoleServiceImpl implements RoleService {
    @Override
    public RoleEnum[] getRoles() {
        return RoleEnum.values();
    }

    @Override
    public RoleEnum getRole(String roleName) throws EntityNotFoundException {
        try {
            return RoleEnum.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(roleName);
        }
    }
}
