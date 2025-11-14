package capstone._4.service.other;

import capstone._4.dto.diary.EmotionResponse;
import capstone._4.dto.diary.output.DiaryCreateResponse;
import capstone._4.dto.diary.output.EmotionResultDto;
import capstone._4.dto.diary.output.FeedBackDto;
import capstone._4.dto.gpt.MessageRequestDto;
import capstone._4.dto.gpt.OpenAiRequestDto;
import capstone._4.dto.gpt.OpenAiResponseDto;
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

        try {
            OpenAiRequestDto req = new OpenAiRequestDto();
            req.setModel("gpt-5-mini");

            req.setMessages(List.of(
                    new MessageRequestDto("system",
                            """
                            너는 사용자의 일기에 대해 따뜻하고 공감하는 상담가 역할을 한다.
                            - 비난 금지
                            - 판단 금지
                            - 감정 공감 먼저
                            - 4~6줄로 작성
                            - 마지막 줄은 '응원 문장'으로 마무리
                            - 어려운 단어 사용 금지, 부드러운 톤 유지
                            """),
                    new MessageRequestDto("user", content)
            ));

            OpenAiResponseDto response = openAiWebClient.post()
                    .uri("/chat/completions")
                    .bodyValue(req)
                    .retrieve()
                    .bodyToMono(OpenAiResponseDto.class)
                    .block();

            String feedback = response.getChoices()
                    .get(0)
                    .getMessage()
                    .getContent()
                    .replace("\\n", "\n")
                    .replace("  \n", "\n");

            return feedback;

        } catch (Exception e) {
            log.error("GPT 피드백 요청 중 예외 발생: {}", e.getMessage());
            return "피드백 생성 실패";
        }
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
