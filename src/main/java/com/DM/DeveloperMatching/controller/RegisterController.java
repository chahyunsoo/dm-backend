package com.DM.DeveloperMatching.controller;

import com.DM.DeveloperMatching.dto.Email.EmailRequest;
import com.DM.DeveloperMatching.dto.Register.RegisterRequest;
import com.DM.DeveloperMatching.service.MailService;
import com.DM.DeveloperMatching.service.RegisterAndLoginService;
import com.DM.DeveloperMatching.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class RegisterController {

    private final UserService userService;
    private final MailService mailService;
    private final RegisterAndLoginService registerAndLoginService;

    @GetMapping("/root")
    public ResponseEntity root() {
        return ResponseEntity.status(HttpStatus.OK).body("good");
    }
    @PostMapping("/register/check-nickname")
    public ResponseEntity<String> registerNickName(@RequestBody RegisterRequest registerRequest) {
        if (registerAndLoginService.checkNickNameDuplicate(registerRequest.getNickName())) {
            return ResponseEntity.badRequest().body("중복되는 별명이 존재합니다.");
        }
        else return ResponseEntity.ok().body("사용 가능한 별명입니다.");
    }

    @PostMapping("/register/check-email")
    public ResponseEntity<String> registerEmail(@RequestBody EmailRequest emailRequest) {
        if (registerAndLoginService.checkLoginEmailDuplicate(emailRequest.getEmail())) {
            return ResponseEntity.badRequest().body("중복되는 Email이 존재합니다.");
        } else {
            int authNumber = mailService.sendMail(emailRequest.getEmail());
            String number = "" + authNumber;
            return ResponseEntity.ok().body(number);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
//        boolean isNicknameDuplicate = registerAndLoginService.checkNickNameDuplicate(registerRequest.getNickName());
//        boolean isEmailDuplicate = registerAndLoginService.checkLoginEmailDuplicate(registerRequest.getEmail());
//        if (isEmailDuplicate || isNicknameDuplicate) {
//            StringBuilder errorMessage = new StringBuilder();
//            if (isEmailDuplicate) {
//                errorMessage.append("중복되는 Email이 존재합니다.");
//            }
//            if (isNicknameDuplicate) {
//                errorMessage.append("중복되는 별명이 존재합니다.");
//            }
//            return ResponseEntity.badRequest().body(errorMessage.toString());
//        }
        registerAndLoginService.register2(registerRequest);
        return ResponseEntity.ok().body("회원가입을 성공하였습니다.");
    }
}