package me.youjin.tutorial.service;

import me.youjin.tutorial.dto.UserDto;
import me.youjin.tutorial.entity.Authority;
import me.youjin.tutorial.entity.User;
import me.youjin.tutorial.repository.UserRepository;
import me.youjin.tutorial.util.SecurityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Collections;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User signup(UserDto userDto) {
        // DB에 저장되어 있는지 확인
        if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
            throw new RuntimeException("이미 가입되어 있는 유저 입니다.");
        }

        // DB에 없다면 권한정보를 만든다 - 회원가입을 통해 만들어진 user는 User 권한을 가진다
       Authority authority = Authority.builder()
                .authorityName("ROLE_USER") // ROLE_USER 이라는 권한을 가진다
                .build();

        // User 정보를 만든다
        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();
        
        // User 정보와 권한정보를 저장한다
        return userRepository.save(user);
    }

    // User, 권한정보를 가져오는 method 2개

    // username을 parameter로 받고 user객체와 권한정보를 갖고 올 수 있는 method
    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities(String username) {
        return userRepository.findOneWithAuthoritiesByUsername(username);
    }

    // 현재 security context에 저장되어있는 username에 해당하는 user정보와 권한정보만 받아갈 수 있다
    @Transactional(readOnly = true)
    public Optional<User> getMyUserWithAuthorities() {
        return SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUsername);
    }
}
