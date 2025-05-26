package org.studentmanagement.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.studentmanagement.data.bindingModels.AddClassBindingModel;
import org.studentmanagement.data.bindingModels.AddStudentBindingModel;
import org.studentmanagement.data.entities.ClassEntity;
import org.studentmanagement.data.entities.UserEntity;
import org.studentmanagement.data.enums.RoleEnum;
import org.studentmanagement.data.repositories.ClassRepository;
import org.studentmanagement.data.repositories.UserRepository;
import org.studentmanagement.data.viewModels.ClassViewModel;
import org.studentmanagement.data.viewModels.UserViewModel;
import org.studentmanagement.testUtilities.BaseTest;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class ClassControllerTests extends BaseTest {
    @Autowired
    ClassRepository classRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    Gson gson;

    @Nested
    @ActiveProfiles("test")
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    class WithSetUp {
        @BeforeEach()
        public void setUp() throws Exception {
            authorize(RoleEnum.TEACHER);
        }

        @Test
        void addClassSuccessful() throws Exception {
            AddClassBindingModel model = AddClassBindingModel
                    .builder()
                    .title("testTitle")
                    .description("testDescription")
                    .build();

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

            Assertions.assertEquals(1, classRepository.count());
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

            Assertions.assertArrayEquals(new String[]{"Title should not be empty"}, mappedResult);
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
            mockMvc.perform(get("/class/123")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound());
        }

        @Test
        void setClassTeacher() throws Exception {
            addTestClass();
            UserEntity user = addTestUser(RoleEnum.TEACHER);
            AddClassBindingModel model = AddClassBindingModel
                    .builder()
                    .teacherId(user.getId())
                    .build();

            MvcResult result = mockMvc.perform(patch("/class/1")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(model)))
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
            AddClassBindingModel model = AddClassBindingModel
                    .builder()
                    .teacherId(user.getId())
                    .build();

            mockMvc.perform(patch("/class/100")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(model)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void setClassTeacherNonExistentTeacher() throws Exception {
            addTestClass();
            AddClassBindingModel model = AddClassBindingModel
                    .builder()
                    .teacherId(100L)
                    .build();

            MvcResult result = mockMvc.perform(patch("/class/1")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(model)))
                    .andExpect(status().isNotFound())
                    .andReturn();

            Assertions.assertTrue(result.getResponse().getContentAsString().isEmpty());
        }

        @Test
        void setClassTeacherUserNotTeacherRole() throws Exception {
            addTestClass();
            UserEntity user = addTestUser(RoleEnum.STUDENT);
            AddClassBindingModel model = AddClassBindingModel
                    .builder()
                    .teacherId(user.getId())
                    .build();

            MvcResult result = mockMvc.perform(patch("/class/1")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(model)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            Assertions.assertEquals("User is not a teacher", result.getResponse().getContentAsString());
        }

        @Test
        void addStudentToClass() throws Exception {
            addTestClass();
            UserEntity student = addTestUser(RoleEnum.STUDENT);
            AddStudentBindingModel model = new AddStudentBindingModel(student.getId());

            MvcResult result = mockMvc.perform(post("/class/1")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(model)))
                    .andExpect(status().isOk())
                    .andReturn();

            UserViewModel mappedStudent = objectMapper.convertValue(student, UserViewModel.class);
            ClassViewModel mappedResult = objectMapper
                    .readValue(result.getResponse().getContentAsString(), ClassViewModel.class);

            Assertions.assertArrayEquals(mappedResult.getStudents(), new UserViewModel[]{mappedStudent});
        }

        @Test
        void addStudentToClassNonExistentClass() throws Exception {
            UserEntity user = addTestUser(RoleEnum.STUDENT);
            AddStudentBindingModel model = new AddStudentBindingModel(user.getId());

            MvcResult result = mockMvc.perform(post("/class/100")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(model)))
                    .andExpect(status().isNotFound())
                    .andReturn();

            Assertions.assertTrue(result.getResponse().getContentAsString().isEmpty());
        }

        @Test
        void addStudentToClassNonExistentUser() throws Exception {
            addTestClass();
            AddStudentBindingModel model = new AddStudentBindingModel(100L);

            MvcResult result = mockMvc.perform(post("/class/1")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(model)))
                    .andExpect(status().isNotFound())
                    .andReturn();

            Assertions.assertTrue(result.getResponse().getContentAsString().isEmpty());
        }

        @Test
        void addStudentToClassUserNotStudentRole() throws Exception {
            addTestClass();
            UserEntity user = addTestUser(RoleEnum.TEACHER);
            AddStudentBindingModel model = new AddStudentBindingModel(user.getId());

            MvcResult result = mockMvc.perform(post("/class/1")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(model)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            Assertions.assertEquals("User with id "+ user.getId() +" is not a student", result.getResponse().getContentAsString());
        }
    }

    @Nested
    @ActiveProfiles("test")
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    class WithoutSetUp {
        @Test
        void getUserClassesForTeacher() throws Exception {
            authorize(RoleEnum.TEACHER);
            ClassEntity validClass = addTestClass(user, new UserEntity[0]);
            addTestClass(null, new UserEntity[]{user});

            MvcResult result = mockMvc.perform(get("/class")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();

            ClassViewModel[] mappedResult = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    ClassViewModel[].class
            );

            Assertions.assertEquals(1, mappedResult.length);
            Assertions.assertEquals(validClass.getId(), mappedResult[0].getId());
        }

        @Test
        void getUserClassesForStudent() throws Exception {
            authorize(RoleEnum.STUDENT);
            ClassEntity validClass = addTestClass(null, new UserEntity[]{user});
            addTestClass(user, new UserEntity[0]);

            MvcResult result = mockMvc.perform(get("/class")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();

            ClassViewModel[] mappedResult = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    ClassViewModel[].class
            );

            Assertions.assertEquals(1, mappedResult.length);
            Assertions.assertEquals(validClass.getId(), mappedResult[0].getId());
        }
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

    private void addTestClass() {
        addTestClass(null, new UserEntity[]{});
    }

    private ClassEntity addTestClass(UserEntity teacher, UserEntity[] students) {
        Random random = new Random();
        int randomNumber = random.nextInt(100);

        ClassEntity clazz = new ClassEntity(
                "test" + randomNumber,
                "",
                teacher,
                new LinkedList<>(Arrays.stream(students).toList())
        );

        classRepository.save(clazz);

        return clazz;
    }
}
