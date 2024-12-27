package org.studentmanagement.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.studentmanagement.data.bindingModels.AddClassBindingModel;
import org.studentmanagement.data.entities.ClassEntity;
import org.studentmanagement.data.entities.UserEntity;
import org.studentmanagement.data.enums.RoleEnum;
import org.studentmanagement.data.repositories.ClassRepository;
import org.studentmanagement.data.repositories.UserRepository;
import org.studentmanagement.data.viewModels.ClassViewModel;
import org.studentmanagement.data.viewModels.UserViewModel;
import org.studentmanagement.testUtilities.BaseTest;

import java.util.LinkedList;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ClassControllerTests extends BaseTest {
    @Autowired
    ClassRepository classRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    Gson gson;

    @BeforeEach
    public void setUp() throws Exception {
        authorize(RoleEnum.TEACHER);
    }

    @Test
    void addClassSuccessful() throws Exception {
        AddClassBindingModel model = new AddClassBindingModel("testTitle", "testDescription");
        String jsonModel = gson.toJson(model);

        mockMvc
                .perform(post("/class")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonModel)
                        .header("Authorization", "Bearer " + token))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").value("1"),
                        jsonPath("$.title").value("testTitle"),
                        jsonPath("$.description").value("testDescription")
                );

        Assertions.assertEquals(classRepository.count(), 1);
    }

    @Test
    void addClassNoTitle() throws Exception {
        AddClassBindingModel model = new AddClassBindingModel();
        model.setDescription("testDescription");

        String jsonModel = gson.toJson(model);

        MvcResult result = mockMvc
                .perform(post("/class")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonModel)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andReturn();

        String[] mappedResult = objectMapper.readValue(result.getResponse().getContentAsString(), String[].class);

        Assertions.assertArrayEquals(mappedResult, new String[]{"Title should not be empty"});
    }

    @Test
    void getClassSuccessful() throws Exception {
        ClassEntity classEntity = new ClassEntity();
        classEntity.setTitle("testTitle");
        classEntity.setDescription("testDescription");

        String jsonEntity = gson.toJson(classEntity);

        MvcResult postResult = mockMvc
                .perform(post("/class")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonEntity)
                        .header("Authorization", "Bearer " + token))
                .andReturn();

        MvcResult getResult = mockMvc
                .perform(get("/class/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals(postResult.getResponse().getContentAsString(), getResult.getResponse().getContentAsString());
    }

    @Test
    void getClassNonExistent() throws Exception {
        MvcResult result = mockMvc
                .perform(get("/class/123")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andReturn();

        Assertions.assertEquals(result.getResponse().getContentAsString(), "Class not found");
    }

    @Test
    void setClassTeacher() throws Exception {
        addTestClass();
        UserEntity user = addTestUser(RoleEnum.TEACHER);

        MvcResult result = mockMvc.perform(patch("/class/1/teacher/" + user.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        ClassViewModel mappedResult = objectMapper
                .readValue(result.getResponse().getContentAsString(), ClassViewModel.class);

        UserViewModel mappedUser = objectMapper
                .convertValue(user, UserViewModel.class);

        assert (mappedResult.getTeacher()).equals(mappedUser);
    }

    @Test
    void setClassTeacherNonExistentClass() throws Exception {
        addTestUser(RoleEnum.TEACHER);

        MvcResult result = mockMvc.perform(patch("/class/100/teacher/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andReturn();

        Assertions.assertEquals(result.getResponse().getContentAsString(), "Class not found");
    }

    @Test
    void setClassTeacherNonExistentTeacher() throws Exception {
        addTestClass();

        MvcResult result = mockMvc.perform(patch("/class/1/teacher/100")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andReturn();

        Assertions.assertTrue(result.getResponse().getContentAsString().isEmpty());
    }

    @Test
    void setClassTeacherUserNotTeacherRole() throws Exception {
        addTestClass();
        UserEntity user = addTestUser(RoleEnum.STUDENT);

        MvcResult result = mockMvc.perform(patch("/class/1/teacher/" + user.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andReturn();

        Assertions.assertEquals(result.getResponse().getContentAsString(), "User is not a teacher");
    }

    @Test
    void addStudentToClass() throws Exception {
        addTestClass();
        UserEntity student = addTestUser(RoleEnum.STUDENT);

        MvcResult result = mockMvc.perform(post("/class/1/student/" + student.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        UserViewModel mappedStudent = objectMapper.convertValue(student, UserViewModel.class);
        ClassViewModel mappedResult = objectMapper
                .readValue(result.getResponse().getContentAsString(), ClassViewModel.class);

        Assertions.assertArrayEquals(mappedResult.getStudents(), new UserViewModel[]{mappedStudent});
    }

    @Test
    void addStudentToClassNonExistentClass() throws Exception {
        addTestUser(RoleEnum.STUDENT);

        MvcResult result = mockMvc.perform(post("/class/100/student/100")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andReturn();

        Assertions.assertTrue(result.getResponse().getContentAsString().isEmpty());
    }

    @Test
    void addStudentToClassNonExistentUser() throws Exception {
        addTestClass();

        MvcResult result = mockMvc.perform(post("/class/1/student/100")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andReturn();

        Assertions.assertTrue(result.getResponse().getContentAsString().isEmpty());
    }

    @Test
    void addStudentToClassUserNotStudentRole() throws Exception {
        addTestClass();
        addTestUser(RoleEnum.TEACHER);

        MvcResult result = mockMvc.perform(post("/class/1/student/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andReturn();

        Assertions.assertEquals(result.getResponse().getContentAsString(), "User is not a student");
    }

    private UserEntity addTestUser(RoleEnum role) {
        UserEntity user = new UserEntity();
        user.setEmail("test@test.com");
        user.setFirstName("test");
        user.setLastName("test");
        user.setPassword("testtest");
        user.setRole(role);

        userRepository.save(user);

        return user;
    }

    private ClassEntity addTestClass() {
        Random random = new Random();
        int randomNumber = random.nextInt(100);

        ClassEntity clazz = new ClassEntity(
                "test" + Integer.toString(randomNumber),
                "",
                null,
                new LinkedList<>()
        );

        classRepository.save(clazz);

        return clazz;
    }
}
