package ru.job4j.auth.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.repository.person.MemoryUserRepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private MemoryUserRepository users;
    private UserController controller;

    @BeforeEach
    public void initServices() {
        users = mock(MemoryUserRepository.class);
        controller = new UserController(users, new BCryptPasswordEncoder());
    }

    @Test
    public void whenSignUpWithValidPersonThenSaveSuccessfully() {
        var person = new Person();
        person.setLogin("admin");
        person.setPassword("password");
        doNothing().when(users).save(any());

        controller.signUp(person);

        verify(users).save(person);
    }

    @Test
    public void whenSignUpWithoutPasswordThenThrowNullPointerException() {
        var person = new Person();
        person.setLogin("admin");

        assertThatThrownBy(() -> controller.signUp(person))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Username and password mustn't be empty");
    }

    @Test
    public void whenSignUpWithoutLoginThenThrowNullPointerException() {
        var person = new Person();
        person.setPassword("password");

        assertThatThrownBy(() -> controller.signUp(person))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Username and password mustn't be empty");
    }

    @Test
    public void whenSignUpWithShortPasswordThenThrowIllegalArgumentException() {
        var person = new Person();
        person.setLogin("admin");
        person.setPassword("passw");

        assertThatThrownBy(() -> controller.signUp(person))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid password. Password length must be more than 5 characters.");
    }
}