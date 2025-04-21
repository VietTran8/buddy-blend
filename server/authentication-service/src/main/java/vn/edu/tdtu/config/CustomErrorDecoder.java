package vn.edu.tdtu.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.exception.UnauthorizedException;

import java.io.IOException;
import java.io.InputStream;

@Component
public class CustomErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String s, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());
        ResDTO<?> res = new ResDTO<>();

        try (InputStream bodyStream = response.body().asInputStream()) {
            res = objectMapper.readValue(bodyStream, ResDTO.class);
        } catch (IOException e) {
            return new Exception("Failed to parse response body", e);
        }

        String message = res.getMessage();

        return switch (status) {
            case NOT_FOUND -> new UnauthorizedException(message);
            case BAD_REQUEST -> new BadRequestException(message);
            case INTERNAL_SERVER_ERROR -> new Exception(message);
            default -> defaultErrorDecoder.decode(s, response);
        };
    }
}
