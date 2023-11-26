package com.DM.DeveloperMatching.config.jwt;

import com.DM.DeveloperMatching.domain.User;
import com.DM.DeveloperMatching.service.RegisterAndLoginService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter { //OncePerRequestFilter : 매번 들어갈 때 마다 체크 해주는 필터

    private final RegisterAndLoginService registerAndLoginService;
    //    @Value("${jwt.secret-key}")
//    private static final String secretKey;
    private final String secretKey;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        //Header의 Authorization 값이 비어 있다면 -> Jwt Token을 전송하지 않는다 -> 로그인하지 못함
        if (authorizationHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        //Header의 Authorization 값이 'Bearer'로 시작하지 않는다면 잘못된 토큰이다
        if (!authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        //System.out.println("authorizationHeader = " + authorizationHeader);
        //전송받은 Token에서 'Bearer' 뒷부분(Jwt Token)만 추출
        String jwtToken = authorizationHeader.split(" ")[1];
        //System.out.println("jwtToken = " + jwtToken);


        //전송받은 Jwt Token이 만료되었으면 -> 다음 필터를 진행한다(인증X)
        if (JwtTokenUtils.isExpired(jwtToken, secretKey)) {
            filterChain.doFilter(request, response);
            return;
        }

        //Jwt Token에서 email 추출
        String email = JwtTokenUtils.extractUserEmail(jwtToken, secretKey);

        //Jwt Token에서 추출한 email로 어떤 User인지 찾기
        User findByLoginEmail = registerAndLoginService.getLoginByEmail(email);

        //login한 User정보로 UsernamePasswordAuthenticationToken 발급
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                findByLoginEmail.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // 권한 부여
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}