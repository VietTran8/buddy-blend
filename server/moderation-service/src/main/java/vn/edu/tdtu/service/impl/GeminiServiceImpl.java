package vn.edu.tdtu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.client.GeminiClient;
import vn.edu.tdtu.dto.gemini.request.GeminiRequest;
import vn.edu.tdtu.dto.gemini.request.GeminiText;
import vn.edu.tdtu.dto.gemini.request.Part;
import vn.edu.tdtu.dto.gemini.response.Root;
import vn.edu.tdtu.service.interfaces.GeminiService;
import vn.edu.tdtu.utils.PromptUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiServiceImpl implements GeminiService {
    private final GeminiClient geminiClient;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Override
    public String call(String prompt) {
        GeminiText text = new GeminiText(prompt);
        Part part = new Part(List.of(text));
        GeminiRequest request = new GeminiRequest(List.of(part));

        ResponseEntity<Root> response = geminiClient.getResponse(geminiApiKey, request, "no-cache");

        if (HttpStatusCode.valueOf(200).equals(response.getStatusCode())) {
            return response.getBody().getContent();
        }

        return null;
    }

    @Override
    public String call(Resource resource) {
        String promptString = getStringFromResource(resource);
        return call(promptString);
    }

    @Override
    public String call(Resource templateResource, Map<String, String> context) {
        return call(getStringFromResource(templateResource), context);
    }

    @Override
    public String call(String templatePrompt, Map<String, String> context) {
        String prompt = PromptUtil.create(templatePrompt, context);
        return call(prompt);
    }

    private static String getStringFromResource(Resource resource) {
        try {
            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }
}
