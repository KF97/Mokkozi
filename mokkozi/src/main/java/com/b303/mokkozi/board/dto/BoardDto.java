package com.b303.mokkozi.board.dto;


import com.b303.mokkozi.comment.dto.CommentDto;
import com.b303.mokkozi.common.response.BaseResponseBody;
import com.b303.mokkozi.entity.Board;
import com.b303.mokkozi.gallery.dto.GalleryDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class BoardDto extends BaseResponseBody {

    private Long id;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date regDate;
    private String active;
    private String userEmail;
    private String nickName;
    private boolean boardLike; //현재 로그인한 사용자의 게시글 좋아요 여부
    private String profileUrl;
    private List<GalleryDto> galleryList;
    private List<CommentDto> commentList;
    private Long likeCount;

//    private List<CommentDto> comment;


    @Builder
    public BoardDto(Board board, boolean boardLike,Long likeCount) {

        this.id = board.getId();
        this.content = board.getContent();
        this.regDate = board.getRegDate();
        this.active = board.getActive();

        this.userEmail = board.getUser().getEmail();
        this.nickName = board.getUser().getNickname();
        this.profileUrl = board.getUser().getProfile();

        this.boardLike = boardLike;
        this.likeCount = likeCount;
    }

    public static BoardDto of(Integer statusCode, String message, BoardDto board) {
        BoardDto res = board;
        res.setStatusCode(statusCode);
        res.setMessage(message);
        return res;
    }

    public static BoardDto of(Integer statusCode, String message, BoardDto board,
                              List<GalleryDto> galleryList, List<CommentDto> commentList) {
        BoardDto res = board;
        res.setStatusCode(statusCode);
        res.setMessage(message);
        res.setGalleryList(galleryList);
        res.setCommentList(commentList);
        return res;
    }
}
