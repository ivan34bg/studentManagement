package org.studentmanagement.services.implementations;

import org.springframework.stereotype.Service;
import org.studentmanagement.data.enums.RoleEnum;
import org.studentmanagement.services.RoleService;

@Service
public class RoleServiceImpl implements RoleService {
    @Override
    public RoleEnum[] getRoles() {
        return RoleEnum.values();
    }
}
