package com.b303.mokkozi.jwt;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// TokenProvider, JwtFilter를 SecurityConfig에 적용할 때 사용할 JwtSecurityConfig 클래스
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private TokenProvider tokenProvider;

    // Constructor
    public JwtSecurityConfig(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    // JwtFilter를 Security 로직에 적용하는 역할을 수행합니다.
    public void configure(HttpSecurity http) {
        // TokenProvider를 주입받아서 JwtFilter를 Security로직에 필터를 등록한다.
        JwtFilter customFilter = new JwtFilter(tokenProvider);
        // 지정된 필터(두번째 param) 앞에 커스텀 필터를 추가한다.
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
