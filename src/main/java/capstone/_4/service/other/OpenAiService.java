package capstone._4.service.other;

import capstone._4.domain.User;
import capstone._4.dto.gpt.*;
import capstone._4.dto.gpt.OpenAiResponseDto;
import capstone._4.dto.schedule.input.GroupScheduleInfoDto;
import capstone._4.exception.GptErrorException;
import capstone._4.service.QuestionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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

    public OpenAiService(@Qualifier("OpenAiWebClient") WebClient webClient,
                         QuestionService questionService) {
        this.webClient = webClient;
        this.questionService = questionService;
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
        openAiRequestDto.setModel("gpt-5-nano");  //모델 설정
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
                        "                • 활동 분류: 힐링/휴식, 스포츠/레저, 식사/음료, 창의/체험, 여행/탐방, 문화/예술\n" +
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
                        "                    예) [\"힐링/휴식\"],[\"문화/예술\"] 이런식으로 각각                                                  " +
                        "                • 추가 필드는 허용되지 않습니다.")
                .build());
        messages.add(MessageRequestDto.builder()
                .role("user")
                .content("활동 위치는 " + groupScheduleInfoDto.getArea() + ",인원정보는 " + groupScheduleInfoDto.getMemberIds().size() + "명, 나이대는 각[" +
                        users.stream().map(user -> user.getAge() + "대 " + user.getGender()).collect(Collectors.joining(","))
                        + "], 활동시간대는" + groupScheduleInfoDto.getStartTime().format(dtf) + "/" + groupScheduleInfoDto.getEndTime().format(dtf)
                        + "이고 " + groupScheduleInfoDto.getInoutdoor() + "에서 하기를 원해.")
                .build());
        log.info("메시지 정보:\n{}", messages.get(2).getContent());
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
                                                                        "category", Map.of("type", "string"),
                                                                        "items", Map.of(
                                                                                "type", "array",
                                                                                "items", Map.of(
                                                                                        "type", "object",
                                                                                        "properties", Map.of(
                                                                                                "activity", Map.of("type", "string"),
                                                                                                "location", Map.of("type", "string"),
                                                                                                "description", Map.of("type", "string")
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
                        • 질문 형식은 요체 같은 형식으로 제공해줘야해.
                        • 질문 형식 예제 : "오늘 가족활동중 어느것이 가장 기억에 남았나요?"
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
        openAiRequestDto.setModel("gpt-4.1-nano");  //모델 설정.
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
