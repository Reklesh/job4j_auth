package ru.job4j.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.auth.WebSecurity;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.repository.person.MemoryUserRepository;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(WebSecurity.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MemoryUserRepository users;

    @MockitoBean
    private BCryptPasswordEncoder encoder;

    @Test
    void whenSignUpWithValidDataThenReturnOk() throws Exception {
        var person = new Person();
        person.setLogin("admin");
        person.setPassword("password");

        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        doNothing().when(users).save(any(Person.class));

        mockMvc.perform(post("/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void whenSignUpWithoutPasswordThenReturnBadRequest() throws Exception {
        var person = new Person();
        person.setLogin("admin");

        mockMvc.perform(post("/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].password").exists())
                .andExpect(jsonPath("$[0].password")
                        .value(containsString("Password must be empty")));
    }

    @Test
    void whenSignUpWithoutLoginThenReturnBadRequest() throws Exception {
        var person = new Person();
        person.setPassword("password");

        mockMvc.perform(post("/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].login").exists())
                .andExpect(jsonPath("$[0].login")
                        .value(containsString("Username must be empty")));
    }

    @Test
    void whenSignUpWithShortPasswordThenReturnBadRequest() throws Exception {
        var person = new Person();
        person.setLogin("admin");
        person.setPassword("12345");

        mockMvc.perform(post("/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].password").exists())
                .andExpect(jsonPath("$[0].password")
                        .value(containsString("Password length must be at least 6 characters")));
    }
}