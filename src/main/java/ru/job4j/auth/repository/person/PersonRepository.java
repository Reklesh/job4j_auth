package ru.job4j.auth.repository.person;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.auth.model.Person;

public interface PersonRepository extends CrudRepository<Person, Integer> {
}
