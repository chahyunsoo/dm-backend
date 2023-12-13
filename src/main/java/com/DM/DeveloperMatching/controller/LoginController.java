package com.DM.DeveloperMatching.controller;

import com.DM.DeveloperMatching.config.jwt.JwtTokenUtils;
import com.DM.DeveloperMatching.domain.User;
import com.DM.DeveloperMatching.dto.Login.LoginRequest;
import com.DM.DeveloperMatching.dto.Login.LoginResponse;
import com.DM.DeveloperMatching.service.RegisterAndLoginService;
import com.DM.DeveloperMatching.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class LoginController {

    private final RegisterAndLoginService registerAndLoginService;
    @Value("${jwt.secret-key}")
    private String secretKey;
    private UserService userService;

//    @PostMapping("/login")
//    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
//        User loginUser = registerAndLoginService.login(loginRequest);
//
//        if (loginUser == null) {
//            // 로그인 실패 시 LoginResponse 객체 생성
//            LoginResponse loginResponse = new LoginResponse(HttpStatus.BAD_REQUEST, "로그인 Email 또는 설정한 비밀번호가 틀렸습니다.", null);
//            return ResponseEntity.badRequest().body(loginResponse);
//        }
//
//        // 로그인 성공 시 JWT 토큰 생성 및 LoginResponse 객체 생성
//        String secretKey = ""; //시크릿 키
//        long expireTimeMs = 1000 * 60 * 30; // Token 유효 시간 = 30분
//        String jwtToken = JwtTokenUtils.createToken(loginUser.getUId(), loginUser.getEmail(), secretKey, expireTimeMs);
//
//        LoginResponse loginResponse = new LoginResponse(HttpStatus.OK, "토큰이 정상적으로 발급되었습니다.", jwtToken);
//        return ResponseEntity.ok().body(loginResponse);
//    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        User loginUser = registerAndLoginService.login(loginRequest);

        if (loginUser == null) {
            // 로그인 실패 시 LoginResponse 객체 생성
            LoginResponse loginResponse = new LoginResponse(HttpStatus.BAD_REQUEST, "로그인 Email 또는 설정한 비밀번호가 틀렸습니다.", null, null, null);
            return ResponseEntity.badRequest().body(loginResponse);
        }

        // 로그인 성공 시 JWT 토큰 생성 및 LoginResponse 객체 생성
        long expireTimeMs = 1000 * 60 * 600; // Token 유효 시간 = 10시간
        String jwtToken = JwtTokenUtils.createToken(loginUser.getUId(), loginUser.getEmail(), secretKey, expireTimeMs);

        Long uId = JwtTokenUtils.extractUserId(jwtToken, secretKey);

        String nickName = loginUser.getNickName();

        LoginResponse loginResponse = new LoginResponse(HttpStatus.OK, "토큰이 정상적으로 발급되었습니다.", jwtToken, uId,nickName);
        return ResponseEntity.ok().body(loginResponse);
    }
}