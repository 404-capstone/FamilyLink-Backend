package capstone._4.controller;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.ServeyDto;
import capstone._4.dto.group.GroupInfoResponseDto;
import capstone._4.dto.group.GroupGenerateDto;
import capstone._4.enums.ResponseEnum;
import capstone._4.service.GroupService;
import capstone._4.service.token.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @PostMapping(value = "/generation",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> groupGeneration(@RequestParam("groupname") String name,@RequestParam String role,@RequestParam(required = false) MultipartFile image
            ,HttpServletRequest request) {
        int id = tokenTakeUserId(request);
        GroupGenerateDto groupResponseDto = groupService.generateGroup(name,id,role,image);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(
                ResponseEnum.GENERATE_COMPLETED.getCode(), ResponseEnum.SUCCESS.getMessage(),
                groupResponseDto));
    }

    @GetMapping("/search")
    public ResponseEntity<?> groupSearch(@RequestParam Integer groupId) {
        GroupInfoResponseDto groupInfoResponseDto =groupService.searchGroup(groupId);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(), groupInfoResponseDto));
    }

    @PostMapping("/code")
    public ResponseEntity<?> groupCodeGeneration(@RequestParam Integer groupid){
        String code=groupService.generateCode(groupid);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(ResponseEnum.GENERATE_COMPLETED.getCode(),
                ResponseEnum.GENERATE_COMPLETED.getMessage(),code));
    }

    @GetMapping("/code/search")
    public ResponseEntity<?> groupCodeSearch(@RequestParam Integer groupid){
        String code=groupService.searchCode(groupid);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(),code));
    }


    @PostMapping("/access/search/code")
    public ResponseEntity<?> groupSerachwithCode(@RequestParam String code) {
        int groupid=groupService.searchGroupWithCode(code);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(),groupid ));
    }

    @PostMapping("/access")
    public ResponseEntity<?> groupAccess(@RequestParam String code,@RequestParam String role,HttpServletRequest request){
        int id = tokenTakeUserId(request);
        GroupGenerateDto groupGenerateDto=groupService.accessGroupWithCode(code,id,role);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(),groupGenerateDto));
    }

    /**
     *여기 response부분 수정하기.
     */

    @DeleteMapping("/quit")
    public ResponseEntity<?> groupQuit(@RequestParam Integer groupId,HttpServletRequest request) {
        int id = tokenTakeUserId(request);
        groupService.quitGroup(groupId,id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponseDto<>(ResponseEnum.QUIT_SUCCESS.getCode(),
                ResponseEnum.QUIT_SUCCESS.getMessage(),id));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> groupDelete(@RequestParam Integer groupId,HttpServletRequest request) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponseDto<>(ResponseEnum.DELETE_SUCCESS.getCode(),
                ResponseEnum.DELETE_SUCCESS.getMessage(),groupId));
    }

    @PatchMapping(value = "/edit",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> groupEdit(@RequestParam Integer groupId, @RequestParam String name, @RequestParam(required = false) MultipartFile image) {
        log.info("start");
        groupService.updateGroup(groupId,name,image);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponseDto<>(ResponseEnum.UPDATE_SUCCESS.getCode(),
                ResponseEnum.UPDATE_SUCCESS.getMessage(),groupId));
    }

    @DeleteMapping("/user/delete")
    public ResponseEntity<?> groupUserDelete(@RequestParam Integer groupId,@RequestParam Integer userId) {
        groupService.deleteUserWithGroup(groupId,userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponseDto<>(ResponseEnum.DELETE_SUCCESS.getCode(),
                ResponseEnum.DELETE_SUCCESS.getMessage(),userId));
    }

    @PostMapping("/servey/save")
    public ResponseEntity<?> serveySave(@RequestParam Integer groupId, @RequestBody ServeyDto dto, HttpServletRequest request) {
        int id=tokenTakeUserId(request);
        groupService.saveScore(dto,groupId,id);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(),"점수저장에 성공했습니다."));
    }


    @GetMapping("/servey/search")
    public ResponseEntity<?> serveySearch(@RequestParam Integer groupId,HttpServletRequest request) {
        int id=tokenTakeUserId(request);
        ServeyDto responseDto=groupService.searchUserScore(groupId,id);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(),responseDto));
    }

    private int tokenTakeUserId(HttpServletRequest request) {
        String token= request.getHeader("Authorization");
        return jwtService.returnToken(token);
    }
}
