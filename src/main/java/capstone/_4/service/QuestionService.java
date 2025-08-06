package capstone._4.service;

import capstone._4.domain.QuestionInventory;
import capstone._4.dto.gpt.OpenAiQuestionContent;
import capstone._4.repository.DiaryRepository;
import capstone._4.repository.QuestionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;


    @Transactional
    public void questionSave(OpenAiQuestionContent content){ //생성된 질문 저장.
        List<QuestionInventory> q=content.getQuestions().stream()
                .map(OpenAiQuestionContent.Question::getContent)
                .map(QuestionInventory::new)
                .toList(); //문제 빼오기

        questionRepository.saves(q);

        while(q.size()>0) {

            for (int i = 0; i < 3; i++) {

            }
        }

    }

}
