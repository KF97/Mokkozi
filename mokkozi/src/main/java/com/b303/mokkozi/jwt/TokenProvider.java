package com.b303.mokkozi.jwt;

import com.b303.mokkozi.entity.User;
import com.b303.mokkozi.user.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

// 토큰을 생성하고, 토큰의 유효성을 검증하는 메소드가 포함되어 있다.

// TokenProvider 클래스를 Bean 객체로 등록하기 위해 @Component 어노테이션을 사용
@Component
// InitializingBean : 새롭게 Bean객체를 생성하고 해당 Bean 객체의 property를 초기화한다.
public class TokenProvider implements InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    private final long tokenValidityInMilliseconds;

    private static String secret;
    private static String AUTHORITIES_KEY = "role";

    private Key key;

    @Autowired
    UserService userService;

    // Constructor. 토큰의 secret key와 만료기한 값을 불러와서, TokenProvider의 클래스 변수에 저장한다.
    public TokenProvider(
            // application.properties에서 설정한 Base64 기반 secret key를 먼저 불러온다.
            @Value("${jwt.secret}") String secret,
            // application.properties에서 설정한 토큰 만료기한값을 불러온다.
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds) {
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
    }

    @Override
    // 주로 Bean 객체의 property가 모두 올바르게 설정되었는지를 검사하는 용도로 주로 사용된다.
    // Keys : secret key와 key쌍들을 암호화하여 생성하기 위한 유틸리티 클래스.
    public void afterPropertiesSet() throws Exception {
        // Base64로 인코딩한 문자열을 다시 디코드.
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);    // 새로운 key를 생성한다.
    }

    /* stream : Java8부터 추가된 기능으로, 기존에 자바 컬렉션이나 배열의 원소를 가공할 때, for문, foreach문으로 원소 하나씩 골라내서
    가공했다면, Stream을 이용하여 람다함수 형식으로 간결하게 개별 원소에 대한 처리가 가능해졌다.
    stream().map()은 요소들을 특정조건에 해당하는 값으로 변환해 준다. ex) 대,소문자 변형 작업
    */
    /**
     * JWT 토큰을 이용하여, body에 담겨 있는 payload를 확인한다.
     * @param token
     * @return Authentication
     */
    public Authentication getAuthentication(String token) {
        logger.info("TokenProvider.getAuthentication() 69 : 함수 시작");
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // 토큰에서 사용자 이메일, 권한 정보(role) 불러오기
        logger.info("TokenProvider.getAuthentication() 84 : 토큰에 들어있는 이메일 정보 : {}", claims.get("email", String.class));
        logger.info("TokenProvider.getAuthentication() 85 : 토큰에 들어있는 권한 정보 : {}", claims.get("role", String.class));

        // 유저 객체 이메일 통해 찾기
        User user = userService.findByEmail(claims.get("email", String.class)).get();

        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        // 사용자 role에 따라 권한 다르게
        if (claims.get("role").equals("user")) {
            customUserDetails.roles.add(new SimpleGrantedAuthority("user"));
        } else {
            customUserDetails.roles.add(new SimpleGrantedAuthority("admin"));
        }

        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        result.setDetails(user);

        return result;
    }

    public String createToken(Authentication authentication, String authorities) {
        // 사용자의 권한 정보를 불러온다.
        // 아래 코드는 권한을 자동으로 생성해주는 것.
//        String authorities = authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(","));

        // 토큰의 만료기한을 설정한다.
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        // 토큰을 생성하여 return한다.
        return Jwts.builder()
                // 아래의 내용은 registered claim을 설정하는 과정이다.
                // claim은? JWT내 payload에 들어가는 데이터의 형태이다. 하나의 claim은 key-value 쌍으로 이루어져 있다.
                // claim의 종류로는 registered claim, public claim, private claim이 존재한다.
                .claim("email", authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)    // 토큰의 권한 정보
                .setIssuer("mokkozi.com")               // 해당 토큰을 발급한 곳의 정보
                .setIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())) // 해당 토큰의 생성일
                .signWith(key, SignatureAlgorithm.HS512)// 서명
                .setExpiration(validity)                // 해당 토큰의 만료일
                .compact();                             // 이를 조합하여 JWT를 만들고, 해당하는 String을 반환한다.
    }


    // 토큰을 파라미터로 받아 파싱한 후, 문제가 발생하면 return false, 문제가 없으면 return true
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            logger.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }


}
