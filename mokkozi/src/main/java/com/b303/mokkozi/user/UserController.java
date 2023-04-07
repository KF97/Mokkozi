package com.b303.mokkozi.user;

import com.b303.mokkozi.common.response.BaseResponseBody;
import com.b303.mokkozi.entity.User;
import com.b303.mokkozi.jwt.CustomUserDetails;
import com.b303.mokkozi.jwt.TokenProvider;
import com.b303.mokkozi.user.dto.*;
import com.b303.mokkozi.user.request.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/meet/user")
public class UserController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    // Constructor. Bean이 생성될 때 클래스 변수 Initializing
    public UserController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @ApiOperation(value = "로그인", notes = "ID, PW 이용 로그인")
    @ApiResponses({@ApiResponse(code = 200, message = "로그인 성공"), @ApiResponse(code = 500, message = "로그인 실패")})
    public ResponseEntity<? extends BaseResponseBody> login(
            @RequestBody @ApiParam(value = "회원의 로그인 정보(아이디와 패스워드)", required = true) CredentialPostReq credentials) {

        // ID, PW가 담긴 토큰 발급
        log.info("UserController.login 65 : 이메일: {}, 비밀번호: {}", credentials.getEmail(), credentials.getPassword());
        log.info("UserController.login 66 : Authenticaion 토큰 생성. 이 과정에서 해당 사용자가 존재하는지, 비밀번호는 맞는지를 확인합니다.");
        // 아이디 또는 비밀번호가 맞지 않는 경우 에러 메시지를 출력한다. (log 형태)
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword());

        // 이 토큰을 이용해서 Authenticaion 객체 생성
        log.info("UserController.login 69 : Authenticaion 객체 생성");


        // Dummy가 담긴 User 또는 실제 User 객체를 반환한다.
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // Spring Security에 해당 authenticaion 객체를 저장한다.
        log.info("UserController.login 79 : SecurityContextHolder에 authentication 객체를 저장합니다.");
        SecurityContextHolder.getContext().setAuthentication(authentication);


        CustomUserDetails result = (CustomUserDetails) authentication.getPrincipal();
        log.info("UserController.login 85 : authenticaion 정보 : {}", result.getUser());

        return ResponseEntity.ok(TokenDto
                .of(200, "로그인에 성공하였습니다. 토큰 발급 완료",
                        tokenProvider.createToken(authentication, "user"),
                        result.getUser().getNickname(),
                        result.getUser().getProfile(),
                        result.getUser().getEmail(),
                        result.getUser().getRole()
                ));
    }


    @GetMapping("/getuser")
    @ApiOperation(value = "유저정보", notes = "이메일로 다른 유저 정보 가져오기")
    @ApiResponses({@ApiResponse(code = 200, message = "유저정보 가져오기 성공"), @ApiResponse(code = 500, message = "유저정보 가져오기 실패")})
    public ResponseEntity<? extends BaseResponseBody> getuser(@RequestParam @ApiParam(value = "다른 사용자의 이메일", required = true) String toUserEmail,
                                           @ApiIgnore Authentication authentication) {

    	User getuser = userService.findByEmail(toUserEmail).get();

    	// 관심사도 불러온다.
        List<UserInterestDto> userInterest = userService.getUserInterest(getuser);

    	return ResponseEntity.status(200).body(UserDto.of(200, "유저 정보 불러오기 성공", getuser, userInterest));
    }

    @GetMapping("/getUserByNickname")
    @ApiOperation(value = "유저정보", notes = "닉네임으로 다른 유저 정보 가져오기")
    @ApiResponses({@ApiResponse(code = 200, message = "유저정보 가져오기 성공"), @ApiResponse(code = 500, message = "유저정보 가져오기 실패")})
    public ResponseEntity<? extends BaseResponseBody> getuserByNickname(@RequestParam @ApiParam(value = "다른 사용자의 이메일", required = true) String nickName,
                        @ApiIgnore Authentication authentication) {

        Optional<User> result = userService.findByNickname(nickName);
        if (result.isPresent()) {
            return ResponseEntity.status(200).body(UserDto.of(200, "닉네임으로 유저 객체 받아오기 성공", result.get()));
        }
        else {
            return ResponseEntity.status(200).body(BaseResponseBody.of(404, "유저를 찾을 수 없습니다."));
        }

    }


    @PostMapping("/join")
    @ApiOperation(value = "회원가입", notes = "회원 가입에 필요한 정보를 입력하고 회원가입한다.")
    @ApiResponses({@ApiResponse(code = 200, message = "회원가입 성공"), @ApiResponse(code = 500, message = "회원가입 실패")})
    public ResponseEntity<? extends BaseResponseBody> join(@RequestBody @ApiParam(value="회원가입 시 필요한 정보") JoinInfoPostReq joinInfo) {
        log.info("회원가입 시 받아온 정보는 {}", joinInfo.toString());

        // 사용자가 입력한 암호를 한번 인코딩해야 한다.
        joinInfo.setPassword(passwordEncoder.encode(joinInfo.getPassword()));

        try {
            User result = userService.join(joinInfo);
            return ResponseEntity.status(200).body(BaseResponseBody.of(200, "회원가입 성공."));
        }
        catch (Exception e) {
            return ResponseEntity.status(401).body(BaseResponseBody.of(401, "회원가입 실패."));
        }
    }
    
    // 유저 팔로우
    @PostMapping("/follow")
    @ApiOperation(value = "팔로우", notes = "다른 사용자를 팔로우할 수 있다.")
    @ApiResponses({@ApiResponse(code = 200, message = "팔로우 성공"), @ApiResponse(code = 500, message = "팔로우 실패")})
    public ResponseEntity<? extends BaseResponseBody>  follow(@RequestParam @ApiParam(value = "다른 사용자의 이메일", required = true) String toUserEmail
            ,@ApiIgnore Authentication authentication
    ) {
        try{
            User user = (User) authentication.getDetails();
            userService.createFollow(user,toUserEmail);
            return ResponseEntity.ok(BaseResponseBody.of(200, "팔로우 성공"));
        } catch (AuthenticationException | NullPointerException e) {
            return ResponseEntity.status(401).body(BaseResponseBody.of(401, "로그인 인증 실패"));
        }catch (NoSuchElementException e){
            e.printStackTrace();
            return ResponseEntity.status(404).body(BaseResponseBody.of(404, "존재하지 않는 사용자입니다."));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(403).body(BaseResponseBody.of(403, "잘못된 요청입니다."));
        }
    }

    // 유저 팔로우 취소
    @DeleteMapping("/unfollow")
    @ApiOperation(value = "팔로우 취소", notes = "다른 사용자를 팔로우 취소할 수 있다.")
    @ApiResponses({@ApiResponse(code = 200, message = "언팔로우 성공"), @ApiResponse(code = 500, message = "언팔로우 실패")})
    public ResponseEntity<? extends BaseResponseBody>  deleteFollow(@RequestParam @ApiParam(value = "팔로우 ID", required = true) Long followId
            ,@ApiIgnore Authentication authentication
    ) {

        try{
            User user = (User) authentication.getDetails();
            userService.deleteFollow(followId);
            return ResponseEntity.ok(BaseResponseBody.of(200, "언팔로우 성공"));
        } catch (AuthenticationException | NullPointerException e) {
            return ResponseEntity.status(401).body(BaseResponseBody.of(401, "로그인 인증 실패"));
        }catch (NoSuchElementException e){
            e.printStackTrace();
            return ResponseEntity.status(404).body(BaseResponseBody.of(404, "존재하지 않는 정보입니다."));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(403).body(BaseResponseBody.of(403, "잘못된 요청입니다."));
        }
    }

    //나의 팔로워 목록 확인
    @Transactional
    @GetMapping("/followers")
    @ApiOperation(value = "팔로워 목록 ", notes = "팔로워 정보를 리스트로 반환")
    @ApiResponses({@ApiResponse(code = 200, message = "팔로워 목록 조회 성공"), @ApiResponse(code = 500, message = "팔로워 목록 조회 실패")})
    public ResponseEntity<? extends BaseResponseBody> getFollowers(
            @ApiIgnore Authentication authentication
    ){
        try{
            User user = (User) authentication.getDetails();
            List<UserFollowDto> followers = userService.getFollowers(user);
            return ResponseEntity.ok(UserFollowListDto.of(200, "팔로워 목록 조회 성공",followers));
        } catch (AuthenticationException | NullPointerException e) {
            return ResponseEntity.status(401).body(BaseResponseBody.of(401, "로그인 인증 실패"));
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(403).body(BaseResponseBody.of(403, "잘못된 요청입니다."));
        }
    }

    //나의 팔로잉 목록 확인
    @Transactional
    @GetMapping("/following")
    @ApiOperation(value = "팔로잉 목록 ", notes = "팔로잉 정보를 리스트로 반환")
    @ApiResponses({@ApiResponse(code = 200, message = "팔로워 목록 조회 성공"), @ApiResponse(code = 500, message = "팔로잉 목록 조회 실패")})
    public ResponseEntity<? extends BaseResponseBody> getFollowing(
            @ApiIgnore Authentication authentication
    ){
        try{
            User user = (User) authentication.getDetails();
            List<UserFollowDto> following = userService.getFollowing(user);
            return ResponseEntity.ok(UserFollowListDto.of(200, "팔로워 목록 조회 성공",following));
        } catch (AuthenticationException | NullPointerException e) {
            return ResponseEntity.status(401).body(BaseResponseBody.of(401, "로그인 인증 실패"));
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(403).body(BaseResponseBody.of(403, "잘못된 요청입니다."));
        }
    }


    //랜덤 추천
    @GetMapping("/recommend/random")
    @ApiOperation(value = "랜덤 추천 목록 ", notes = "로그인한 회원을 제외한 랜덤 추천 목록을 반환")
    @ApiResponses({@ApiResponse(code = 200, message = "회원 랜덤 조회 성공"), @ApiResponse(code = 500, message = "회원 랜덤 조회 실패")})
    public ResponseEntity<? extends BaseResponseBody> recommendRandom(
            @ApiIgnore Authentication authentication
    ){
        try{
            User user = (User) authentication.getDetails();
            List<User> random = userService.getRandomUser(user);
            return ResponseEntity.ok(UserRecommendDto.of(200, "회원 랜덤 조회 성공",random));
        } catch (AuthenticationException | NullPointerException e) {
            return ResponseEntity.status(401).body(BaseResponseBody.of(401, "로그인 인증 실패"));
        } catch (NoSuchElementException e){
            e.printStackTrace();
            return ResponseEntity.status(404).body(BaseResponseBody.of(404, "존재하지 않는 정보입니다."));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(403).body(BaseResponseBody.of(403, "잘못된 요청입니다."));
        }
    }

    //랜덤 추천 비회원ver.
    @GetMapping("/recommend/guest_random")
    @ApiOperation(value = "랜덤 추천 목록 ", notes = "로그인한 회원을 제외한 랜덤 추천 목록을 반환")
    @ApiResponses({@ApiResponse(code = 200, message = "회원 랜덤 조회 성공"), @ApiResponse(code = 500, message = "회원 랜덤 조회 실패")})
    public ResponseEntity<? extends BaseResponseBody> recommendRandomNotLogin(){
        log.info("회원 랜덤 조회 시작합니다.");

        List<User> random = userService.getRandomUserNotLogin();
        log.info("랜덤으로 가져온 목록은... {}", random);
        return ResponseEntity.status(200).body(UserRecommendDto.of(200, "회원 랜덤 조회 성공", random));
    }

    //위치 기반 추천
    @GetMapping("/recommend/location")
    @ApiOperation(value = "위치 기반 추천 목록 ", notes = "로그인한 회원을 제외한 위치 기반 추천 목록을 반환")
    @ApiResponses({@ApiResponse(code = 200, message = "회원 위치 기반 조회 성공"), @ApiResponse(code = 500, message = "회원 위치 기반 조회 실패")})
    public ResponseEntity<? extends BaseResponseBody> recommendLocation(
            @ApiIgnore Authentication authentication
    ){
        try{
            User user = (User) authentication.getDetails();
            List<User> location = userService.getLocationUser(user);
            return ResponseEntity.ok(UserRecommendDto.of(200, "회원 위치 기반 조회 성공",location));
        } catch (AuthenticationException | NullPointerException e) {
            return ResponseEntity.status(401).body(BaseResponseBody.of(401, "로그인 인증 실패"));
        } catch (NoSuchElementException e){
            e.printStackTrace();
            return ResponseEntity.status(404).body(BaseResponseBody.of(404, "존재하지 않는 정보입니다."));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(403).body(BaseResponseBody.of(403, "잘못된 요청입니다."));
        }
    }


    @PostMapping("/validEmail")
    @ApiOperation(value = "이메일 검증", notes = "입력한 이메일이 이미 존재하는지 검증합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "이미 아이디가 있습니다."),
            @ApiResponse(code = 404, message = "아이디 사용 가능"),
            @ApiResponse(code = 500, message = "에러 발생")})
    public ResponseEntity<? extends BaseResponseBody> validEmail(@RequestBody EmailPostReq email) {
        log.info("UseController.validEmail 235 : 이메일 검증합니다. {} ", email.getEmail());

        Optional<User> user = userService.findByEmail(email.getEmail());
        if (user.isPresent()) {
            log.info("UseController.validEmail 245 : 사용자가 존재합니다.");
            return ResponseEntity.status(200).body(BaseResponseBody.of(200, "이미 사용자가 존재합니다."));
        }
        else {
            log.info("UseController.validEmail 249 : 사용 가능한 아이디입니다.");
            return ResponseEntity.status(200).body(BaseResponseBody.of(404, "사용 가능한 아이디입니다."));
        }
    }

    @PostMapping("/validNickname")
    @ApiOperation(value = "닉네임 검증", notes = "입력한 닉네임이 이미 존재하는지 검증합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "이미 닉네임이 있습니다."),
            @ApiResponse(code = 404, message = "닉네임 사용 가능"),
            @ApiResponse(code = 500, message = "에러 발생")})
    public ResponseEntity<? extends BaseResponseBody> validNickname(@RequestBody NicknamePostReq nickname) {
        log.info("UseController.validNickname 362 : 닉네임 검증합니다 : {} ", nickname.getNickname());

        Optional<User> user = userService.findByNickname(nickname.getNickname());
        if (user.isPresent()) {
            log.info("UseController.validNickname 266 : 사용자가 존재합니다.");
            return ResponseEntity.status(200).body(BaseResponseBody.of(200, "이미 사용자가 존재합니다."));
        } else {
            log.info("UseController.validEmail 249 : 사용 가능한 닉네임입니다.");
            return ResponseEntity.status(200).body(BaseResponseBody.of(404, "사용 가능한 닉네임입니다."));
        }
    }

    // 관리자용 사용자 목록 조회
    @GetMapping("/list")
    @ApiOperation(value = "사용자 목록 조회", notes = "관리자는 사용자 목록을 조회할 수 있다.")
    @ApiResponses({@ApiResponse(code = 200, message = "사용자 목록 성공"), @ApiResponse(code = 400, message = "실패"),
            @ApiResponse(code = 401, message = "로그인 인증 실패"), @ApiResponse(code = 403, message = "잘못된 요청")})
    public ResponseEntity<? extends BaseResponseBody>  getUserList(@RequestParam int page
            ,@ApiIgnore Authentication authentication
    ) {
        try{
            User user = (User) authentication.getDetails();
            Page<User> list = userService.getUserList(user,page);
            return ResponseEntity.ok(UserListDto.of(200, "사용자 목록 조회 성공",list));
        } catch (AuthenticationException | NullPointerException e) {
            return ResponseEntity.status(401).body(BaseResponseBody.of(401, "로그인 인증 실패"));
        }catch (NoSuchElementException e){
            e.printStackTrace();
            return ResponseEntity.status(404).body(BaseResponseBody.of(404, "사용자가 존재하지 않습니다."));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(403).body(BaseResponseBody.of(403, "잘못된 요청입니다."));
        }
    }

    @PatchMapping("/active")
    @ApiOperation(value = "사용자 활동 변경", notes = "관리자는 사용자 활동 상태를 변경할 수 있다.")
    @ApiResponses({@ApiResponse(code = 200, message = "사용자 활동 변경 성공"), @ApiResponse(code = 400, message = "실패"),
            @ApiResponse(code = 401, message = "로그인 인증 실패"), @ApiResponse(code = 403, message = "잘못된 요청")})
    public ResponseEntity<? extends BaseResponseBody>  verifyUser(@RequestBody UserActivePatchReq vupr
        , @ApiIgnore Authentication authentication
    ) {
            try{
                User user = (User) authentication.getDetails();
                if (!user.getRole().equals("관리자")) new Exception();
                userService.modifyUserActive(vupr);
                return ResponseEntity.ok(BaseResponseBody.of(200, "사용자 활동 변경 성공"));
            } catch (AuthenticationException | NullPointerException e) {
                return ResponseEntity.status(401).body(BaseResponseBody.of(401, "로그인 인증 실패"));
            }catch (NoSuchElementException e){
                e.printStackTrace();
                return ResponseEntity.status(404).body(BaseResponseBody.of(404, "사용자가 존재하지 않습니다."));
            }catch (Exception e){
                e.printStackTrace();
                return ResponseEntity.status(403).body(BaseResponseBody.of(403, "잘못된 요청입니다."));
            }
    }




}
