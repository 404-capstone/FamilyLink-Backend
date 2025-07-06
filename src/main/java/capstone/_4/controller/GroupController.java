package capstone._4.controller;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.group.GroupInfoDto;
import capstone._4.dto.group.GroupGenerateDto;
import capstone._4.enums.ResponseEnum;
import capstone._4.service.GroupService;
import capstone._4.service.token.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> groupGeneration(@RequestParam("groupname") String name,@RequestParam String role, HttpServletRequest request) {
        String token=request.getHeader("Authorization").substring(7).trim();
        int id=jwtService.getIdFromToken(token);
        GroupGenerateDto groupResponseDto = groupService.generateGroup(name,id,role);
        return ResponseEntity.ok().body(new ApiResponseDto<>(
                ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(),
                groupResponseDto));
    }

    @GetMapping("/search")
    public ResponseEntity<?> groupSearch(@RequestParam Integer groupId) {
        GroupInfoDto groupInfoDto=groupService.searchGroup(groupId);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(),groupInfoDto));
    }

    @PostMapping("/code")
    public ResponseEntity<?> groupCodeGeneration(@RequestParam Integer groupid){
        String code=groupService.generateCode(groupid);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(),code));
    }

    @PostMapping("/search/code")
    public ResponseEntity<?> groupSerachwithCode(@RequestParam String code) {
        int groupid=groupService.searchGroupWithCode(code);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(),groupid ));
    }

    @PostMapping("/access")
    public ResponseEntity<?> groupAccess(@RequestParam String code,@RequestParam String role,HttpServletRequest request){
        String token=request.getHeader("Authorization").substring(7).trim();
        int id=jwtService.getIdFromToken(token);
        GroupGenerateDto groupGenerateDto=groupService.accessGroupWithCode(code,id,role);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(),groupGenerateDto));
    }

    /**
     *여기 response부분 수정하기.
     */

    @DeleteMapping("/quit")
    public ResponseEntity<?> groupQuit(@RequestParam Integer groupId,HttpServletRequest request) {
        String token=request.getHeader("Authorization").substring(7).trim();
        int id=jwtService.getIdFromToken(token);
        groupService.quitGroup(groupId,id);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.QUIT_SUCCESS.getCode(),
                ResponseEnum.QUIT_SUCCESS.getMessage(),id));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> groupDelete(@RequestParam Integer groupId,HttpServletRequest request) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.DELETE_SUCCESS.getCode(),
                ResponseEnum.DELETE_SUCCESS.getMessage(),groupId));
    }

}
