package ru.job4j.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.auth.WebSecurity;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.repository.person.PersonRepository;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonController.class)
@Import(WebSecurity.class)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PersonRepository persons;

    @MockitoBean
    private BCryptPasswordEncoder encoder;

    @Test
    @WithMockUser
    void whenFindByIdWithExistingIdThenReturnPerson() throws Exception {
        var person = new Person(1, "admin", "password");
        when(persons.findById(anyInt())).thenReturn(Optional.of(person));

        mockMvc.perform(get("/person/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value("admin"))
                .andExpect(jsonPath("$.password").value("password"));
    }

    @Test
    @WithMockUser
    void whenFindByIdWithNonExistingIdThenReturnNotFound() throws Exception {
        when(persons.findById(anyInt())).thenReturn(Optional.empty());

        mockMvc.perform(get("/person/4"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(status().reason("User with ID 4 not found"));
    }

    @Test
    @WithMockUser
    void whenCreateValidPersonThenReturnCreatedPerson() throws Exception {
        var person = new Person();
        person.setLogin("admin");
        person.setPassword("password");
        var savedPerson = new Person(1, "admin", "password");

        when(persons.save(any(Person.class))).thenReturn(savedPerson);

        mockMvc.perform(post("/person/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value("admin"))
                .andExpect(jsonPath("$.password").value("password"));
    }

    @Test
    @WithMockUser
    void whenCreatePersonWithoutPasswordThenReturnBadRequest() throws Exception {
        var person = new Person();
        person.setLogin("admin");

        mockMvc.perform(post("/person/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].password").exists())
                .andExpect(jsonPath("$[0].password")
                        .value(containsString("Password must be empty")));
    }

    @Test
    @WithMockUser
    void whenCreatePersonWithoutLoginThenReturnBadRequest() throws Exception {
        var person = new Person();
        person.setPassword("password");

        mockMvc.perform(post("/person/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].login").exists())
                .andExpect(jsonPath("$[0].login")
                        .value(containsString("Username must be empty")));
    }

    @Test
    @WithMockUser
    void whenUpdateValidPersonThenReturnOk() throws Exception {
        var person = new Person(1, "admin", "password");
        when(persons.save(any(Person.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/person/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void whenUpdatePersonWithoutPasswordThenReturnBadRequest() throws Exception {
        var person = new Person();
        person.setId(5);
        person.setLogin("admin");

        mockMvc.perform(put("/person/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].password").exists())
                .andExpect(jsonPath("$[0].password")
                        .value(containsString("Password must be empty")));
    }

    @Test
    @WithMockUser
    void whenUpdatePersonWithoutLoginThenReturnBadRequest() throws Exception {
        var person = new Person();
        person.setId(5);
        person.setPassword("password");

        mockMvc.perform(put("/person/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].login").exists())
                .andExpect(jsonPath("$[0].login")
                        .value(containsString("Username must be empty")));
    }

    @Test
    @WithMockUser
    void whenUpdatePersonWithNullIdThenReturnBadRequest() throws Exception {
        var person = new Person();
        person.setLogin("admin");
        person.setPassword("password");

        mockMvc.perform(put("/person/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].id")
                        .value(containsString("Id must be non null")));
    }
}
