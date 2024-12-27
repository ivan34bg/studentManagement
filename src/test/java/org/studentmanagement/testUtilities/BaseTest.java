package org.studentmanagement.testUtilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.studentmanagement.data.bindingModels.LoginBindingModel;
import org.studentmanagement.data.entities.UserEntity;
import org.studentmanagement.data.enums.RoleEnum;
import org.studentmanagement.data.repositories.UserRepository;

import java.util.LinkedList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BaseTest {
    @Autowired
    public MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    public String token;

    public void authorize(RoleEnum role) throws Exception {
        userRepository.save(new UserEntity(
                "loggedInUser@test.com",
                "$2a$10$gL473jsyStv7IUj1wl7PI.BmLutLl3rPBItcKv0pmkTZle4zJTCW2",
                "test",
                "test",
                role,
                new LinkedList<>()
        ));

        LoginBindingModel model = new LoginBindingModel("loggedInUser@test.com", "test1234");

        Gson gson = new Gson();
        String jsonUserDetails = gson.toJson(model);

        token = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUserDetails))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}
