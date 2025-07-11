package vn.tdtu.common.config.openfeign;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import vn.tdtu.common.exception.BadRequestException;
import vn.tdtu.common.exception.UnauthorizedException;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.io.IOException;
import java.io.InputStream;

public class CustomErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String s, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());

        ResponseVM<?> responseVm = new ResponseVM<>();

        try (InputStream bodyStream = response.body().asInputStream()) {
            responseVm = objectMapper.readValue(bodyStream, ResponseVM.class);
        } catch (IOException e) {
            return new RuntimeException(e);
        }

        String errorMessage = responseVm.getMessage();

        switch (status) {
            case BAD_REQUEST -> {
                return new BadRequestException(errorMessage);
            }
            case UNAUTHORIZED -> {
                return new UnauthorizedException(errorMessage);
            }
            case INTERNAL_SERVER_ERROR -> {
                return new RuntimeException(errorMessage);
            }
            default -> {
                return defaultDecoder.decode(s, response);
            }
        }
    }
}
