package me.youjin.tutorial.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class SecurityUtil {
    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    private SecurityUtil() {
    }

    // security context에서 authentication 객체를 꺼내와서 authentication 객체를 통해 username을 반환해주는 util성 method
    // doFilter에서 request 가 들어오는 시점에 Security Context에 Authentication 객체가 저장된다
    public static Optional<String> getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            logger.debug("Security Context에 인증 정보가 없습니다.");
            return Optional.empty();
        }

        String username = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            username = springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        }

        return Optional.ofNullable(username);
    }
}
