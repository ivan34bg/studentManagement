package org.studentmanagement.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.studentmanagement.data.repositories.UserRepository;

import java.util.Arrays;

@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addUserSuccessfully() throws Exception {
        MultiValueMap<String, String> newUser = new LinkedMultiValueMap<>();
        newUser.add("username", "testUsername");
        newUser.add("firstName", "testFirstName");
        newUser.add("lastName", "testLastName");

        mockMvc.perform(post("/user")
                        .params(newUser))
                .andExpectAll(
                        status().isCreated(),
                        content().contentType("application/json"),
                        jsonPath("$.id").value("1"),
                        jsonPath("$.firstName").value("testFirstName"),
                        jsonPath("$.lastName").value("testLastName"),
                        jsonPath("$.role").value("PENDING")
                );

        Assertions.assertEquals(userRepository.count(), 1);
    }

    @Test
    void addUserNonUnique() throws Exception {
        MultiValueMap<String, String> newUser = new LinkedMultiValueMap<>();
        newUser.add("username", "testUsername");
        newUser.add("firstName", "testFirstName");
        newUser.add("lastName", "testLastName");

        mockMvc.perform(post("/user")
                        .params(newUser))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/user").params(newUser))
                .andExpectAll(status().isConflict());

        Assertions.assertEquals(userRepository.count(), 1);
    }

    @Test
    void addUserEmptyFields() throws Exception {
        MultiValueMap<String, String> user = new LinkedMultiValueMap<>();

        MvcResult result = mockMvc.perform(post("/user")
                        .params(user))
                .andExpect(status().isBadRequest())
                .andReturn();

        String[] mappedResult = objectMapper.readValue(result.getResponse().getContentAsString(), String[].class);
        String[] sortedMappedResult = Arrays.stream(mappedResult).sorted().toArray(String[]::new);

        String[] expectedResult = Arrays.stream(new String[]{
                "Username should not be empty",
                "First name should not be empty",
                "Last name should not be empty"
        }).sorted().toArray(String[]::new);

        Assertions.assertArrayEquals(sortedMappedResult, expectedResult);
    }

    @Test
    void addUserLessThanThreeSymbolsFields() throws Exception {
        MultiValueMap<String, String> user = new LinkedMultiValueMap<>();
        user.add("username", "1");
        user.add("firstName", "1");
        user.add("lastName", "1");

        MvcResult result = mockMvc.perform(post("/user")
                        .params(user))
                .andExpect(status().isBadRequest())
                .andReturn();

        String[] mappedResult = objectMapper.readValue(result.getResponse().getContentAsString(), String[].class);
        String[] expectedResult = new String[]{
                "Username should have at least 3 symbols"
        };

        Assertions.assertArrayEquals(mappedResult, expectedResult);
    }

    @Test
    void getUserSuccessfully() throws Exception {
        MultiValueMap<String, String> newUser = new LinkedMultiValueMap<>();
        newUser.add("username", "testUsername");
        newUser.add("firstName", "testFirstName");
        newUser.add("lastName", "testLastName");

        mockMvc.perform(post("/user")
                .params(newUser));

        mockMvc.perform(get("/user/1"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json"),
                        jsonPath("$.id").value("1"),
                        jsonPath("$.firstName").value("testFirstName"),
                        jsonPath("$.lastName").value("testLastName"),
                        jsonPath("$.role").value("PENDING")
                );
    }

    @Test
    void getUserNonExistent() throws Exception {
        MvcResult result = mockMvc.perform(get("/user/1"))
                .andExpect(status().isNotFound())
                .andReturn();

        Assertions.assertTrue(result.getResponse().getContentAsString().isEmpty());
    }
}
