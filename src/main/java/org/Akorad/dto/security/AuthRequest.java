package org.Akorad.dto.security;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {

    @Schema(description = "Логин пользователя", example = "Ivan123")
    @NotBlank(message = "Имя пользователя не должно быть пустым")
    @Size(min = 3, max = 20, message = "Имя должно содержать от 3 до 20 символов")
    private String username;

    @Schema(description = "Пароль пользователя", example = "password123")
    @NotBlank(message = "Пароль не должен быть пустым")
    @Size(min = 6, max = 64, message = "Пароль должен содержать от 6 до 64 символов.")
    private String password;
}
