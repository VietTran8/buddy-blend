package vn.edu.tdtu.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;

import java.io.IOException;

public class BeanOutputParser<T> {
    private static String cleanJson(String input) {
        if (input.startsWith("```json") && input.endsWith("```")) {
            return input.substring(7, input.length() - 3).trim();
        }
        return input;
    }

    public T parse(String responseJson, Class<T> clazz) {
        try {
            responseJson = cleanJson(responseJson);
            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(responseJson, clazz);
        } catch (IOException e) {
            return null;
        }
    }

    @SneakyThrows
    public String getFormat(Class<T> clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            T dummyInstance = clazz.getDeclaredConstructor().newInstance();

            String jsonExample = mapper.writeValueAsString(dummyInstance);

            return "No need for any additional response, no need to use markdown format or code fencing (```)... You just need to respond with a single JSON object in the following format:\n" +
                    "\"" + jsonExample + "\"";
        } catch (Exception e) {
            throw new RuntimeException("Unable to create prompt for class: " + clazz.getSimpleName(), e);
        }
    }
}