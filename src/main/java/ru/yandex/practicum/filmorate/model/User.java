package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
@Builder
public class User {
    @EqualsAndHashCode.Exclude
    private Long id;

    private String name;

    @Email(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "email is not valid")
    private String email;

    @NotBlank(message = "login can't be blank")
    private String login;

    @PastOrPresent(message = "birthday can't be after today")
    private LocalDate birthday;
}
