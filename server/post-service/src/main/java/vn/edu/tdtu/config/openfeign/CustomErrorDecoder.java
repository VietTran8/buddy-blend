package vn.edu.tdtu.config.openfeign;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.exception.UnauthorizedException;

import java.io.IOException;
import java.io.InputStream;

public class CustomErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();
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

        String errorMessage = res.getMessage();

        switch (status) {
            case BAD_REQUEST -> {
                return new BadRequestException(errorMessage);
            }
            case UNAUTHORIZED -> {
                return new UnauthorizedException(errorMessage);
            }
            case INTERNAL_SERVER_ERROR -> {
                return new Exception(errorMessage);
            }
            default -> {
                return defaultDecoder.decode(s, response);
            }
        }
    }
}