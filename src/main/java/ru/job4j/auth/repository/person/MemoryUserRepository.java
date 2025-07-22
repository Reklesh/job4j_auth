package ru.job4j.auth.repository.person;

import org.springframework.stereotype.Repository;
import ru.job4j.auth.model.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MemoryUserRepository implements UserRepository {

    private final Map<String, Person> users = new ConcurrentHashMap<>();

    @Override
    public void save(Person person) {
        users.put(person.getLogin(), person);
    }

    @Override
    public Person findByUsername(String username) {
        return users.get(username);
    }

    @Override
    public List<Person> findAll() {
        return new ArrayList<>(users.values());
    }
}
