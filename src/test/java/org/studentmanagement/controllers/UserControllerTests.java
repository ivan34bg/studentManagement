package org.studentmanagement.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import org.studentmanagement.data.entities.UserEntity;
import org.studentmanagement.data.enums.RoleEnum;
import org.studentmanagement.data.repositories.UserRepository;
import org.studentmanagement.data.viewModels.UserViewModel;
import org.studentmanagement.testUtilities.BaseTest;

import java.util.Arrays;

@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTests extends BaseTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addUserSuccessfully() throws Exception {
        MultiValueMap<String, String> newUser = new LinkedMultiValueMap<>();
        newUser.add("email", "test@test.com");
        newUser.add("firstName", "testFirstName");
        newUser.add("lastName", "testLastName");
        newUser.add("password", "testtest");

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
        UserEntity user = addTestUser();

        MultiValueMap<String, String> newUser = new LinkedMultiValueMap<>();
        newUser.add("email", user.getEmail());
        newUser.add("firstName", "testFirstName");
        newUser.add("lastName", "testLastName");
        newUser.add("password", "testtest");

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
                "Email should not be empty",
                "First name should not be empty",
                "Last name should not be empty",
                "Password should not be empty"
        }).sorted().toArray(String[]::new);

        Assertions.assertArrayEquals(sortedMappedResult, expectedResult);
    }

    @Test
    void addUserLessThanThreeSymbolsFields() throws Exception {
        MultiValueMap<String, String> user = new LinkedMultiValueMap<>();
        user.add("email", "");
        user.add("firstName", "1");
        user.add("lastName", "1");
        user.add("password", "1");

        MvcResult result = mockMvc.perform(post("/user")
                        .params(user))
                .andExpect(status().isBadRequest())
                .andReturn();

        String[] mappedResult = objectMapper.readValue(result.getResponse().getContentAsString(), String[].class);
        String[] sortedMappedResult = Arrays.stream(mappedResult).sorted().toArray(String[]::new);

        String[] expectedResult = Arrays.stream(new String[]{
                "Email is invalid",
                "Email should not be empty",
                "Password cannot be less than 8 symbols"
        }).sorted().toArray(String[]::new);

        Assertions.assertArrayEquals(sortedMappedResult, expectedResult);
    }

    @Test
    void getUserSuccessfully() throws Exception {
        UserEntity user = addTestUser();
        UserViewModel mappedUser = objectMapper.convertValue(user, UserViewModel.class);

        authorize(RoleEnum.ADMIN);

        MvcResult getResult = mockMvc
                .perform(get("/user/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        UserViewModel mappedResult = objectMapper
                .readValue(getResult.getResponse().getContentAsString(), UserViewModel.class);

        Assertions.assertEquals(mappedResult, mappedUser);
    }

    @Test
    void getUserNonExistent() throws Exception {
        authorize(RoleEnum.ADMIN);
        MvcResult result = mockMvc.perform(get("/user/100")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andReturn();

        Assertions.assertTrue(result.getResponse().getContentAsString().isEmpty());
    }

    @Test
    void setUserRole() throws Exception {
        addTestUser();

        authorize(RoleEnum.ADMIN);

        MvcResult result = mockMvc.perform(patch("/user/1/role")
                        .content("ADMIN")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        UserViewModel mappedResult = objectMapper
                .readValue(result.getResponse().getContentAsString(), UserViewModel.class);

        Assertions.assertEquals(mappedResult.getRole(), RoleEnum.ADMIN);
    }

    private UserEntity addTestUser() {
        UserEntity user = new UserEntity();
        user.setEmail("test@test.com");
        user.setFirstName("test");
        user.setLastName("test");
        user.setPassword("testtest");

        userRepository.save(user);

        return user;
    }
}
