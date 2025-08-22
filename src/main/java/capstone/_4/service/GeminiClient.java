package capstone._4.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
@Service
public class GeminiClient {
    private final String apiKey;
    private final String model;
    private final RestTemplate restTemplate;

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key={apiKey}";

    public GeminiClient(@Value("${gemini.api-key}") String apiKey,
                        @Value("${gemini.model}") String model,
                        RestTemplate restTemplate) {
        this.apiKey = apiKey;
        this.model = model;
        this.restTemplate = restTemplate;
    }

    public String generateContent(String prompt) {
        Map<String, Object> request = new HashMap<>();
        request.put("contents", new Object[]{
                Map.of("parts", new Object[]{Map.of("text", prompt)})
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    GEMINI_URL,
                    HttpMethod.POST,
                    entity,
                    Map.class,
                    model,
                    apiKey
            );

            Map body = response.getBody();
            if (body == null) return "응답 body가 비어 있습니다.";

            if (body.containsKey("error")) {
                Map error = (Map) body.get("error");
                return "Gemini API 오류: " + error.get("message");
            }

            if (body.containsKey("candidates")) {
                var candidates = (java.util.List<Map>) body.get("candidates");
                if (!candidates.isEmpty()) {
                    var candidate = candidates.get(0);
                    Map content = (Map) candidate.get("content");
                    java.util.List parts = (java.util.List) content.get("parts");
                    if (!parts.isEmpty()) {
                        Map part = (Map) parts.get(0);
                        return (String) part.get("text");
                    }
                }
            }
            return "응답을 가져오지 못했습니다.";
        } catch (Exception e) {
            return "API 호출 실패: " + e.getMessage();
        }
    }

}