package capstone._4.service.other;

import capstone._4.domain.Diary;
import capstone._4.domain.User;
import capstone._4.dto.gpt.*;
import capstone._4.dto.gpt.OpenAiResponseDto;
import capstone._4.dto.schedule.input.GroupScheduleInfoDto;
import capstone._4.exception.GptErrorException;
import capstone._4.service.QuestionService;
import capstone._4.repository.DiaryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OpenAiService {

    private final WebClient webClient;
    private final QuestionService questionService;
    private final DiaryRepository diaryRepository;

    public OpenAiService(@Qualifier("OpenAiWebClient") WebClient webClient,
                         QuestionService questionService,
                         DiaryRepository diaryRepository) {
        this.webClient = webClient;
        this.questionService = questionService;
        this.diaryRepository = diaryRepository;
    }

    /**
     * 가족활동 추천을 gpt에게 전송하여 생성하는 서비스 부분.
     *
     * @param groupScheduleInfoDto 추천 입력 정보.
     * @param users                참여 유저정보.
     * @return
     */

    public OpenAiRecommendComment createRecommend(GroupScheduleInfoDto groupScheduleInfoDto, List<User> users) {
        try {
            log.info("가족 활동 추천 만들기 시작.");
            OpenAiResponseDto openAiResponseDto = getOpenAiRecommend(groupScheduleInfoDto, users);
            String content = openAiResponseDto.getChoices().get(0).getMessage().getContent(); //json을 역직렬화,즉 오브젝트화 하기위해 임시로 담은.
            log.info("response:\n{}", content);

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(content, OpenAiRecommendComment.class); //여기서 오브젝트화.

        } catch (JsonMappingException e) {
            throw new RuntimeException("json 매핑중 오류 발생: " + e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json 변환중 오류 발생: " + e);
        } catch (Exception e) {
            throw new RuntimeException("오류가 발생했습니다." + e);
        }
    }

    /**
     * 매주 월요일에 질문을 생성하는 메서드
     * question안에 contetn에 문제 여러개 존재
     */
    //@Scheduled(cron = "0 58 23 ? * SUN",zone="Asia/Seoul")
    public OpenAiQuestionContent createQuestion() {
        log.info("질문지 만들기 시작.");
        OpenAiRequestDto openAiRequestDto = new OpenAiRequestDto();
        openAiRequestDto.setModel("gpt-5-mini");  //모델 설정
        List<MessageRequestDto> messages = generateQuestionMessage();
        ResponseFormatDto formatDto=generateQuestionSchema();
        openAiRequestDto.setMessages(messages);
        openAiRequestDto.setResponse_format(formatDto);

        OpenAiResponseDto response=webClient.post()
                .uri("/chat/completions")
                .bodyValue(openAiRequestDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(error -> Mono.error(new GptErrorException("gpt 오류 발생" + error))))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(error -> Mono.error(new GptErrorException("gpt오류 발생" + error))))
                .bodyToMono(OpenAiResponseDto.class)
                .block();
        try {
            String question = response.getChoices().get(0).getMessage().getContent();
            ObjectMapper objectMapper = new ObjectMapper();
            OpenAiQuestionContent content = objectMapper.readValue(question, OpenAiQuestionContent.class);  //변환하기 json형태로.
            questionService.originalQuestionSave(content);
            return content;
            //저장하기 로직
        } catch (JsonMappingException e) {
            throw new RuntimeException("json 매핑중 오류 발생: " + e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json 변환중 오류 발생: " + e);
        } catch (Exception e) {
            throw new RuntimeException("오류가 발생했습니다." + e);
        }

    }

    private static List<MessageRequestDto> generateRecommendMessages(GroupScheduleInfoDto groupScheduleInfoDto, List<User> users) {
        List<MessageRequestDto> messages = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        StringBuilder sb = new StringBuilder();
        messages.add(MessageRequestDto.builder()
                .role("system")
                .content("너는 가족 커뮤니케이션 증진을 위한 추천 도우미야.\n" +
                        "                \n" +
                        "                # 1) 내가 제공할 정보\n" +
                        "                • 활동 위치: 시·구 단위 (ex: 서울시 강남구)  \n" +
                        "                • 시간대: 시작/종료 시각 (ex: 2025-07-25 15:00 / 18:00)  \n" +
                        "                • 참여자: 인원수 및 나이대 (ex: 4명, [70대 남자, 50대 여자, 20대 남자, 20대 남자])  \n" +
                        "                • 실내/실외: 선호 여부 (ex: 실내)  \n" +
                        "                • 활동 분류: 힐링/휴식, 스포츠/레저, 식사, 먹거리/음료, 창의/체험, 여행/탐방, 문화/예술\n" +
                        "\n" +
                        "                # 2) 응답 JSON 스키마 (strict 모드)\n" +
                        "                {\n" +
                        "                  \"recommendations\": [\n" +
                        "                    {\n" +
                        "                      \"category\": \"string\",       // 활동 분류\n, 활동 분류당 하나씩만." +
                        "                      \"items\": [\n" +
                        "                        {\n" +
                        "                          \"activity\": \"string\",   // 활동 이름\n" +
                        "                          \"location\": \"string\",   // 활동 장소\n" +
                        "                          \"description\": \"string\" // 활동 설명\n" +
                        "                        }\n" +
                        "                      ]\n" +
                        "                    }\n" +
                        "                  ]\n" +
                        "                }\n" +
                        "\n" +
                        "                • 배열 하나당 items 5개를 반드시 채워줘.\n" +
                        "                •  recommendations 배열안에 category당 하나의 활동분류,오브젝트를 생성해줘." +
                        "                    예) [\"힐링/휴식\"],[\"문화/예술\"] 이런식으로 각각   \n" +
                        "                •  description에는 시간 표현(예: “시간대”, “~시”, “~분”, “am/pm”, “12:30”, “15:00-18:00”)을 넣지 않는다." +
                        "                   나쁜예 - \"description\": \"바람 맞으며 달리는 활동. 시간대 12:00-15:30 추천.\n" +
                        "                   좋은예 - \"description\": \"바람을 맞으며 달리는 활동. 한산한 때 방문하면 더 쾌적함.\n" +
                        "                • 시간 관련 문구가 떠오르면 “한산한 때에 방문 권장” 같은 **비시간형 표현**으로 바꿀 것. \n" +
                        "                • 추가 필드는 허용되지 않습니다.")
                .build());
        messages.add(MessageRequestDto.builder()
                .role("user")
                .content("활동 위치는 " + groupScheduleInfoDto.getArea() + ",인원정보는 " + groupScheduleInfoDto.getMemberIds().size() + "명, 나이대는 각[" +
                        users.stream().map(user -> user.getAge() + "대 " + user.getGender()).collect(Collectors.joining(","))
                        + "], 활동시간대는" + groupScheduleInfoDto.getStartTime().format(dtf) + "/" + groupScheduleInfoDto.getEndTime().format(dtf)
                        + "이고 " + groupScheduleInfoDto.getInoutdoor() + "에서 하기를 원해.")
                .build());
        log.info("메시지 정보:\n{}", messages.get(1).getContent());
        sb.append("활동분류는 ");

        sb.append(groupScheduleInfoDto.getActivityPersonalityList().stream().map(activityPersonality -> activityPersonality.getType())
                .collect(Collectors.joining(",")));
//        for(ActivityPersonality activity:groupScheduleInfoDto.getActivityPersonalityList()){
//            sb.append(activity.getType()+",");
//        }
        sb.append("로 생성해줘.");
        log.info("생성 프롬포트:{}", sb);
        messages.add(MessageRequestDto.builder()
                .role("user")
                .content(sb.toString())  //활동 추천 추가.
                .build());

        return messages;
    }

    private static ResponseFormatDto generateRecommendSchema() { //스키마 지정.
        return ResponseFormatDto.builder()
                .type("json_schema")
                .json_schema(Map.of(
                                "name", "family_activate_recommendation_schema",
                                "strict", true,
                                "schema", Map.of(
                                        "type", "object",
                                        "properties", Map.of(
                                                "recommendations", Map.of(
                                                        "type", "array",
                                                        "items", Map.of(
                                                                "type", "object",
                                                                "properties", Map.of(
                                                                        "category", Map.of("type", "string",
                                                                        "enum",List.of("힐링/휴식", "스포츠/레저", "식사", "먹거리/음료", "창의/체험", "여행/탐방", "문화/예술")), //생성 카테고리 제약
                                                                        "items", Map.of(
                                                                                "type", "array",
                                                                                "minItems",5, //최대 최소 제약.
                                                                                "maxItems",5,
                                                                                "items", Map.of(
                                                                                        "type", "object",
                                                                                        "properties", Map.of(
                                                                                                "activity", Map.of("type", "string"),
                                                                                                "location", Map.of("type", "string"),
                                                                                                "description", Map.of("type", "string", //시간 조건 제약.
                                                                                                "pattern","^(?!.*((오전|오후)?\\s*(?:[01]?\\d|2[0-3])\\s*시(?:\\s*[0-5]?\\d\\s*분)?|\\b(?:[01]?\\d|2[0-3]):[0-5]\\d\\b|\\b[01]?\\d\\s*(?:am|pm)\\b|시간대)).+")
                                                                                        ),
                                                                                        "required", List.of("activity", "location", "description"),
                                                                                        "additionalProperties", false
                                                                                )
                                                                        )
                                                                ),
                                                                "required", List.of("category", "items"),
                                                                "additionalProperties", false
                                                        )

                                                )

                                        ),
                                        "required", List.of("recommendations"),
                                        "additionalProperties", false
                                )
                        )
                ).build();
    }



    private ResponseFormatDto generateQuestionSchema() {
        return ResponseFormatDto.builder()
                .type("json_schema")
                .json_schema(Map.of(
                                "name", "family_question_schema",
                                "strict", true,
                                "schema", Map.of(
                                        "type", "object",
                                        "properties", Map.of(
                                                "questions", Map.of(
                                                        "type", "array",
                                                        "items", Map.of(
                                                                "type", "object",
                                                                "properties", Map.of(
                                                                        "content", Map.of("type", "string")
                                                                ),
                                                                "required", List.of("content"),
                                                                "additionalProperties", false
                                                        )

                                                )

                                        ),
                                        "required", List.of("questions"),
                                        "additionalProperties", false
                                )
                        )
                ).build();
    }

    private List<MessageRequestDto> generateQuestionMessage() {
        List<MessageRequestDto> messages = new ArrayList<>();
        messages.add(MessageRequestDto.builder()
                .role("system")
                .content("""
                        너는 가족 커뮤니케이션 증진을 위한 질문 생성 도우미야.
                        
                        다음 JSON 스키마(strict 모드)에 완벽히 맞춰서 출력해야 해:
                        {
                          "questions": [
                            {
                              "content": "string"  // 질문 텍스트
                            }
                          ]
                        }
                        • "questions" 배열에 21개의 객체를 채워줌  
                        • 각 객체는 오직 "content" 필드만 가질 것  
                        • 배열 내 질문은 모두 고유(unique)해야 하며,
                          이전에 생성했던 질문과 절대 중복되지 않아야 해.
                        • 민감/사적 주제 금지: 건강, 정치, 종교, 연애·성, 금전(자산·소득·용돈), 외모·몸무게·성적/평가, 가족 간 비교/판단, 위치·신상 식별 정보.
                        • 공개적으로 공유해도 무난한 일상·취향·관찰 중심(가벼운 에피소드, 오늘의 발견, 소소한 추천 등).
                        • 다양성: 주제를 골고루 섞기(음식/취향/음악·콘텐츠/하늘·날씨/산책·풍경/작은 성취/감사/계획·소망/가벼운 유머/추천 등).
                        • 질문 형식은 요체 같은 형식으로 제공해줘야해.
                        • 질문 형식 예제 : "오늘 가족활동중 어느것이 가장 기억에 남았나요?
                            오늘 하루를 한 단어로 요약한다면 뭐였나요?
                            오늘 발견한 소소한 추천(간식·영상·앱)은 무엇이었나요?
                            오늘 스스로 잘했다고 느낀 순간은 언제였나요?"
                        """)
                .build());
        messages.add(MessageRequestDto.builder()
                .role("user")
                .content("새로운 가족과 화목해질수 있거나 가족 커뮤니케이션 증진용 질문 21개를 생성해주세요.")
                .build());
        return messages;
    }



    private OpenAiResponseDto getOpenAiRecommend(GroupScheduleInfoDto groupScheduleInfoDto, List<User> users) { //실질적 호출.
        OpenAiRequestDto openAiRequestDto = new OpenAiRequestDto();
        openAiRequestDto.setModel("gpt-5-mini");  //모델 설정.
        List<MessageRequestDto> messages = generateRecommendMessages(groupScheduleInfoDto, users); //메시지 생성.
        openAiRequestDto.setResponse_format(generateRecommendSchema());
        openAiRequestDto.setMessages(messages);
        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(openAiRequestDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(error -> Mono.error(new GptErrorException("gpt 오류 발생" + error))))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(error -> Mono.error(new GptErrorException("gpt오류 발생" + error))))
                .bodyToMono(OpenAiResponseDto.class)
                .block();

    }


}
