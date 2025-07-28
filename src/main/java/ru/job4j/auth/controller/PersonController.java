package ru.job4j.auth.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.auth.Operation;
import ru.job4j.auth.dto.PersonDto;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.repository.person.PersonRepository;

import java.util.List;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping("/person")
public class PersonController {

    private final PersonRepository persons;
    private final BCryptPasswordEncoder encoder;

    @GetMapping("/")
    public List<Person> findAll() {
        return (List<Person>) this.persons.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        var person = this.persons.findById(id);
        return new ResponseEntity<>(
                person.orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User with ID %d not found".formatted(id)
                )),
                HttpStatus.OK
        );
    }

    @PostMapping("/")
    public ResponseEntity<Person> create(@Validated(Operation.OnCreate.class) @RequestBody Person person) {
        return new ResponseEntity<>(
                this.persons.save(person),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@Validated(Operation.OnUpdate.class) @RequestBody Person person) {
        this.persons.save(person);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Person> patch(@PathVariable int id, @RequestBody PersonDto dto) {
        var person = this.persons.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (dto.getLogin() != null) {
            person.setLogin(dto.getLogin());
        }
        if (dto.getPassword() != null) {
            person.setPassword(encoder.encode(dto.getPassword()));
        }
        return ResponseEntity.ok(this.persons.save(person));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Person person = new Person();
        person.setId(id);
        this.persons.delete(person);
        return ResponseEntity.ok().build();
    }
}