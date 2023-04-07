package com.b303.mokkozi.config;

import com.b303.mokkozi.jwt.JwtAccessDeniedHandler;
import com.b303.mokkozi.jwt.JwtAuthenticationEntryPoint;
import com.b303.mokkozi.jwt.JwtSecurityConfig;
import com.b303.mokkozi.jwt.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // 나중에 추가할 @PreAuthorize 어노테이션을 메소드 단위로 추가하기 위해 적용
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        logger.info("SecurityConfig.configure 43 : 함수 시작합니다.");
        httpSecurity
                // CORS에 대한 preflight 요청 허용
                .cors()
                .and()
                // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
                .csrf().disable()

                // 401 UnAuthorized와 403 Forbidden Error에 대한 예외 처리를 우리가 만든 클래스를 통해 처리할 수 있도록 등록한다.
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)      // 401 Error
                .accessDeniedHandler(jwtAccessDeniedHandler)                // 403 Error

                // 이제부터 수행하는 경우는 HttpServletRequest에 Authentication Token이 포함된 경우이다.
                // 단, 로그인 API, 회원가입 API의 경우 토큰이 없는 상태에서 요청이 들어오기 때문에, 모두 permitAll 처리 한다.
                .and()
                .authorizeRequests()                
                .antMatchers("/api/meet/user/recommend/guest_random").permitAll()
                .antMatchers("/api/meet/user/login").permitAll()
                .antMatchers("/api/meet/user/join").permitAll()
                .antMatchers("/api/meet/user/validEmail").permitAll()
                .antMatchers("/api/meet/user/validNickname").permitAll()
                .antMatchers("/api/meet/gallery/myProfile").permitAll()
                .antMatchers("/api/meet/gallery/images").permitAll()
                // Swagger와 관련된 URL은 모두 예외 처리.
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**", "/swagger/**").permitAll()
                .anyRequest().authenticated()
                // 마지막으로, JwtFilter를 addFilterBefore로 등록했던 JwtSecurityConfig 클래스도 적용해준다.
                .and()
                .apply(new JwtSecurityConfig(tokenProvider));
    }
}
