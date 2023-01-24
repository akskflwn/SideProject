package com.test.project.dto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
public class JwtErrorResponseDto {

    private HttpStatus status;

    private String error;

    private String description;

    public static JwtErrorResponseDto of(HttpStatus status, String error, String description) {
        return JwtErrorResponseDto.builder()
            .status(status)
            .error(error)
            .description(description)
            .build();
    }

    public String convertToJson() {
        return "{\"status\":\""+this.status+"\",\n\"error\":\""+this.error+"\",\n\"description\":\""+this.description+"\"}";
    }
}