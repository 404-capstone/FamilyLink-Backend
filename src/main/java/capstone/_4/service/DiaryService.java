package capstone._4.service;

import capstone._4.repository.DiaryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;

    @Transactional
    public void deleteDiary(Integer diaryId) {
        try{
            diaryRepository.deleteById(diaryId);
        }catch (EntityNotFoundException e){
            throw new EntityNotFoundException("다이어리 삭제를 실패했습니다: "+e.getMessage());
        }
    }
}
