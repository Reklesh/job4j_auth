package ru.job4j.auth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.job4j.auth.Operation;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "person")
public class Person {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull(message = "Id must be non null", groups = {Operation.OnUpdate.class})
    private Integer id;

    @NotBlank(message = "Username must be empty", groups = {Operation.OnCreate.class, Operation.OnUpdate.class})
    private String login;

    @NotBlank(message = "Password must be empty", groups = {Operation.OnCreate.class, Operation.OnUpdate.class})
    @Size(min = 6, message = "Password length must be at least 6 characters",
            groups = {Operation.OnCreate.class, Operation.OnUpdate.class})
    private String password;
}
