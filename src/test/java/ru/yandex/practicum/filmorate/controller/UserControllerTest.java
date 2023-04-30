package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserControllerTest {
    private User user;
    private UserController controller;

    @BeforeEach
    @Autowired
    public void createCorrectUser(UserController controller) {
        this.controller = controller;

        user = User.builder()
                .name("Pavel")
                .login("XpeHo3eM")
                .email("XpeHo3eM@gmail.com")
                .birthday(LocalDate.of(1990, Month.MARCH, 13))
                .build();
    }

    @Test
    public void shouldCreateUserWithCorrectParam() {
        controller.addUser(user);

        List<User> users = controller.getAllUsers();

        assertEquals(user, users.get(0), "Не найден корректный пользователь");
    }

    @Test
    public void shouldSetNameAsLoginByUserWithoutName() {
        user.setName(null);

        assertEquals("XpeHo3eM", controller.addUser(user).getName(), "Пустое имя не получило значения логина");
    }

    @Test
    public void shouldNotCreateUserWithIncorrectEmail() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        user.setEmail("");
        assertFalse(validator.validate(user).isEmpty(), "Пропустило пустой email");

        user.setEmail("121");
        assertFalse(validator.validate(user).isEmpty(), "Пропустило email без @");

        user.setEmail("XpeHo3eM@");
        assertFalse(validator.validate(user).isEmpty(), "Пропустило email без доменого имени");

        user.setEmail("@gmail.com");
        assertFalse(validator.validate(user).isEmpty(), "Пропустило email без имени пользователя");
    }

    @Test
    public void shouldNotCreateUserWithIncorrectLogin() {
        user.setLogin("");

        assertThrows(ValidationException.class,
                () -> controller.addUser(user),
                "Пропустило пустой логин");

        user.setLogin("Practicum Yandex");
        assertThrows(ValidationException.class,
                () -> controller.addUser(user),
                "Пропустило логин с пробелами");
    }

    @Test
    public void shouldNotCreateUserWithIncorrectBirthday() {
        user.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class,
                () -> controller.addUser(user),
                "Пропустило дату рождения из будущего");
    }

    @Test
    public void shouldNotUpdateUserWithoutId() {
        controller.addUser(user);

        assertThrows(ValidationException.class,
                () -> controller.updateUser(User.builder()
                        .name("Serj")
                        .login("XpeHo3eM")
                        .email("XpeHo3eM@gmail.com")
                        .birthday(LocalDate.of(1990, Month.MARCH, 13))
                        .build()),
                "Обновился пользователь без ID");
    }
}
