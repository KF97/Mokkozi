package com.b303.mokkozi.comment.dto;

import com.b303.mokkozi.common.response.BaseResponseBody;
import com.b303.mokkozi.entity.Comment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CommentDto extends BaseResponseBody {
    @ApiModelProperty(name = "댓글 PK")
    private Long id;
    @ApiModelProperty(name = "댓글 내용")
    private String content;
    @ApiModelProperty(name = "댓글 작성일")
    private Date regDate;
    @ApiModelProperty(name = "댓글 작성자 닉네임")
    private String nickName;
    @ApiModelProperty(name = "댓글 작성자 이메일")
    private String email;
    @ApiModelProperty(name = "작성자 프로필 이미지 주소")
    private String file_path;

    public static CommentDto of(Integer statusCode, String message, Comment comment) {
        CommentDto res = new CommentDto();
        res.setId(comment.getId());
        res.setStatusCode(statusCode);
        res.setMessage(message);
        res.setContent(comment.getContent());
        res.setRegDate(comment.getRegDate());
        res.setNickName(comment.getUser().getNickname());
        res.setEmail(comment.getUser().getEmail());
        res.setFile_path(comment.getUser().getProfile());

        return res;
    }
}
