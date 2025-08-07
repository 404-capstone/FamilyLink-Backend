package capstone._4.service;

import capstone._4.domain.question.QuestionInfoList;
import capstone._4.domain.question.QuestionInventory;
import capstone._4.domain.question.QuestionList;
import capstone._4.dto.gpt.OpenAiQuestionContent;
import capstone._4.repository.QuestionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;


    /**
     * 해당 메서드는, gpt로 생성 된 질문들을 각각 db에 저장하는것이다.
     * @apiNote 1. questionInventory는 문제 원형들이다
     * 2. questionList는 위에 인벤토리를 묶는 리스트이다
     * 3. questionInfoList는 위에 인벤토리와 리스트에 메타데이터를 저장하는 엔티티이다.
     * list에서 cascadeall 해놨고, orphanRemoval true로 해놔서 리스트만 저장시 관련된 info 메타데이터도 함께 저장된다.
     * @param content
     */
    @Transactional
    public void questionSave(OpenAiQuestionContent content){ //생성된 질문 저장.
        List<QuestionInventory> q=content.getQuestions().stream()
                .map(OpenAiQuestionContent.Question::getContent)
                .map(QuestionInventory::new)
                .toList(); //문제 빼오기

        questionRepository.saves(q);
        try {
            log.info("문제 저장 시작.");
            for (int i = 0; i < q.size(); i += 3) {
                List<QuestionInventory> arrayinventroy = q.subList(i, Math.min(i + 3, q.size()));
                QuestionList questionList = new QuestionList();

                //위에는,문제 3개식 빼고 리스트 생성.
                for(int j=0;j<arrayinventroy.size();j++){
                    QuestionInventory inventory=arrayinventroy.get(j);
                    QuestionInfoList questionInfoList=new QuestionInfoList();
                    inventory.changeInfo(questionInfoList);
                    questionInfoList.changeSlot(j+1);
                    questionList.changeInfo(questionInfoList);
                }
                questionRepository.saveList(questionList);
                //questionRepository.saveInfo(infoList);
            }
            log.info("문제 저장 완료.");
        }catch(Exception e){
            throw new RuntimeException("오류가 발생했습니다.: "+e.getMessage());
        }



    }

}
