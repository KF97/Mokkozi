package com.b303.mokkozi.comment.request;

import com.b303.mokkozi.entity.Board;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel("CommentWritePostRequest")
public class CommentWritePostReq {

    @ApiModelProperty(name = "게시판 Id", example = "1")
    String boardId;

    @ApiModelProperty(name = "댓글 내용", example = "댓글 내용")
    String content;
}
