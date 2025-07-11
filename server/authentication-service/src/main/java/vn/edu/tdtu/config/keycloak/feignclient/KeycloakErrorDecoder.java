package vn.edu.tdtu.config.keycloak.feignclient;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import vn.tdtu.common.exception.BadRequestException;
import vn.tdtu.common.exception.UnauthorizedException;
import vn.tdtu.common.utils.MessageCode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeycloakErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());

        byte[] body = new byte[0];

        try {
            if (response.body() != null) {
                body = Util.toByteArray(response.body().asInputStream());
            }
        } catch (IOException var6) {
        }

        String message = (new FeignExceptionMessageBuilder())
                .withResponse(response).withMethodKey(methodKey)
                .withBody(body)
                .build();

        return switch (status) {
            case NOT_FOUND, BAD_REQUEST -> new BadRequestException(message);
            case UNAUTHORIZED -> new UnauthorizedException(MessageCode.Authentication.AUTH_INVALID_CREDENTIALS);
            case INTERNAL_SERVER_ERROR -> new Exception(message);
            default -> defaultErrorDecoder.decode(methodKey, response);
        };
    }

    @NoArgsConstructor
    private static class FeignExceptionMessageBuilder {
        private Response response;
        private byte[] body;
        private String methodKey;

        private static Charset getResponseCharset(Map<String, Collection<String>> headers) {
            Collection<String> strings = headers.get("content-type");
            if (strings != null && !strings.isEmpty()) {
                Pattern pattern = Pattern.compile(".*charset=([^\\s|^;]+).*");
                Matcher matcher = pattern.matcher(strings.iterator().next());
                if (!matcher.lookingAt()) {
                    return null;
                } else {
                    String group = matcher.group(1);
                    return !Charset.isSupported(group) ? null : Charset.forName(group);
                }
            } else {
                return null;
            }
        }

        public KeycloakErrorDecoder.FeignExceptionMessageBuilder withResponse(Response response) {
            this.response = response;
            return this;
        }

        public KeycloakErrorDecoder.FeignExceptionMessageBuilder withBody(byte[] body) {
            this.body = body;
            return this;
        }

        public KeycloakErrorDecoder.FeignExceptionMessageBuilder withMethodKey(String methodKey) {
            this.methodKey = methodKey;
            return this;
        }

        public String build() {
            StringBuilder result = new StringBuilder();

            if (this.response.reason() != null) {
                result.append(String.format("[%d %s]", this.response.status(), this.response.reason()));
            } else {
                result.append(String.format("[%d]", this.response.status()));
            }

            result.append(String.format(" during [%s] to [%s] [%s]", this.response.request().httpMethod(), this.response.request().url(), this.methodKey));
            result.append(String.format(": [%s]", this.getBodyAsString(this.body, this.response.headers())));
            return result.toString();
        }

        private String getBodyAsString(byte[] body, Map<String, Collection<String>> headers) {
            Charset charset = getResponseCharset(headers);
            if (charset == null) {
                charset = Util.UTF_8;
            }

            return this.getResponseBody(body, charset);
        }

        private String getResponseBody(byte[] body, Charset charset) {
            Integer maxBodyBytesLength = 400;
            return body.length < maxBodyBytesLength ? new String(body, charset) : this.getResponseBodyPreview(body, charset);
        }

        private String getResponseBodyPreview(byte[] body, Charset charset) {
            try {
                Reader reader = new InputStreamReader(new ByteArrayInputStream(body), charset);
                Integer maxBodyCharsLength = 200;
                CharBuffer result = CharBuffer.allocate(maxBodyCharsLength);
                reader.read(result);
                reader.close();
                result.flip();
                return result + "... (" + body.length + " bytes)";
            } catch (IOException var5) {
                return var5 + ", failed to parse response";
            }
        }
    }
}
