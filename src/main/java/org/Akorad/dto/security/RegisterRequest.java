package org.Akorad.dto.security;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Запрос на регистрацию нового пользователя")
public class RegisterRequest extends AuthRequest{

    @Schema(description = "Имя пользователя", example = "Иван")
    @NotBlank (message = "Имя не должно быть пустым")
    private String firstName;

    @Schema(description = "Фамилия пользователя", example = "Иванов")
    @NotBlank (message = "Фамилия не должна быть пустой")
    private String lastName;
}
