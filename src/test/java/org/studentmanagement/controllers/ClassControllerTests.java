package org.studentmanagement.controllers;

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
import org.studentmanagement.data.repositories.ClassRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ClassControllerTests {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ClassRepository classRepository;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void addClassSuccessful() throws Exception {
        MultiValueMap<String, String> newClass = new LinkedMultiValueMap<>();
        newClass.add("title", "testTitle");
        newClass.add("description", "testDescription");

        mockMvc
                .perform(post("/class")
                        .params(newClass))
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
        MultiValueMap<String, String> newClass = new LinkedMultiValueMap<>();
        newClass.add("description", "testDescription");

        MvcResult result = mockMvc
                .perform(post("/class")
                        .params(newClass))
                .andExpect(status().isBadRequest())
                .andReturn();

        String[] mappedResult = objectMapper.readValue(result.getResponse().getContentAsString(), String[].class);

        Assertions.assertArrayEquals(mappedResult, new String[]{"Title should not be empty"});
    }

    @Test
    void getClassSuccessful() throws Exception {
        MultiValueMap<String, String> newClass = new LinkedMultiValueMap<>();
        newClass.add("title", "testTitle");
        newClass.add("description", "testDescription");

        MvcResult postResult = mockMvc
                .perform(post("/class")
                        .params(newClass))
                .andReturn();

        MvcResult getResult = mockMvc
                .perform(get("/class/1"))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals(postResult.getResponse().getContentAsString(), getResult.getResponse().getContentAsString());
    }

    @Test
    void getClassNonExistent() throws Exception {
        MvcResult result = mockMvc
                .perform(get("/class/123"))
                .andExpect(status().isNotFound())
                .andReturn();

        Assertions.assertTrue(result.getResponse().getContentAsString().isEmpty());
    }
}
