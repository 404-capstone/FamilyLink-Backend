package capstone._4.service.schedule;

import capstone._4.domain.Schedule;
import capstone._4.domain.User;
import capstone._4.dto.schedule.input.ScheduleUpdateRequest;
import capstone._4.dto.schedule.output.*;
import capstone._4.repository.schedule.CommentRepository;
import capstone._4.domain.sch_comment;
import capstone._4.dto.gpt.OpenAiRecommendComment;
import capstone._4.dto.group.output.GroupUserInfoDto;
import capstone._4.dto.schedule.input.CommentCreateRequest;
import capstone._4.dto.schedule.input.GroupScheduleInfoDto;
import capstone._4.enums.ErrorCode;
import capstone._4.repository.group.GroupsUserRepository;
import capstone._4.repository.schedule.ScheduleRepository;
import capstone._4.repository.user.UserRepository;
import capstone._4.service.other.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {

    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final GroupsUserRepository groupsUserRepository;
    private final OpenAiService openAiService;
    private final CommentRepository commentRepository;

    public ScheduleResponseDto getSchedule(Integer groupId) {  //그룹에 해당하는 유저를 찾고, 그룹 전체 가족 일정과,개인 일정들을 조회
        List<GroupUserInfoDto> groupUserInfo = groupsUserRepository.findBygroupId(groupId);
        List<Integer> member = groupUserInfo.stream().map(GroupUserInfoDto::getUserId).toList(); //멤버 찾고

        List<ScheduleInfoDto> scheduleInfoDtos = member.stream()
                .map(id -> {
                    List<Schedule> userScuedule = scheduleRepository.getUserSchedules(id);
                    List<PersonalSchedule> personalSchedules = userScuedule.stream()
                            .map(PersonalSchedule::new)
                            .toList(); //여기까지 personal스케쥴 만들고
                    return new ScheduleInfoDto(id, personalSchedules);
                }).toList();
        List<GroupScheduleDto> groupScheduleDto = scheduleRepository.getGroupSchedulesV2(groupId);

        return ScheduleResponseDto.builder()
                .groupId(groupId)
                .groupSchedule(groupScheduleDto)
                .personalUserSchedule(scheduleInfoDtos).build();
    }

    /**
     * @param groupScheduleInfoDto
     * @apiNote 1.먼저 그룹원들 나이를 조회
     * 2. 조회된 나이를 배열로 받고, openai 서비스 로 이동
     * 3. 서비스에서 나이,실내외여부,활동 들을 입력해서 추천.
     * 4. 반환된 api 결과를 프론트에게 제공
     */
    public OpenAiRecommendComment createRecommend(GroupScheduleInfoDto groupScheduleInfoDto) {
        List<Integer> usersId = groupScheduleInfoDto.getMemberIds();
        List<User> users = userRepository.findByIds(usersId);
        return openAiService.createRecommend(groupScheduleInfoDto, users);
    }


    /**
     * 일정 삭제
     *
     * @param scheduleId 삭제할 일정 ID
     * @return 삭제 성공 여부
     */
    @Transactional
    public boolean deleteScheduleById(Long scheduleId) {
        if (!scheduleRepository.existsById(scheduleId)) {
            return false; // 스케줄이 없으면 false 반환
        }
        scheduleRepository.deleteById(scheduleId);
        log.info("일정 삭제 완료: {}", scheduleId);
        return true;
    }

    @Transactional
    public CommentResponse addComment(CommentCreateRequest request) {
        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));

        sch_comment comment = new sch_comment();
        comment.setSchedule(schedule);
        comment.setBody(request.getContent());
        comment.setDateAt(LocalDate.now());

        sch_comment savedComment = commentRepository.save(comment);

        // 엔티티 -> DTO 변환
        return new CommentResponse(
                savedComment.getId(),
                savedComment.getBody(),
                savedComment.getDateAt(),
                savedComment.getSchedule().getId().longValue()
        );
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByScheduleId(Long scheduleId) {
        List<sch_comment> comments = commentRepository.findByScheduleId(scheduleId);

        return comments.stream()
                .map(c -> new CommentResponse(
                        c.getId(),
                        c.getBody(),
                        c.getDateAt(),
                        c.getSchedule().getId().longValue()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public ScheduleEditResponseDto updateSchedule(ScheduleUpdateRequest request) {
        Schedule schedule = scheduleRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));

        if (request.getTitle() != null) schedule.setTitle(request.getTitle());
        if (request.getStartTime() != null) schedule.setStartTime(request.getStartTime());
        if (request.getEndTime() != null) schedule.setEndTime(request.getEndTime());
        if (request.getContent() != null) schedule.setContent(request.getContent());
        if (request.getLocation() != null) schedule.setLocation(request.getLocation());
        if (request.getTimeflex() != null) schedule.setTimeflex(request.getTimeflex());

        Schedule updatedSchedule = scheduleRepository.save(schedule);

        return new ScheduleEditResponseDto(
                updatedSchedule.getId().longValue(),
                updatedSchedule.getTitle(),
                updatedSchedule.getStartTime(),
                updatedSchedule.getEndTime(),
                updatedSchedule.getContent(),
                updatedSchedule.getLocation(),
                updatedSchedule.getTimeflex()
        );
    }
}

