package vn.edu.tdtu.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.tdtu.dto.gemini.request.GeminiRequest;
import vn.edu.tdtu.dto.gemini.response.Root;

@FeignClient(
        name = "geminiClient",
        url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"
)
public interface GeminiClient {
    @PostMapping
    ResponseEntity<Root> getResponse(@RequestParam("key") String key, @RequestBody GeminiRequest request, @RequestHeader("Cache-Control") String cacheControl);
}
