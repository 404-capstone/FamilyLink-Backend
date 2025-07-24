package capstone._4.service;

import capstone._4.dto.gpt.MessageRequestDto;
import capstone._4.dto.gpt.OpenAiRequestDto;
import capstone._4.dto.gpt.ResponseFormatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GptService {

    private final WebClient webClient;

    public void createMessage(MessageRequestDto messageRequestDto) {
        OpenAiRequestDto openAiRequestDto = new OpenAiRequestDto();
        openAiRequestDto.setModel("gpt-4.1-mini");
        //ResponseFormatDto responseFormatDto= getResponseFormatDto();
        List<MessageRequestDto> messages = generateMessages();
        openAiRequestDto.setResponse_format(generateSchema());
        openAiRequestDto.setMessages(messages);
        webClient.post()
                .uri("/chat/completions")
                .bodyValue(openAiRequestDto)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }



    private static List<MessageRequestDto> generateMessages() {
        List<MessageRequestDto> messages = new ArrayList<>();
        messages.add(MessageRequestDto.builder()
                .role("system")
                .content("너는 지금부터 가족 커뮤니케이션 증진을 위한 도우미야.")
                .build());

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
                                                "recommendation", Map.of(
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
                                        "required", List.of("recommendation"),
                                        "additionalProperties", false
                                )
                        )
                ).build();
    }


}
