package org.Akorad.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(
        description = "Ответ об ошибке",
        title = "ErrorResponse",
        example = "{\n" +
                "  \"timestamp\": \"2023-10-01T12:00:00\",\n" +
                "  \"status\": 404,\n" +
                "  \"error\": \"Not Found\",\n" +
                "  \"message\": \"Resource not found\",\n" +
                "  \"path\": \"/api/resource\"\n" +
                "}")
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
