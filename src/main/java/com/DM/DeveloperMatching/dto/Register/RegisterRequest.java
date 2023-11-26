package com.DM.DeveloperMatching.dto.Register;

import com.DM.DeveloperMatching.domain.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RegisterRequest {
    @NotBlank(message = "사용자 이름을 반드시 입력해주세요.")
    private String userName;

    @NotBlank(message = "별명을 반드시 입력해주세요.")
    private String nickName;

    @NotBlank(message = "이메일을 반드시 입력해주세요.")
    private String email; //회원 아이디 역할

    @NotBlank(message = "비밀번호를 반드시 입력해주세요.")
    private String password;

//    private String passwordCheck;

    //비밀번호 암호화 X
    public User toEntity() {
        return User.builder()
                .userName(this.userName)
                .nickName(this.nickName)
                .email(this.email)
                .password(this.password)
                .build();
    }

    //비밀번호 암호화 O
    public User toEntity(String encodedPassword) {
        return User.builder()
                .userName(this.userName)
                .nickName(this.nickName)
                .email(this.email)
                .password(encodedPassword)
                .build();
    }
}
