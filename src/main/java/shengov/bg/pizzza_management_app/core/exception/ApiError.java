package shengov.bg.pizzza_management_app.core.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;

@Schema(description = "Standard error response returned on failed requests")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
    @Schema(description = "Timestamp when the error occurred", example = "2026-02-14T10:15:30Z")
        Instant timestamp,
    @Schema(description = "HTTP status code", example = "404") Integer status,
    @Schema(description = "HTTP status reason phrase", example = "Not Found") String error,
    @Schema(description = "Human-readable error message", example = "Ingredient not found")
        String message,
    @Schema(
            description = "Request path that triggered the error",
            example = "/api/ingredients/3fa85f64-5717-4562-b3fc-2c963f66afa6")
        String path,
    @Schema(
            description = "Field-level validation errors, present only on 400 responses",
            example =
                "{\"name\": [\"Name is required\", \"Name must be between 2 and 50 characters\"]}")
        Map<String, String[]> errors) {

  public static ApiError from(
      String message, HttpStatus status, HttpServletRequest request, Map<String, String[]> errors) {
    return new ApiError(
        Instant.now(),
        status.value(),
        status.getReasonPhrase(),
        message,
        request.getRequestURI(),
        errors);
  }

  public static ApiError from(String message, HttpStatus status, HttpServletRequest request) {
    return new ApiError(
        Instant.now(),
        status.value(),
        status.getReasonPhrase(),
        message,
        request.getRequestURI(),
        null);
  }
}
