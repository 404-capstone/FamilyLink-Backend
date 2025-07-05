package capstone._4.controller;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.group.GroupResponseDto;
import capstone._4.enums.ResponseEnum;
import capstone._4.service.GroupService;
import capstone._4.service.token.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/group")
@Slf4j
public class GroupController {

    private final GroupService groupService;
    private final JwtService jwtService;

    @Autowired
    public GroupController(GroupService groupService,JwtService jwtService){
        this.groupService = groupService;
        this.jwtService = jwtService;
    }


    /**
    * 그룹을 저장한다
    * @apiNote 1.그룹 이름과 토큰을 받음
     * 2. 새로 그룹을 repo클래스에서 생성
     * 3. 생성된 그룹을 반환.
    * @param name 그룹이름
    * @return : dto
    * */
    @PostMapping("/generation")
    public ResponseEntity<?> groupGeneration(@RequestParam("groupname") String name, HttpServletRequest request) {
        int id=jwtService.getIdFromToken(request.getHeader("Authorization"));
        GroupResponseDto groupResponseDto = groupService.generateGroup(name,id);
        return ResponseEntity.ok().build(new ApiResponseDto<>(
                ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(),
                groupResponseDto
        ));
    }
}
