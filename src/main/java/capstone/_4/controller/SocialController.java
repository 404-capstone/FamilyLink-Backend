package capstone._4.controller;

import capstone._4.dto.SocialResultDto;
import capstone._4.service.SocialService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class SocialController {

    private final SocialService socialService;

    @Autowired
    public SocialController(SocialService socialService) {
        this.socialService = socialService;
    }

    @PostMapping("/login/naver")
    public ResponseEntity<?> naverLogin(@RequestHeader(HttpHeaders.AUTHORIZATION)String authToken){
        SocialResultDto socialResultDto =socialService.naverLoginService(authToken);
        return ResponseEntity.ok().build();
    }
}
