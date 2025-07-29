package capstone._4.service;

import capstone._4.domain.User;
import capstone._4.dto.gpt.MessageRequestDto;
import capstone._4.dto.gpt.OpenAiRequestDto;
import capstone._4.dto.gpt.ResponseFormatDto;
import capstone._4.dto.gpt.recommendResponseDto;
import capstone._4.dto.schedule.OpenAiRecommendResponse;
import capstone._4.dto.schedule.input.ActivityPersonality;
import capstone._4.dto.schedule.input.GroupScheduleInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OpenAiService {

    private final WebClient webClient;

    public OpenAiService(@Qualifier("OpenAiWebClient") WebClient webClient){
        this.webClient = webClient;
    }



    public OpenAiRecommendResponse createRecommend(GroupScheduleInfoDto groupScheduleInfoDto, List<User> users) {//MessageRequestDto messageRequestDto
        log.info("start createMessage");
        OpenAiRequestDto openAiRequestDto = new OpenAiRequestDto();
        openAiRequestDto.setModel("gpt-4.1-nano");  //모델 설정.
        //ResponseFormatDto responseFormatDto= getResponseFormatDto();
        List<MessageRequestDto> messages = generateMessages(groupScheduleInfoDto,users); //메시지 생성.
        openAiRequestDto.setResponse_format(generateSchema());
        openAiRequestDto.setMessages(messages);
        recommendResponseDto recommendResponseDto;
        OpenAiRecommendResponse openAiRecommendResponse =webClient.post()
                .uri("/chat/completions")
                .bodyValue(openAiRequestDto)
                .retrieve()
                .bodyToMono(OpenAiRecommendResponse.class)
                .block();
        log.info("response:\n{}", openAiRecommendResponse.getChoices());
        return openAiRecommendResponse;
    }



    private static List<MessageRequestDto> generateMessages(GroupScheduleInfoDto groupScheduleInfoDto,List<User> users) {
        List<MessageRequestDto> messages = new ArrayList<>();
        DateTimeFormatter dtf= DateTimeFormatter.ofPattern("HH:mm");
        StringBuilder sb=new StringBuilder();
        messages.add(MessageRequestDto.builder()
                .role("system")
                .content("너는 지금부터 가족 커뮤니케이션 증진을 위한 도우미야.")
                .build());
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
                                "                •  recommendations 배열안에 category당 하나의 활동분류,오브젝트를 생성해줘."+
                                "                    예) [\"힐링\"],[\"문화\"] 이런식으로 각각                                                  "+
                                "                • 추가 필드는 허용되지 않습니다.")
                .build());
        messages.add(MessageRequestDto.builder()
                .role("user")
                .content("활동 위치는"+groupScheduleInfoDto.getArea()+",인원정보는 "+groupScheduleInfoDto.getMemberIds().size()+"명, 나이대는 각[" +
                        users.stream().map(user->user.getAge()+"대 "+user.getGender()).collect(Collectors.joining(","))
                        +"], 활동시간대는"+ groupScheduleInfoDto.getStartTime().format(dtf)+"/"+groupScheduleInfoDto.getEndTime().format(dtf)
                        +"이고 "+groupScheduleInfoDto.getInoutdoor()+"에서 하기를 원해.")
                .build());
        log.info("메시지 정보:\n{}", messages.get(2).getContent());
        sb.append("활동분류는 ");

        sb.append(groupScheduleInfoDto.getActivityPersonalityList().stream().map(activityPersonality -> activityPersonality.getType())
                .collect(Collectors.joining(",")));
//        for(ActivityPersonality activity:groupScheduleInfoDto.getActivityPersonalityList()){
//            sb.append(activity.getType()+",");
//        }
        sb.append("로 생성해줘.");
        log.info("생성 프롬포트:{}",sb.toString());
        messages.add(MessageRequestDto.builder()
                .role("user")
                .content(sb.toString())  //활동 추천 추가.
                .build());




//        messages.add(MessageRequestDto.builder()
//                        .role("system")
//                        .content("내가 너에게 질문할때 제공할 정보는 다음과 같아" +
//                                "활동 위치: 시,구 까지만 제공하고, 같은구나,주변구까지만 찾아서 추천해줘.(ex)서울시 강남구) " +
//                                "활동 시간대: 일정 시작 시간,종료시간을 제공해서, 해당 시간대에 적절한 활동을 추천해줘(ex) 25.07.25 15:00 / 18:00) " +
//                                "활동 참여자: 몇명이 참여하며, 각각 나이대가 어떤지에 대해 제공할거야(ex) 4명,(70대,50대,20대,20대)" +
//                                "실내/실외 선호도: 실내에서 활동을 할지,실외에서 활동을 할지 제공해주어서 거기에 맞는 활동을 추천해주면되.(ex: 실내)" +
//                                "활동 분류: 사용자가 원하는 활동 카테고리로 해당 정보를 중심으로 추천해주면되.(힐링/휴식, 스포츠/레저, 식사, 먹거리/음료, 창의/체험, 여행/탐방, 문화/예술)")
//                .build());
//        messages.add(MessageRequestDto.builder()
//                        .role("system")
//                        .content("제공해준 json스키마 형태에 다음과 같이 맞추어서 응답을 해줘야되:" +
//                                "recommendations: 활동 카테고리별 추천 목록" +
//                                "- category: 활동분류.(힐링/휴식, 스포츠/레저, 식사, 먹거리/음료, 창의/체험, 여행/탐방, 문화/예술)" +
//                                "- items: 해당하는 카테고리에 해당하는 활동 리스트" +
//                                "   -activity: 활동 이름(ex: 전주 비빔밥 먹기.)" +
//                                "   -location: 활동 지역 (ex: 인천 송도) " +
//                                "   -description: 활동 설명(ex: 전주에서 유명한 비빔밥 먹기.)")
//                .build());

        return messages;
    }

    private static ResponseFormatDto generateSchema() { //스키마 지정.
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
                                                                "required", List.of("category","items"),
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



}
