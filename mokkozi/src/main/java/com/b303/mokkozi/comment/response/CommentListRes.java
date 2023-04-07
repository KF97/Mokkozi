package com.b303.mokkozi.comment.response;

import com.b303.mokkozi.common.response.BaseResponseBody;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel("CommentListResponse")
public class CommentListRes extends BaseResponseBody {

//    private List<CommentDto> commentList;
//
//    public static CommentListRes of(Integer statusCode, String message, List<CommentDto> commentList) {
//        CommentListRes res = new CommentListRes();
//        res.setStatusCode(statusCode);
//        res.setMessage(message);
//        res.setCommentList(commentList);
//        return res;
//    }
}
