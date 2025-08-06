package capstone._4.service;

import capstone._4.domain.GroupQuestion;
import capstone._4.domain.QuestionInventory;
import capstone._4.domain.User;
import capstone._4.dto.diary.GroupQuestionDetailResponse;
import capstone._4.dto.diary.GroupQuestionResponseDto;
import capstone._4.dto.diary.QuestionAnswerResponse;
import capstone._4.dto.group.output.GroupUserInfoDto;
import capstone._4.repository.DiaryRepository;
import capstone._4.repository.QuestionRepository;
import capstone._4.repository.group.GroupsUserRepository;
import capstone._4.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final GroupsUserRepository groupsUserRepository;
    private final QuestionRepository questionRepository;

    @Transactional
    public void deleteDiary(Integer diaryId) {
        try{
            diaryRepository.deleteById(diaryId);
        }catch (EntityNotFoundException e){
            throw new EntityNotFoundException("다이어리 삭제를 실패했습니다: "+e.getMessage());
        }
    }

    public GroupQuestionDetailResponse searchQuestion(Integer qaId) {

        List<GroupQuestion> questionsInfo=diaryRepository.findAllQuestion(qaId); //질문지 정보,이미 전에 조회했을때 그룹정보를 썻기때문에 여기서는 필요x

        List<Integer> questionIds = questionsInfo.stream() //문제 id 정리.
                .map(GroupQuestion::getId).toList();

        Map<Integer,List<QuestionAnswerResponse>> questionAnswerResponses =diaryRepository.findAllAnswer(questionIds); //문제id를 중점으로 가족 응답 response 존재.

        List<GroupQuestionResponseDto> questionResponseDto= new ArrayList<>();
        for(GroupQuestion groupQuestion:questionsInfo){ //그룹 질문 가져오기.
            Integer questionId=groupQuestion.getId();

            GroupQuestionResponseDto groupQuestionResponseDto=
                   GroupQuestionResponseDto.builder()
                           .questionId(questionId)
                           .question(groupQuestion.getQuestionInventory().getContent())
                           .answerInfo(questionAnswerResponses.getOrDefault(questionId, Collections.emptyList()))
                           .build();
            questionResponseDto.add(groupQuestionResponseDto);
        }

        LocalDate time=questionsInfo.get(0).getDay();
        return GroupQuestionDetailResponse.builder()
                .questionInfo(questionResponseDto)
                .date(time)
                .questionListId(qaId)
                .build();
    }
}
