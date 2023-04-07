package com.b303.mokkozi.gallery;

import com.b303.mokkozi.common.response.BaseResponseBody;
import com.b303.mokkozi.config.S3Uploader;
import com.b303.mokkozi.entity.Gallery;
import com.b303.mokkozi.entity.User;
import com.b303.mokkozi.gallery.dto.GalleriesDto;
import com.b303.mokkozi.gallery.dto.GalleryDto;
import com.b303.mokkozi.gallery.dto.GalleryListDto;
import com.b303.mokkozi.gallery.request.ImageWrapper;
import com.b303.mokkozi.gallery.request.ProfileWrapper;
import com.b303.mokkozi.gallery.request.GalleryVO;
import com.b303.mokkozi.user.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/meet/gallery")
public class GalleryController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private S3Uploader s3Uploader;

    @Autowired
    private UserService userService;

    @Autowired
    GalleryService galleryService;

    /**
     * 회원가입시 첨부한 이미지를 업로드한다. ( 로직을 잘못 짰음 ... )
     * @Param file, fileInfo
     * @Return Response Message
     */
    @PostMapping("/images")
    @ApiOperation(value = "이미지 파일 업로드", notes = "업로드한 이미지를 S3 서버에 올립니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "이미지 업로드 성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 500, message = "파일 업로드 실패")})
    @Transactional
    public ResponseEntity<? extends BaseResponseBody> upload(
            @ModelAttribute ImageWrapper model) {

        logger.info("GalleryController.upload 54 : 이미지 파일 업로드합니다.");

        // 데이터 확인
        logger.info("GalleryController.upload 57 : 파일 리스트: {}", model.getFiles());
        logger.info("GalleryController.upload 57 : 분류 : {}", model.getSort());
        logger.info("GalleryController.upload 57 : 사용자 이메일 : {}", model.getEmail());
        logger.info("GalleryController.upload 57 : 게시글 ID : {}", model.getBoardId());

        for (MultipartFile file:model.getFiles()) {
            // 파일 업로드 시, Exception 처리
            try {
                String file_path = s3Uploader.upload(file, "myPage");

                GalleryVO galleryVO = new GalleryVO();
                galleryVO.setFilePath(file_path);
                galleryVO.setSort(model.getSort());
                galleryVO.setTitle(file.getOriginalFilename());

                // 게시글 이미지 업로드인 경우
                if (model.getEmail().equals("")) {
                    logger.info("GalleryController.upload 67 : 게시글 이미지 저장 결과 : {}",
                        galleryService.galleryCreate(galleryVO, model.getBoardId()));
                }
                // 프로필 이미지 업로드인 경우
                else {
                    logger.info("GalleryController.upload 73 : 프로필 이미지 저장 결과 : {}",
                        galleryService.galleryCreate(galleryVO, model.getEmail()));
                }
            // S3 파일 업로드가 실패하는 경우, Exception이 발생한다.
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body(BaseResponseBody.of(500, "파일 업로드 실패하였습니다.."));
            }
        }
        // for문 종료 후. 모든 파일이 에러 없이 업로드 됐다면 이곳으로 넘어온다.
        return ResponseEntity.status(200).body(BaseResponseBody.of(200, "파일 S3 업로드 및 DB 저장 완료"));
    }


    /**
     * 사용자의 대표 프로필을 S3에 업로드하고, DB에 저장한다.
     * @Params file, userEmail
     * @Return Response Message
     */
    @PostMapping("/myProfile")
    @Transactional
    @ApiOperation(value = "사용자 대표 프로필 업로드", notes = "회원가입시 첨부한 대표 프로필을 S3에 올리고, User 객체의 profile 을 수정")
    @ApiResponses({
            @ApiResponse(code = 200, message = "대표 프로필 업로드 성공"),
            @ApiResponse(code = 500, message = "대표 프로필 업로드 실패"),
            @ApiResponse(code = 401, message = "권한 인증 실패")})
    public ResponseEntity<? extends BaseResponseBody> uploadMyProfile(
            @ModelAttribute ProfileWrapper model) {

        logger.info("GalleryController.uploadMyProfile 87 : 파일 용량 정보 : {}", model.getFile().getSize());
        logger.info("GalleryController.uploadMyProfile 88 : 파일 컨텐츠 타입 : {}", model.getFile().getContentType());
        logger.info("GalleryController.uploadMyProfile 89 : 파일 원래 이름 : {}", model.getFile().getOriginalFilename());
        logger.info("GalleryController.uploadMyProfile 91 : getName : {}", model.getFile().getName());
        logger.info("GalleryController.uploadMyProfile 92 : 회원가입한 사용자 이메일 정보 : {}", model.getEmail());

        String file_path = "";
        // 1. S3에 업로드하기
        try {
            logger.info("GalleryController.uploadMyProfile 100 : S3에 파일 업로드합니다.");
            // 2번째 인자는 폴더이름이다.
            file_path = s3Uploader.upload(model.getFile(), "profile");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(BaseResponseBody.of(500, "대표 프로필 업로드 실패"));
        }

        if (!file_path.equals("")) {
            // User 객체의 profile 컬럼 속성을 업데이트한다.
            logger.info("GalleryController.uploadMyProfile 109 : User profile 속성 수정합니다.");
            User user = userService.findByEmail(model.getEmail()).get();
            user.setProfile(file_path);
            userService.userUpdate(user);
        }
        else {
            return ResponseEntity.status(500).body(BaseResponseBody.of(500, "대표 프로필 업로드 실패. file_path Null"));
        }

        return ResponseEntity.status(200).body(BaseResponseBody.of(200, "대표 프로필 업로드 성공!"));
    }

    @PostMapping("/findByEmail")
    @ApiOperation(value = "이메일 통해 이미지를 불러온다.", notes = "사용자 이메일을 통해 프로필 이미지를 불러온다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "이미지 불러오기 성공"),
            @ApiResponse(code = 500, message = "이미지 불러오기 실패"),
            @ApiResponse(code = 401, message = "권한 인증 실패")})
    public ResponseEntity<? extends BaseResponseBody> findByEmail(Authentication authentication) {
        // Authentication 통해 UserEmail을 받는다.
        User user = (User) authentication.getDetails();

        List<GalleryDto> result = galleryService.getGalleryListByUser(user);
        return ResponseEntity.status(200).body(GalleriesDto.of(200, "갤러리 목록 불러오기 성공", result));
    }

}



