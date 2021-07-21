package me.youjin.tutorial.controller;

import me.youjin.tutorial.dto.LoginDto;
import me.youjin.tutorial.dto.TokenDto;
import me.youjin.tutorial.jwt.JwtFilter;
import me.youjin.tutorial.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto) { // loginDto로 post 요청으로 들어온 속성을 받고
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()); // authenticationToken을 생성한다

        // 위에서 만든 authenticationToken을 받아 authenticate가 실행이 될 때 -> loadByUsername method가 실행된다
        // 그 결과값을 가지고 authentication 객체를 생성한다
        // 그 객체를 securitycontextholder에 저장한다
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // authentication 객체를 createToken method를 통해 jwt를 생성한다
        String jwt = tokenProvider.createToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt); // response header에 jwt를 넣어준다

        return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK); // TokenDto를 이용해 body에도 넣어서 리턴
    }
}
