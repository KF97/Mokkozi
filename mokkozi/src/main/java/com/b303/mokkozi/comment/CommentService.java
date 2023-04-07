package com.b303.mokkozi.comment;

import com.b303.mokkozi.comment.dto.CommentDto;
import com.b303.mokkozi.comment.request.CommentWritePostReq;
import com.b303.mokkozi.entity.Comment;
import com.b303.mokkozi.entity.User;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface CommentService {
    Comment createComment(User user, CommentWritePostReq cwpr);

    void deleteComment(Long commentId) throws NoSuchElementException;

    List<CommentDto> getCommentList(Long boardId);
}
