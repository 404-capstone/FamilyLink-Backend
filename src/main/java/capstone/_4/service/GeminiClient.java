package capstone._4.service;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class GeminiClient {

    private final String apiKey;
    private final String model;
    private final RestTemplate restTemplate;

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key={apiKey}";

    public GeminiClient(String apiKey, String model, RestTemplate restTemplate) {
        this.apiKey = apiKey;
        this.model = model;
        this.restTemplate = restTemplate;
    }

    public String generateContent(String prompt) {
        // 요청 JSON 구성
        Map<String, Object> request = new HashMap<>();
        request.put("contents", new Object[]{
                Map.of("parts", new Object[]{Map.of("text", prompt)})
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                GEMINI_URL,
                HttpMethod.POST,
                entity,
                Map.class,
                model,
                apiKey
        );

        Map body = response.getBody();
        if (body != null && body.containsKey("candidates")) {
            Map candidate = (Map) ((java.util.List) body.get("candidates")).get(0);
            Map content = (Map) candidate.get("content");
            java.util.List parts = (java.util.List) content.get("parts");
            Map part = (Map) parts.get(0);
            return (String) part.get("text");
        }

        return "응답을 가져오지 못했습니다.";
    }
}