package capstone._4.service.schedule;

import capstone._4.domain.*;
import capstone._4.dto.schedule.*;
import capstone._4.dto.schedule.input.ScheduleUpdateRequest;
import capstone._4.dto.schedule.output.*;
import capstone._4.exception.FastApiException;
import capstone._4.repository.schedule.CommentRepository;
import capstone._4.dto.gpt.OpenAiRecommendComment;
import capstone._4.dto.group.output.GroupUserInfoDto;
import capstone._4.dto.schedule.input.CommentCreateRequest;
import capstone._4.dto.schedule.input.GroupScheduleInfoDto;
import capstone._4.repository.group.GroupsUserRepository;
import capstone._4.repository.schedule.ScheduleRepository;
import capstone._4.repository.user.UserRepository;
import capstone._4.service.other.OpenAiService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ScheduleService {

    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final GroupsUserRepository groupsUserRepository;
    private final OpenAiService openAiService;
    private final CommentRepository commentRepository;
    private final WebClient webClient;

    public ScheduleService(UserRepository userRepository, ScheduleRepository scheduleRepository,
                           GroupsUserRepository groupsUserRepository,
                           OpenAiService openAiService,
                           CommentRepository commentRepository,
                           @Qualifier("FastApiWebClient") WebClient webClient) {
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.groupsUserRepository = groupsUserRepository;
        this.openAiService = openAiService;
        this.commentRepository = commentRepository;
        this.webClient = webClient;
    }

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
    //댓글작성
    @Transactional
    public CommentResponse addComment(CommentCreateRequest request) {
        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));

        User user = userRepository.findById(request.getUserId().intValue())
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        sch_comment comment = new sch_comment();
        comment.setSchedule(schedule);
        comment.setUser(user);
        comment.setBody(request.getContent());
        comment.setDateAt(LocalDate.now());

        sch_comment savedComment = commentRepository.save(comment);

        // Schedule -> ScheduleResponse 변환
        ScheduleResponse scheduleDto = convertToScheduleResponse(schedule);

        return new CommentResponse(
                savedComment.getId(),
                savedComment.getBody(),
                savedComment.getDateAt(),
                schedule.getId().longValue(),
                scheduleDto
        );
    }
    //일정 + 댓글 조회
    @Transactional(readOnly = true)
    public ScheduleWithCommentsResponse getScheduleWithComments(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));

        // 참여자 리스트
        List<Long> participantIds = schedule.getGroupsSchedule().stream()
                .map(gs -> gs.getUser().getId().longValue())
                .collect(Collectors.toList());

        // 댓글 리스트
        List<CommentSimpleResponse> commentList = commentRepository.findByScheduleId(scheduleId).stream()
                .map(c -> new CommentSimpleResponse(
                        c.getId(),
                        c.getBody(),
                        c.getDateAt(),
                        c.getUser().getId().longValue()
                ))
                .collect(Collectors.toList());
        //개인 일정만 퍼미션
        Boolean permission = null;
        if (schedule.getUser() != null) { // 개인 일정이면
            permission = schedule.getPermission();
        }

        return ScheduleWithCommentsResponse.builder()
                .scheduleId(schedule.getId())
                .title(schedule.getTitle())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .timeflex(schedule.getTimeflex())
                .location(schedule.getLocation())
                .content(schedule.getContent())
                .participantIds(participantIds)
                .permission(permission)
                .comments(commentList)
                .build();
    }





    /**
     * Schedule 엔티티를 ScheduleResponse로 변환
     */
    private ScheduleResponse convertToScheduleResponse(Schedule schedule) {
        if (schedule.getGroupsSchedule() != null && !schedule.getGroupsSchedule().isEmpty()) {
            // groupsSchedule 리스트에서 userId 리스트 추출
            List<Integer> participantIds = schedule.getGroupsSchedule().stream()
                    .map(gs -> gs.getUser().getId())
                    .collect(Collectors.toList());

            return new GroupScheduleResponse(
                    schedule.getId(),
                    schedule.getTitle(),
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    schedule.getTimeflex(),
                    schedule.getContent(),
                    schedule.getLocation(),
                    participantIds,
                    schedule.getCalendar() != null ? schedule.getCalendar().getId() : null
            );
        } else {
            return new PersonalScheduleResponse(
                    schedule.getId(),
                    schedule.getTitle(),
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    schedule.getTimeflex(),
                    schedule.getPermission()
            );
        }
    }


    //일정 수정
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


        if (request.getParticipantIds() != null) {
            schedule.getGroupsSchedule().clear();
            Groups group = schedule.getGroup();
            if (group == null) {
                throw new RuntimeException("스케줄에 연결된 그룹이 없습니다.");
            }
            int groupId = group.getId();

            List<GroupsSchedule> newParticipants = request.getParticipantIds().stream()
                    .map(userId -> {
                        // 그룹에 속한 유저인지 체크
                        boolean exists = groupsUserRepository.existsByGroupIdAndUserid(groupId, userId.intValue());
                        if (!exists) {
                            log.warn("그룹 {}에 없는 유저 {}가 추가 시도됨", groupId, userId);
                            throw new RuntimeException("그룹에 속하지 않은 유저 " + userId + "를 추가할 수 없습니다.");
                        }
                        // 유저 엔티티 조회
                        User user = userRepository.findById(userId.intValue())
                                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다: " + userId));
                        // 그룹 스케줄 생성
                        GroupsSchedule gs = new GroupsSchedule();
                        gs.setSchedule(schedule);
                        gs.setUser(user);
                        return gs;
                    })
                    .collect(Collectors.toList());

            schedule.getGroupsSchedule().addAll(newParticipants);
        }

        Schedule updatedSchedule = scheduleRepository.save(schedule);

        return new ScheduleEditResponseDto(
                updatedSchedule.getId().longValue(),
                updatedSchedule.getTitle(),
                updatedSchedule.getStartTime(),
                updatedSchedule.getEndTime(),
                updatedSchedule.getContent(),
                updatedSchedule.getLocation(),
                updatedSchedule.getTimeflex(),
                updatedSchedule.getGroupsSchedule().stream()
                        .map(gs -> Optional.ofNullable(gs.getUser())
                                .map(User::getId)
                                .map(Long::valueOf)
                                .orElse(null))
                        .collect(Collectors.toList())
        );
    }



    public OptimalResponse optimalSchedule(ScheduleOptimizeRequest optimalSchedule) {
        log.info("유저 찾기.");
        List<String> userRole = userRepository.findByIds(optimalSchedule.getMemberIds())
                .stream().map((u)->u.getGroupsuser().get(0).getRole()).toList();
        List<Schedule>personalSchedule=scheduleRepository.getScheduleWithDay(optimalSchedule.getGroupId(),optimalSchedule.getDate(),optimalSchedule.getMemberIds());
        if(personalSchedule.isEmpty()){
            throw new EntityNotFoundException("해당 날짜에 일정이 존재하지 않습니다.");
        }
        log.info("유저별 스케쥴 나누기 실행.");
        Map<Integer,List<Schedule>> byUserScheduel=personalSchedule
                .stream().collect(Collectors.groupingBy(s->s.getUser().getId()));
//유저와 스케쥴을 모으고. 유저별로 나누어서 주기.
        log.info("요청 dto작성");
        List<ScheduleOptimizeApiRequestDto> detailSchedule=optimalSchedule.getMemberIds().stream()
                .map((id)->{
                    return new ScheduleOptimizeApiRequestDto(id,
                            byUserScheduel.getOrDefault(id, List.of()));
                })
                .toList();
        OptimizeRequest optimizeRequest=new OptimizeRequest(optimalSchedule,detailSchedule);

        //fastapi 요청.
        log.info("api요청");
         SchedulelOptimizeApiResponse schedulelOptimizeApiResponse =webClient.post().uri("/schedule/optimization")
                .bodyValue(optimizeRequest)
                .retrieve()
                 .onStatus(HttpStatusCode::is4xxClientError,cr->
                     cr.bodyToMono(String.class).flatMap(error-> Mono.error(new FastApiException("최적화중 오류가 발생했습니다:"+error)))
                 )
                 .onStatus(HttpStatusCode::is5xxServerError,cr->
                         cr.bodyToMono(String.class).flatMap(error->Mono.error(new FastApiException("최적화중 오류가 발생했습니다."+error))))
                .bodyToMono(SchedulelOptimizeApiResponse.class)
                .block();

        log.info("응답하기.");
        return new OptimalResponse(optimalSchedule.getGroupId(),new BeforeSchedule(personalSchedule),
                new AfterSchedule(personalSchedule,schedulelOptimizeApiResponse,
                optimalSchedule.getTitle(),optimalSchedule.getMemberIds(),userRole));

    }

    private static List<Schedule> getSchedule(List<Schedule> schedule) {
        return schedule;
    }
}

