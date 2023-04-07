package com.b303.mokkozi.comment;

import com.b303.mokkozi.board.BoardService;
import com.b303.mokkozi.comment.dto.CommentDto;
import com.b303.mokkozi.comment.request.CommentWritePostReq;
import com.b303.mokkozi.entity.Comment;
import com.b303.mokkozi.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CommentServiceImpl implements CommentService{

    @Autowired
    BoardService boardService;

    @Autowired
    CommentRepository commentRepository;

    @Override
    public Comment createComment(User user, CommentWritePostReq cwpr) {

        Comment comment = new Comment();
        comment.setContent(cwpr.getContent());

        // Board 객체를 집어넣어야 한다.
        comment.setBoard(boardService.getBoardById(Long.parseLong(cwpr.getBoardId())));
        comment.setUser(user);

        return commentRepository.save(comment);
    }


    @Override
    public void deleteComment(Long commentId) throws NoSuchElementException {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NoSuchElementException("not found"));
        commentRepository.deleteById(comment.getId());
    }

    @Override
    public List<CommentDto> getCommentList(Long boardId) {
        List<Comment> comments = commentRepository.findByBoardId(boardId).orElseThrow(() -> new NoSuchElementException("not found"));

        List<CommentDto> result = new ArrayList<>();
        // CommentDto로 변환한다.
        for (Comment comment:comments) {
            CommentDto commentDto = new CommentDto();
            commentDto.setContent(comment.getContent());
            commentDto.setRegDate(comment.getRegDate());
            commentDto.setNickName(comment.getUser().getNickname());
            commentDto.setEmail(comment.getUser().getEmail());
            commentDto.setFile_path(comment.getUser().getProfile());
            commentDto.setId(comment.getId());

            result.add(commentDto);
        }

        return result;
    }
}
