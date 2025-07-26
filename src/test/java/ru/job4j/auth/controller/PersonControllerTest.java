package ru.job4j.auth.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.repository.person.PersonRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class PersonControllerTest {

    private PersonRepository persons;
    private PersonController controller;

    @BeforeEach
    public void initServices() {
        persons = mock(PersonRepository.class);
        controller = new PersonController(persons);
    }

    @Test
    public void whenFindByIdWithExistingIdThenReturnPerson() {
        var person = new Person(1, "admin", "password");
        when(persons.findById(anyInt())).thenReturn(Optional.of(person));

        var responseEntity = controller.findById(1);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(person);
    }

    @Test
    public void whenFindByIdWithNonExistingIdThenThrowResponseStatusException() {
        when(persons.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.findById(4))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User with ID 4 not found");
    }

    @Test
    public void whenCreateValidPersonThenReturnCreatedPerson() {
        var person = new Person();
        person.setLogin("admin");
        person.setPassword("password");
        var person2 = new Person(1, "admin", "password");
        when(persons.save(any())).thenReturn(person2);

        var responseEntity = controller.create(person);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isEqualTo(person2);
    }

    @Test
    public void whenCreatePersonWithoutPasswordThenThrowNullPointerException() {
        var person = new Person();
        person.setLogin("admin");

        assertThatThrownBy(() -> controller.create(person))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Username and password mustn't be empty");
    }

    @Test
    public void whenCreatePersonWithoutLoginThenThrowNullPointerException() {
        var person = new Person();
        person.setPassword("password");

        assertThatThrownBy(() -> controller.create(person))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Username and password mustn't be empty");
    }

    @Test
    public void whenUpdateValidPersonThenReturnOk() {
        var person = new Person();
        person.setLogin("admin");
        person.setPassword("password");
        when(persons.save(any())).thenAnswer(
                invocation -> invocation.getArgument(0));

        var responseEntity = controller.update(person);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(persons).save(person);
    }

    @Test
    public void whenUpdatePersonWithoutPasswordThenThrowNullPointerException() {
        var person = new Person();
        person.setLogin("admin");

        assertThatThrownBy(() -> controller.update(person))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Username and password mustn't be empty");
    }

    @Test
    public void whenUpdatePersonWithoutLoginThenThrowNullPointerException() {
        var person = new Person();
        person.setPassword("password");

        assertThatThrownBy(() -> controller.update(person))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Username and password mustn't be empty");
    }
}