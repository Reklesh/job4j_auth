package ru.job4j.auth.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.job4j.auth.Operation;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.repository.person.MemoryUserRepository;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@Validated
@RequestMapping("/users")
public class UserController {

    private final MemoryUserRepository users;
    private final BCryptPasswordEncoder encoder;

    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@Validated(Operation.OnCreate.class) @RequestBody Person person) {
        person.setPassword(encoder.encode(person.getPassword()));
        users.save(person);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<Person>> findAll() {
        return ResponseEntity.ok(users.findAll());
    }
}