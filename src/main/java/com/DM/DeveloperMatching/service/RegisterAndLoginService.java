package com.DM.DeveloperMatching.service;

import com.DM.DeveloperMatching.domain.User;
import com.DM.DeveloperMatching.dto.Login.LoginRequest;
import com.DM.DeveloperMatching.dto.Register.RegisterRequest;
import com.DM.DeveloperMatching.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RegisterAndLoginService {
    @Autowired private final UserRepository userRepository;
    @Autowired private final BCryptPasswordEncoder encoder;
    /**
     * login할 때 사용하는 Email 중복 체크
     * 회원가입 기능 구현시 사용
     * 중복되면 return true
     */
    public boolean checkLoginEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 회원가입 할 때 작성하는 userName 중복 체크
     * 회원가입 기능 구현시 사용
     * 중복되면 return true
     */
    public boolean checkUserNameDuplicate(String userName) {
        return userRepository.existsByUserName(userName);
    }

    /**
     * 회원가입 할 때 작성하는 nickName 중복 체크
     * 회원가입 기능 구현시 사용
     * 중복되면 return true
     */
    public boolean checkNickNameDuplicate(String nickName) {
        return userRepository.existsByNickName(nickName);
    }

    /**
     * 회원가입1
     * 화면에서 RegisterRequest(userName,email,password)를 입력 받아 변환 후 저장
     * email, userName 중복체크는 Controller에서 진행예정 -> 에러 메시지 출력해야 해서
     */
    public void register1(RegisterRequest registerRequest) {
        userRepository.save(registerRequest.toEntity());
    }

    /**
     * 회원가입2
     * 화면에서 RegisterRequest(userName,email,password)를 입력 받아 변환 후 저장
     * 회원가입1과는 다르게 비밀번호를 암호화한 후 저장
     * email, userName 중복체크는 Controller에서 진행예정 -> 에러 메시지 출력해야 해서
     */
    public void register2(RegisterRequest registerRequest) {
        userRepository.save(registerRequest.toEntity(encoder.encode(registerRequest.getPassword())));
    }

    /**
     * 로그인 기능
     * 화면에서 LoginRequest(email,password)를 입력 받고 그 email과 password가 일치하면, return User
     * email이 존재하지 않거나 password가 일치하지 않으면 return null
     */
    public User login(LoginRequest loginRequest) {
        Optional<User> findByLoginEmail = userRepository.findByEmail(loginRequest.getEmail());

        //loginEmail과 일치하는 User가 없으면 return null
        if (findByLoginEmail.isEmpty()) {
            return null;
        }

        User user = findByLoginEmail.get();
        System.out.println("user.getEmail() = " + user.getEmail());
        System.out.println("user.getPassword() = " + user.getPassword());
        System.out.println("user.getNickName() = " + user.getNickName());


        // 비밀번호 검증: 입력받은 비밀번호와 암호화된 비밀번호를 비교
        if (!encoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return null;
        }
//        //찾은 User의 password와 입력된 password가 다르면 return null
//        if (!user.getPassword().equals(loginRequest.getPassword())) {
//            return null;
//        }
        return user;
    }

    /**
     * uId를 입력 받아 User를 return 해줌
     * 인증, 인가를 할 때 사용 해야 한다
     * uId가 null이거나(로그인이 안됨), uId로 찾은 User가 없으면 return null
     * uId로 찾은 User가 존재한다면 return User
     */
    public User getLoginByUid(Long uId) {
        if (uId == null) {
            return null;
        }

        Optional<User> findById = userRepository.findById(uId);
        if (findById.isEmpty()) {
            return null;
        }

        return findById.get();
    }

    /**
     * email을 입력받아 User를 return 해줌
     * 인증, 인가를 할 때 사용 해야 한다
     * email이 null이거나(로그인이 안됨), email로 찾은 User가 없으면 return null
     * email로 찾은 User가 존재한다면 return User
     */
    public User getLoginByEmail(String email) {
        if (email == null) {
            return null;
        }

        Optional<User> findByEmail = userRepository.findByEmail(email);
        if (findByEmail.isEmpty()) {
            return null;
        }

        return findByEmail.get();
    }
}