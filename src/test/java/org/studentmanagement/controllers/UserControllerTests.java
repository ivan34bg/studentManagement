package org.studentmanagement.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.studentmanagement.data.bindingModels.RegisterUserBindingModel;
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
    @Autowired
    private Gson gson;

    @Test
    void addUserSuccessfully() throws Exception {
        RegisterUserBindingModel model = new RegisterUserBindingModel(
                "test@test.com",
                "testtest",
                "testFirstName",
                "testLastName"
        );

        String jsonModel = gson.toJson(model);

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonModel))
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

        RegisterUserBindingModel model = new RegisterUserBindingModel(
                user.getEmail(),
                "testFirstName",
                "testLastName",
                "testtest"
        );

        String jsonModel = gson.toJson(model);

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonModel))
                .andExpectAll(status().isConflict());

        Assertions.assertEquals(userRepository.count(), 1);
    }

    @Test
    void addUserEmptyFields() throws Exception {
        String jsonModel = gson.toJson(new RegisterUserBindingModel());

        MvcResult result = mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonModel))
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
        RegisterUserBindingModel model = new RegisterUserBindingModel("", "1", "1", "1");
        String jsonModel = gson.toJson(model);

        MvcResult result = mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonModel))
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
