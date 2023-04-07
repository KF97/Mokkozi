package com.b303.mokkozi.comment;

import com.b303.mokkozi.comment.dto.CommentDto;
import com.b303.mokkozi.comment.dto.CommentListDto;
import com.b303.mokkozi.comment.request.CommentWritePostReq;
import com.b303.mokkozi.common.response.BaseResponseBody;
import com.b303.mokkozi.entity.Comment;
import com.b303.mokkozi.entity.User;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Api(value = "댓글 API", tags = { "Comment." })
@RestController
@RequestMapping("/api/meet/comment")
public class CommentController {

    @Autowired
    CommentService commentService;

    // 댓글 작성
    @PostMapping("")
    @ApiOperation(value = "댓글 작성", notes = "새로운 댓글을 작성한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 400, message = "실패"),
            @ApiResponse(code = 401, message = "로그인 인증 실패"),
            @ApiResponse(code = 403, message = "잘못된 요청")})
    public ResponseEntity<? extends BaseResponseBody> writeComment(
            @RequestBody @ApiParam(value = "댓글 정보", required = true) CommentWritePostReq cwpr, Authentication authentication
    ) {
        log.info("CommentController.writeComment 40 : 댓글 작성 컨트롤러 진입");
        log.info("CommentController.writeComment 41 : 게시글 번호 : {}", cwpr.getBoardId());
        log.info("CommentController.writeComment 42 : 댓글 내용 : {}", cwpr.getContent());
        User user = (User) authentication.getDetails();

        try {
            // 작성한 댓글 객체를 받아온다.
            Comment comment = commentService.createComment(user, cwpr);
            // StatusCode, Message, CommentDto를 반환한다.
            return ResponseEntity.status(200).body(CommentDto.of(200, "댓글 작성 완료", comment));
        }
        catch (Exception e)  {
            e.printStackTrace();
            return ResponseEntity.status(500).body(BaseResponseBody.of(500, "에러 발생"));
        }

    }

    // 댓글 삭제
    @DeleteMapping("")
    @ApiOperation(value = "댓글 삭제", notes = "댓글을 삭제한다.")
    @ApiResponses({ @ApiResponse(code = 200, message = "성공"), @ApiResponse(code = 400, message = "실패"),
            @ApiResponse(code = 401, message = "로그인 인증 실패"),@ApiResponse(code = 403, message = "잘못된 요청")})
    public ResponseEntity<? extends BaseResponseBody> deleteComment(
            @RequestParam @ApiParam(value = "댓글 ID", required = true) Long commentId
    ) {
        try {
            commentService.deleteComment(commentId);
            return ResponseEntity.status(200).body(BaseResponseBody.of(200, "댓글 삭제 완료"));
        }catch (NoSuchElementException e) {
            e.printStackTrace();
            return ResponseEntity.status(404).body(BaseResponseBody.of(404, "댓글 삭제 실패"));
        }catch (Exception e){
            return ResponseEntity.status(403).body(BaseResponseBody.of(403, "잘못된 요청입니다."));
        }
    }

    // 게시글 댓글 목록 불러오기
    @GetMapping("")
    @ApiOperation(value = "하나의 게시글에 대한 댓글 목록 조회", notes = "댓글 목록을 조회한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 400, message = "실패"),
            @ApiResponse(code = 401, message = "로그인 인증 실패"),
            @ApiResponse(code = 403, message = "잘못된 요청")})
    public ResponseEntity<? extends BaseResponseBody> getCommentList(
            @RequestParam @ApiParam(value = "해당 댓글의 게시글 Id", required = true) String boardId) {

        List<CommentDto> commentList = commentService.getCommentList(Long.parseLong(boardId));

        return ResponseEntity.status(200).body(CommentListDto.of(200, "댓글 목록", commentList));
    }
}
