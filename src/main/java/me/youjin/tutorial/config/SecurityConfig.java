package me.youjin.tutorial.config;

import me.youjin.tutorial.jwt.JwtAccessDeniedHandler;
import me.youjin.tutorial.jwt.JwtAuthenticationEntryPoint;
import me.youjin.tutorial.jwt.JwtSecurityConfig;
import me.youjin.tutorial.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// 추가적인 설정을 위해 WebSecurityConfigurer를 implements 하거나 WebSecurityConfigurerAdapter를 extends할 수 있다
@EnableWebSecurity // 기본적인 web 보안을 활성화 하겠다
@EnableGlobalMethodSecurity(prePostEnabled = true) // @PreAuthorize annotation을 메소드 단위로 추가하기 위해 적용
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // jwt package에 만든 5가지의 class를 적용
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(
            TokenProvider tokenProvider,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // h2, favicon 관련 요청 Spring Security 로직을 수행하지 않도록 무시한다
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers(
                        "/h2-console/**",
                        "/favicon.ico"
                );
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // token 방식을 사용하므로 csrf설정을 disable한다

                .exceptionHandling() // exception을 handling할 때
                .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 우리가 만들었던 class 추가해줌
                .accessDeniedHandler(jwtAccessDeniedHandler)

                .and() // h2 console을 위한 설정
                .headers()
                .frameOptions()
                .sameOrigin()

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // session을 사용하지 않기때문에 stateless로 설정

                .and()
                .authorizeRequests() // http servlet request를 사용하는 요청들에 대한 접근제한 설정
                .antMatchers("/api/hello").permitAll() // api/hello에 대한 것은 인증없이 접근을 허용
                .antMatchers("/api/authenticate").permitAll()
                .antMatchers("/api/signup").permitAll()
                .anyRequest().authenticated() // 나머지는 인증을 받아야 한다

                .and()
                .apply(new JwtSecurityConfig(tokenProvider));
    }
}
