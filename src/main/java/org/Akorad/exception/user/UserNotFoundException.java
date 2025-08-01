package org.Akorad.exception.user;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super("Пользователь с именем '" + username + "' не найден");
    }
}
