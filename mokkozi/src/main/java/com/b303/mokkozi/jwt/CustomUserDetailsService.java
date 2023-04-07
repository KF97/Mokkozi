package com.b303.mokkozi.jwt;

import com.b303.mokkozi.entity.User;
import com.b303.mokkozi.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        logger.info("CustomUserDetailService.loadUserbyUsername 24 : 사용자 이메일을 활용하여 사용자를 찾습니다.");
        Optional<User> user = userService.findByEmail(userEmail);

        if (user.isPresent()) {
            logger.info("CustomUserDetailService.loadUserbyUsername 28 : 유저 찾기 성공!");
            CustomUserDetails customUserDetails = new CustomUserDetails(user.get());
            // 비밀번호 암호화.
            logger.info("CustomUserDetailService.loadUserbyUsername 36 : 사용자 암호는 {}", user.get().getPassword());

            // 권한 설정도 함께 해준다. 사용자냐 관리자냐에 따라 나뉜다.
            if (user.get().getRole().equals("user")) {
                customUserDetails.roles.add(new SimpleGrantedAuthority("user"));
            } else {
                customUserDetails.roles.add(new SimpleGrantedAuthority("admin"));
            }
            return customUserDetails;
        }
        else {
            logger.info("CustomUserDetailService.loadUserbyUsername 47 : 입력한 아이디, PW를 갖는 회원이 존재하지 않습니다.");
            throw new UsernameNotFoundException(userEmail + "is Not Found");
        }
    }
}
