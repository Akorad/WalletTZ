package org.Akorad.dto.security;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на вход в систему")
public class LoginRequest extends AuthRequest{
}
