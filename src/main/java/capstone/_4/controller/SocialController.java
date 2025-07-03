package capstone._4.controller;

import capstone._4.dto.social.SocialInputDto;
import capstone._4.dto.social.SocialResultDto;
import capstone._4.service.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<?> naverLogin(@RequestBody @Valid SocialInputDto socialInputDto, HttpServletResponse response){
        SocialResultDto socialResultDto = userService.userSave(socialInputDto);
        response.setHeader("Authorization", "Bearer "+socialResultDto.getAccessToken());
        response.setHeader("Refresh-Token","Bearer "+ socialResultDto.getRefreshToken());
        return ResponseEntity.ok().body(socialResultDto);
    }
}
