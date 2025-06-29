package org.studentmanagement.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.studentmanagement.data.enums.RoleEnum;
import org.studentmanagement.exceptions.EntityNotFoundException;
import org.studentmanagement.services.implementations.RoleServiceImpl;

@SpringBootTest
class RoleServiceTests {
    RoleService roleService;

    @BeforeEach
    void setUp() {
        roleService = new RoleServiceImpl();
    }

    @Test
    void testGetRoles() {
        RoleEnum[] result = roleService.getRoles();

        Assertions.assertArrayEquals(result, RoleEnum.values());
    }

    @Test
    void testGetRole() throws EntityNotFoundException {
        for (RoleEnum role : RoleEnum.values()) {
            RoleEnum result = RoleEnum.valueOf(role.name());
            Assertions.assertEquals(result, role);
        }
    }
}
