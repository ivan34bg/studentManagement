package org.studentmanagement.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.studentmanagement.data.enums.RoleEnum;
import org.studentmanagement.testUtilities.BaseTest;

import java.util.prefs.BackingStoreException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class RoleControllerTests extends BaseTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getRoles() throws Exception {
        authorize(RoleEnum.ADMIN);

        MvcResult result = mockMvc
                .perform(get("/roles")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        RoleEnum[] mappedResult = objectMapper.readValue(result.getResponse().getContentAsString(), RoleEnum[].class);
        Assertions.assertArrayEquals(mappedResult, RoleEnum.values());
    }
}
