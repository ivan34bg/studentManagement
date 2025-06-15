package org.studentmanagement.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.studentmanagement.data.enums.RoleEnum;
import org.studentmanagement.services.implementations.RoleServiceImpl;

@SpringBootTest
public class RoleServiceTests {
    RoleService roleService;

    @BeforeEach
    void setUp() {
        roleService = new RoleServiceImpl();
    }

    @Test
    public void testGetRoles() {
        RoleEnum[] result = roleService.getRoles();

        Assertions.assertArrayEquals(result, RoleEnum.values());
    }
}
