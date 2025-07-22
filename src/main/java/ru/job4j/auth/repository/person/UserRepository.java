package ru.job4j.auth.repository.person;

import ru.job4j.auth.model.Person;

import java.util.List;

public interface UserRepository {

    void save(Person person);

    Person findByUsername(String username);

    List<Person> findAll();
}
