package capstone._4.service.other;

import capstone._4.dto.diary.EmotionResponse;
import capstone._4.dto.diary.output.DiaryCreateResponse;
import capstone._4.dto.diary.output.EmotionResultDto;
import capstone._4.dto.diary.output.FeedBackDto;
import capstone._4.exception.FastApiException;
import capstone._4.repository.DiaryRepository;
import capstone._4.repository.EmotionRepository;
import capstone._4.service.DiaryService;
import capstone._4.service.GeminiClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class FeedBackWriter {
    private final DiaryService diaryService;
    private final WebClient webClient;

    public FeedBackWriter(DiaryService diaryService,
                          @Qualifier("FastApiWebClient") WebClient webClient) {
        this.diaryService = diaryService;
        this.webClient = webClient;
    }

    /**
     * gemini 피드백과 fastapi 병렬처리수행.
     * @param diary
     * @return
     */
    public FeedBackDto createFeedBack(DiaryCreateResponse diary){
        //피드백 불러오기.
        CompletableFuture<String> feedBackResult=CompletableFuture
                .supplyAsync(()-> diaryService.generateAndSaveFeedback(diary.getId()));

        //감정 불러오기
        CompletableFuture<List<EmotionResultDto>> emotionResult= createEmotionResult(diary.getContent());

        //두작업 다 완료시 결과 받아서 값을 반환한다.두개만 사용해서 이렇게 사용.2개 이상은allof
        return feedBackResult.thenCombine(emotionResult,(feedBack,emotions)->{
            diaryService.saveFeedBackInfo(feedBack,emotions,diary.getId());
            return FeedBackDto.builder()
                    .emotions(emotions)
                    .feedback(feedBack)
                    .diary(diary.getContent()).build();
        }).join();
    }

    private CompletableFuture<List<EmotionResultDto>> createEmotionResult(String feedback){
        log.info("감정 분석.");
        return webClient.post().uri("/emotion")
                .bodyValue(Map.of("feedback",feedback))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, re->re.bodyToMono(String.class)
                        .flatMap(error-> Mono.error(new FastApiException("감정분석중 오류가 발생했습니다."+error))))
                .onStatus(HttpStatusCode::is5xxServerError,re->re.bodyToMono(String.class)
                        .flatMap(error-> Mono.error(new FastApiException("감정분석중 오류가 발생했습니다."+error))))
                .bodyToMono(EmotionResponse.class)
                .map(EmotionResponse::getEmotions)
                .toFuture(); //여기는 비동기 위해서 사용.

    }
}
