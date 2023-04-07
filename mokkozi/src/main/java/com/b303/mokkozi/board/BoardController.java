package com.b303.mokkozi.board;

import com.b303.mokkozi.board.dto.BoardDto;
import com.b303.mokkozi.board.request.BoardModifyPatchReq;
import com.b303.mokkozi.board.request.BoardWritePostReq;
import com.b303.mokkozi.board.dto.BoardListDto;
import com.b303.mokkozi.comment.CommentService;
import com.b303.mokkozi.comment.dto.CommentDto;
import com.b303.mokkozi.common.response.BaseResponseBody;
import com.b303.mokkozi.entity.Board;
import com.b303.mokkozi.entity.Gallery;
import com.b303.mokkozi.entity.User;
import com.b303.mokkozi.gallery.GalleryService;
import com.b303.mokkozi.gallery.dto.GalleryDto;
import com.b303.mokkozi.gallery.dto.GalleryListDto;
import com.b303.mokkozi.user.UserService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Api(value = "게시판 API", tags = {"Board."})
@RestController
@RequestMapping("/api/meet/board")
public class BoardController {

    @Autowired
    BoardService boardService;

    @Autowired
    CommentService commentService;

    @Autowired
    GalleryService galleryService;

    @Autowired
    UserService userService;

    //게시글 목록 조회
    @GetMapping("")
    @ApiOperation(value = "게시글 목록 조회", notes = "게시글 목록을 조회한다.")
    @ApiResponses({@ApiResponse(code = 200, message = "성공"), @ApiResponse(code = 400, message = "실패"),
            @ApiResponse(code = 401, message = "로그인 인증 실패"), @ApiResponse(code = 403, message = "잘못된 요청")})
    public ResponseEntity<? extends BaseResponseBody> getBoardList(

            @RequestParam @ApiParam(value = "게시글 페이지 Index", defaultValue = "0") int page
            , @ApiIgnore Authentication authentication
    ) {
        log.info("BoardController.getBoardList 46 : 함수 시작.");

        try {
            User user = (User) authentication.getDetails();
//            User user = userService.findById((long)).get();
            log.info("BoardController.getBoardList 50 : User : {}", user.getEmail());
//            if(user!=null){}
            Page<BoardDto> boardList = boardService.getBoardList(user,page);
            log.info("BoardController.getBoardList 61 : boardList : {}", boardList);
            // 이미지도 가져온다.
            GalleryListDto galleryListDto = galleryService.getGalleryLists(boardList);
            log.info("BoardController.getBoardList 62 : 이미지 목록은..? : {}", galleryListDto.getGalleryList());

            // 댓글도 불러온다.
            List<List<CommentDto>> commentLists = new ArrayList<>();
            boardList.map(m -> commentLists.add(commentService.getCommentList(m.getId())));

            return ResponseEntity.ok(BoardListDto.of(200, "게시글 목록 조회 완료.", boardList, galleryListDto, commentLists));
        } catch (AuthenticationException | NullPointerException e) {
            return ResponseEntity.status(401).body(BaseResponseBody.of(401, "로그인 인증 실패"));
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return ResponseEntity.status(404).body(BaseResponseBody.of(404, "게시글 결과 없음."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(403).body(BaseResponseBody.of(403, "잘못된 요청입니다."));
        }

    }

    //게시글 상세조회
    @GetMapping("/{boardId}")
    @ApiOperation(value = "게시글 상세 조회", notes = "단일 게시글을 조회한다.")
    @ApiResponses({@ApiResponse(code = 200, message = "성공"), @ApiResponse(code = 400, message = "실패"),
            @ApiResponse(code = 401, message = "로그인 인증 실패"), @ApiResponse(code = 403, message = "잘못된 요청")})
    public ResponseEntity<? extends BaseResponseBody> getBoardDetail(
            @PathVariable("boardId") Long boardId
            , @ApiIgnore Authentication authentication
    ) {

        try {
            User user = (User) authentication.getDetails();
            BoardDto boardDto = boardService.getBoardDetail(user,boardId);

            // 이미지 정보도 함께 가져온다. 이 때 빈 리스트일 수도 있다. ( [] ) -> 게시글 목록 갯수와 일치시키기 위해
            List<GalleryDto> galleryList = galleryService.getGalleryList(boardService.getBoardById(boardId));

            List<CommentDto> commentList = commentService.getCommentList(boardId);

            return ResponseEntity.ok(BoardDto.of(200, "게시글 상세 조회 완료.", boardDto, galleryList, commentList));
        } catch (AuthenticationException | NullPointerException e) {
            return ResponseEntity.status(401).body(BaseResponseBody.of(401, "로그인 인증 실패"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(BaseResponseBody.of(404, "게시글 결과 없음."));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(BaseResponseBody.of(403, "잘못된 요청입니다."));
        }
    }


    // 게시글 작성
    @PostMapping("")
    @ApiOperation(value = "게시글 작성", notes = "새로운 게시글 작성한다.")
    @ApiResponses({@ApiResponse(code = 200, message = "성공"), @ApiResponse(code = 400, message = "실패"),
            @ApiResponse(code = 401, message = "로그인 인증 실패"), @ApiResponse(code = 403, message = "잘못된 요청")})
    public ResponseEntity<? extends BaseResponseBody> writeBoard(
            @ModelAttribute @ApiParam(value = "게시글 정보", required = true) BoardWritePostReq model
            , @ApiIgnore Authentication authentication
    ) {
        log.info("BoardController.createBoard 97 : 메서드 진입");
        log.info("BoardController.createBoard 98 : 파일 정보 : {}", model.getFiles());
        log.info("BoardController.createBoard 99 : 글 내용 : {}", model.getContent());

        try {
            User user = (User) authentication.getDetails();

            BoardDto board = boardService.createBoard(user, model);

            return ResponseEntity.status(200).body(BaseResponseBody.of(200, "게시글 작성 완료"));
        } catch (AuthenticationException | NullPointerException e) {
            return ResponseEntity.status(401).body(BaseResponseBody.of(401, "로그인 인증 실패"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(403).body(BaseResponseBody.of(403, "잘못된 요청입니다."));
        }
    }

    // 게시글 수정
    @PutMapping("")
    @ApiOperation(value = "게시글 수정", notes = "게시글 정보를 수정한다.")
    @ApiResponses({@ApiResponse(code = 200, message = "성공"), @ApiResponse(code = 400, message = "실패"),
            @ApiResponse(code = 401, message = "로그인 인증 실패"), @ApiResponse(code = 403, message = "잘못된 요청")})
    public ResponseEntity<? extends BaseResponseBody> modifyBoard(
            @ModelAttribute @ApiParam(value = "게시글 수정 정보", required = true) BoardModifyPatchReq model
            , @ApiIgnore Authentication authentication
    ) {

        log.info("BoardController.modifyBoard 141 : 컨트롤러 진입");
        log.info("BoardController.modifyBoard 142 : 데이터 확인 - 삭제할 파일 인덱스 : {}", model.getDeleteFilesIndex());
        log.info("BoardController.modifyBoard 143 : 데이터 확인 - 새로운 이미지 파일 : {}", model.getNewFiles());
        log.info("BoardController.modifyBoard 144 : 데이터 확인 - 수정한 내용 : {}", model.getContent());
        log.info("BoardController.modifyBoard 145 : 데이터 확인 - Board Id : {}", model.getId());

        User user = (User) authentication.getDetails();
        log.info("BoardController.modifyBoard 149 : 로그인한 유저 정보 확인 - {}", user);
        BoardDto result = boardService.modifyBoard(user, model);

        return ResponseEntity.status(200).body(BoardDto.of(200, "게시글 수정 완료", result));
    }


    // 게시글 삭제
    @DeleteMapping("")
    @ApiOperation(value = "게시글 삭제", notes = "게시글을 삭제한다.")
    @ApiResponses({@ApiResponse(code = 200, message = "성공"), @ApiResponse(code = 400, message = "실패"),
            @ApiResponse(code = 401, message = "로그인 인증 실패"), @ApiResponse(code = 403, message = "잘못된 요청")})
    public ResponseEntity<? extends BaseResponseBody> deleteBoard(
            @RequestParam @ApiParam(value = "게시글 ID", required = true) Long boardId
            ,@ApiIgnore Authentication authentication) {

        try {
            boardService.deleteBoard(boardId);
            return ResponseEntity.status(200).body(BaseResponseBody.of(200, "게시글 삭제 완료"));
        } catch (AuthenticationException | NullPointerException e) {
            return ResponseEntity.status(401).body(BaseResponseBody.of(401, "로그인 인증 실패"));
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return ResponseEntity.status(404).body(BaseResponseBody.of(404, "게시글 삭제 실패"));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(BaseResponseBody.of(403, "잘못된 요청입니다."));

        }

    }

    // 게시글 좋아요
    @PostMapping("/like")
    @ApiOperation(value = "게시글 좋아요", notes = "게시글 좋아요 기능을 적용한다.")
    @ApiResponses({@ApiResponse(code = 200, message = "성공"), @ApiResponse(code = 400, message = "실패"),
            @ApiResponse(code = 401, message = "로그인 인증 실패"), @ApiResponse(code = 403, message = "잘못된 요청")})
    public ResponseEntity<? extends BaseResponseBody> boardLike(
            @RequestParam @ApiParam(value = "게시글 ID", required = true) Long boardId
            ,@ApiIgnore Authentication authentication
    ) {

        try {
            User user = (User) authentication.getDetails();

            boardService.createBoardLike(user, boardId);
            return ResponseEntity.status(200).body(BaseResponseBody.of(200, "success"));
        } catch (AuthenticationException | NullPointerException e) {
            return ResponseEntity.status(401).body(BaseResponseBody.of(401, "로그인 인증 실패"));
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return ResponseEntity.status(404).body(BaseResponseBody.of(404, "게시글 없음."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(403).body(BaseResponseBody.of(403, "잘못된 요청입니다."));
        }

    }

    // 게시글 좋아요 취소
    @DeleteMapping("/unlike")
    @ApiOperation(value = "게시글 좋아요 취소", notes = "게시글 좋아요 기능을 취소한다.")
    @ApiResponses({@ApiResponse(code = 200, message = "성공"), @ApiResponse(code = 400, message = "실패"),
            @ApiResponse(code = 401, message = "로그인 인증 실패"), @ApiResponse(code = 403, message = "잘못된 요청")})
    public ResponseEntity<? extends BaseResponseBody> boardUnlike(
            @RequestParam @ApiParam(value = "게시글 ID", required = true) Long boardId
            ,@ApiIgnore Authentication authentication
    ) {
        try {
            User user = (User) authentication.getDetails();

            boardService.deleteBoardLike(user, boardId);
            return ResponseEntity.status(200).body(BaseResponseBody.of(200, "success"));
        } catch (AuthenticationException | NullPointerException e) {
            return ResponseEntity.status(401).body(BaseResponseBody.of(401, "로그인 인증 실패"));
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return ResponseEntity.status(404).body(BaseResponseBody.of(404, "게시글 없음."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(403).body(BaseResponseBody.of(403, "잘못된 요청입니다."));
        }
    }

    /* 게시글 좋아요 확인?
    -> 이걸 GetMapping으로 따로 뺄지
        아니면 게시글 상세조회에서 정보를 포함하여 함께 리턴할지
    * */


    // 게시글 검색
//    @GetMapping("/search/{type}")
//    @ApiOperation(value = "게시글 목록 검색", notes = "게시글 목록을 검색한다.")
//    @ApiResponses({@ApiResponse(code = 200, message = "성공"), @ApiResponse(code = 400, message = "실패"),
//            @ApiResponse(code = 401, message = "로그인 인증 실패"), @ApiResponse(code = 403, message = "잘못된 요청")})
//    public ResponseEntity<? extends BaseResponseBody> searchBoardList(
//            @PathVariable("type") String type, @RequestParam @ApiParam(value = "검색 키워드", required = true) String
//            keyword, @RequestParam @ApiParam(value = "페이지 번호", required = true) int pageIdx
//            ,@ApiIgnore Authentication authentication
//    ) {
//        try {
//            User user = (User) authentication.getDetails();
//            Page<BoardDto> boardList = boardService.searchBoardList(user,type, keyword, pageIdx);
//            // 이미지도 같이 검색해서 보내야 한다.
//
//            return ResponseEntity.ok(BoardListDto.of(200, "게시글 검색 완료.", boardList));
//        } catch (AuthenticationException | NullPointerException e) {
//            return ResponseEntity.status(401).body(BaseResponseBody.of(401, "로그인 인증 실패"));
//        } catch (NoSuchElementException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(404).body(BaseResponseBody.of(404, "게시글 검색 결과 없음."));
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(403).body(BaseResponseBody.of(403, "잘못된 요청입니다."));
//        }
//    }

}
