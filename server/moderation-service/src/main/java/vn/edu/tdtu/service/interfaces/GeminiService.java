package vn.edu.tdtu.service.interfaces;

import org.springframework.core.io.Resource;

import java.util.Map;

public interface GeminiService {
    String call(String prompt);

    String call(Resource resource);

    String call(Resource templateResource, Map<String, String> context);

    String call(String templatePrompt, Map<String, String> context);
}