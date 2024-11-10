package org.studentmanagement.testUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
    private final MultiValueMap<String, String> userDetails = new LinkedMultiValueMap<>(){{
        add("email", "loggedInUser@test.com");
        add("password", "test1234");
    }};

    public void authorize(RoleEnum role) throws Exception {
        userRepository.save(new UserEntity(
                "loggedInUser@test.com",
                "$2a$10$gL473jsyStv7IUj1wl7PI.BmLutLl3rPBItcKv0pmkTZle4zJTCW2",
                "test",
                "test",
                role,
                new LinkedList<>()
        ));

        token = mockMvc.perform(post("/login")
                        .params(userDetails))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}
