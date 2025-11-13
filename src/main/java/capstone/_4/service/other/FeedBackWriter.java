package capstone._4.service.other;

import capstone._4.dto.diary.EmotionResponse;
import capstone._4.dto.diary.output.DiaryCreateResponse;
import capstone._4.dto.diary.output.EmotionResultDto;
import capstone._4.dto.diary.output.FeedBackDto;
import capstone._4.exception.FastApiException;
import capstone._4.service.DiaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class FeedBackWriter {
    private final DiaryService diaryService;

    // WebClient 빈 주입
    private final WebClient openAiWebClient;
    private final WebClient fastApiWebClient;

    public FeedBackWriter(DiaryService diaryService,
                          @Qualifier("OpenAiWebClient") WebClient openAiWebClient,
                          @Qualifier("FastApiWebClient") WebClient fastApiWebClient) {
        this.diaryService = diaryService;
        this.openAiWebClient = openAiWebClient;
        this.fastApiWebClient = fastApiWebClient;
    }

    /**
     * GPT 피드백과 FastAPI 감정 분석을 병렬로 처리
     * @param diary 생성된 다이어리 정보
     * @return FeedBackDto (피드백 DTO)
     */
    public FeedBackDto createFeedBack(DiaryCreateResponse diary) {
        // GPT 피드백 생성
        CompletableFuture<String> feedBackResult = CompletableFuture
                .supplyAsync(() -> generateAndSaveFeedback(diary.getContent()));

        // 감정 분석 요청
        CompletableFuture<List<EmotionResultDto>> emotionResult = createEmotionResult(diary.getContent());

        // 두 작업을 병렬로 처리한 후 결과 결합
        return feedBackResult.thenCombine(emotionResult, (feedBack, emotions) -> {
            diaryService.saveFeedBackInfo(feedBack, emotions, diary.getId());
            return FeedBackDto.builder()
                    .emotions(emotions)
                    .feedback(feedBack)
                    .diary(diary.getContent())
                    .build();
        }).join();
    }

    /**
     * GPT를 통해 피드백을 생성하는 메서드
     * @param content 다이어리 내용
     * @return 생성된 피드백
     */
    private String generateAndSaveFeedback(String content) {
        log.info("GPT 피드백 요청: {}", content);

        // GPT API 호출 (예시)
        String feedback = null;
        try {
            feedback = openAiWebClient.post()
                    .uri("/chat/completions")  // 엔드포인트
                    .bodyValue(Map.of(
                            "model", "gpt-3.5-turbo",  // 사용하는 모델 이름
                            "messages", List.of(Map.of(  // 메시지 내용
                                    "role", "user",  // 메시지의 역할 (사용자의 메시지)
                                    "content", content  // 다이어리 내용
                            )),
                            "max_tokens", 150  // 최대 토큰 수 설정
                    ))
                    .retrieve()
                    .bodyToMono(Map.class) // 응답을 Map으로 받음
                    .map(response -> {
                        // 응답에서 'choices'가 있는지 확인
                        if (response != null && response.containsKey("choices")) {
                            List<Map> choices = (List<Map>) response.get("choices");
                            if (choices.isEmpty()) {
                                log.error("GPT 응답에서 'choices' 배열이 비어 있습니다.");
                                return "피드백 생성 실패";  // 선택지가 없으면 실패 메시지 반환
                            }
                            // 선택지에서 message.content를 추출
                            Map<String, Object> choice = choices.get(0);  // 첫 번째 선택지
                            Map<String, Object> message = (Map<String, Object>) choice.get("message");
                            return (String) message.get("content");  // message 안의 content 반환
                        } else {
                            log.error("GPT 응답에서 'choices' 필드가 누락되었습니다.");
                            return "피드백 생성 실패";  // 응답에 'choices'가 없으면 실패 메시지 반환
                        }
                    })
                    .block(); // 동기 호출로 응답 기다리기
        } catch (Exception e) {
            log.error("GPT 피드백 요청 중 예외 발생: {}", e.getMessage());
            return "피드백 생성 실패";  // 예외 발생 시 실패 메시지 반환
        }

        return feedback != null ? feedback : "피드백 생성 실패"; // 응답이 없으면 기본값 반환
    }


    /**
     * 감정 분석을 위한 메서드
     * @param feedback 다이어리 내용
     * @return 감정 분석 결과
     */
    private CompletableFuture<List<EmotionResultDto>> createEmotionResult(String feedback) {
        log.info("감정 분석 요청: {}", feedback);
        return fastApiWebClient.post().uri("/predict_emotion")
                .bodyValue(Map.of("text", feedback))
                .retrieve()
                .bodyToMono(EmotionResponse.class)
                .map(response -> {
                    log.info("감정 분석 응답: {}", response.getEmotions()); // 감정 분석 응답에서 중요한 정보만 출력
                    return response.getEmotions();
                })
                .toFuture(); // 비동기적으로 감정 분석 결과 반환
    }
}
