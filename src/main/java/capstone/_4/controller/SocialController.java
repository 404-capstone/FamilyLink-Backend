package capstone._4.controller;

import capstone._4.dto.SocialResultDto;
import capstone._4.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class SocialController {

    private final UserService userService;

    @Autowired
    public SocialController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login/naver")
    public ResponseEntity<?> naverLogin(@RequestHeader(HttpHeaders.AUTHORIZATION)String authToken){
        SocialResultDto socialResultDto = userService.userSave(authToken);
        return ResponseEntity.ok().body(socialResultDto);
    }
}
