package com.b303.mokkozi.comment.dto;

import com.b303.mokkozi.comment.response.CommentListRes;
import com.b303.mokkozi.common.response.BaseResponseBody;
import com.b303.mokkozi.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CommentListDto extends BaseResponseBody {
    private List<CommentDto> commentList;

    public static CommentListDto of(Integer statusCode, String message, List<CommentDto> commentList) {
        CommentListDto res = new CommentListDto();
        res.setStatusCode(statusCode);
        res.setMessage(message);
        res.setCommentList(commentList);
        return res;
    }
}
