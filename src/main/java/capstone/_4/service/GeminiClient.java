package capstone._4.service;

import capstone._4.domain.Diary;
import capstone._4.repository.DiaryRepository;
import jakarta.transaction.Transactional;
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
    private final DiaryRepository diaryRepository;

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key={apiKey}";

    public GeminiClient(@Value("${gemini.api-key}") String apiKey,
                        @Value("${gemini.model}") String model,
                        RestTemplate restTemplate,
                        DiaryRepository diaryRepository) {
        this.apiKey = apiKey;
        this.model = model;
        this.restTemplate = restTemplate;
        this.diaryRepository = diaryRepository;
    }
    @Transactional
    public String generateAndSaveFeedback(Long diaryId) {
        // 1. DB에서 일지 조회
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("일지를 찾을 수 없습니다."));

        // 2. 프롬프트 작성
        String prompt = """
            너는 따뜻하게 공감해주면서도 객관적인 개선 피드백을 주는 '일지 코치'야.

            [지침]
            1. 먼저 사용자의 감정을 공감하며 짧게 응원해줘. (따뜻한 톤)
            2. 이어서 개선할 점이나 긍정적인 습관 제안을 간단히 해줘. (객관적 톤)
            3. 전체 답변은 4~5줄로 제한해.

            [일지]
            %s
            """.formatted(diary.getContent());



        // 3. Gemini API 호출
        String feedback = generateContent(prompt);

        // 4. DB에 feedbook 저장
        diary.setFeedbook(feedback);
        diaryRepository.save(diary);

        return feedback;
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