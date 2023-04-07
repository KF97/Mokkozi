package com.b303.mokkozi.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class JwtFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    public static final String AUTHORIZATION_HEADER = "Authorization";

    private TokenProvider tokenProvider;

    public JwtFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    // 토큰의 인증정보를 SecurityContext에 저장하는 역할을 수행한다.
    // SecurityContext는 SecurityContextHolder 내에 포함되어 있는 객체이며, 인증 정보를 포함하고 있다.

    // HttpServletRequest, HttpServletResponse객체에 대해 Filter를 수행한다.
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        logger.info("JwtFilter.doFilter 36 : JwtFilter 수행");
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String jwt = resolveToken(httpServletRequest);
        logger.info("JwtFilter.doFilter 39 : Token String : {}", jwt);
        String requestURI = httpServletRequest.getRequestURI();

        // 토큰 검증하고 SecurityCOntextHolder에 Authentication 객체 넣기
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            logger.info("JwtFilter.doFilter 44 : 토큰 유효함");
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            logger.info("JwtFilter.doFilter 46 : getAuthenticaion 수행 완료. {}", authentication);
            logger.info("JwtFilter.doFilter 47 : 생성한 authentication 객체를 SecurityContextHolder에 저장합니다.");
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("JwtFilter.doFilter 49 : Security Context에 '{}' 인증 정보를 저장했습니다. uri: {}", authentication.getName(), requestURI);
        } else {
            logger.debug("JwtFilter.doFilter 51 : 유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
        }
        // 필터 연쇄 적용.
        filterChain.doFilter(servletRequest, servletResponse);
    }
    // request Header(즉, 클라이언트의 axios 요청)에서 토큰 정보를 꺼내오기 위한 메소드
    private String resolveToken(HttpServletRequest request) {
        // Header에 있는 Authorization 정보를 꺼내온다.
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        // 토큰 텍스트가 존재하고, Bearer을 포함하고 있다면
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}