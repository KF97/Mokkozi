package com.b303.mokkozi.board;

import com.b303.mokkozi.board.dto.BoardDto;
import com.b303.mokkozi.board.request.BoardModifyPatchReq;
import com.b303.mokkozi.board.request.BoardWritePostReq;
import com.b303.mokkozi.entity.Board;
import com.b303.mokkozi.entity.User;
import org.springframework.data.domain.Page;

import java.util.NoSuchElementException;

public interface BoardService {
    
    Page<BoardDto> getBoardList(User user,int page);

    BoardDto createBoard(User user, BoardWritePostReq bwpr);

    void deleteBoard(Long boardId) throws NoSuchElementException;

    BoardDto getBoardDetail(User user, Long boardId) throws NoSuchElementException;

    Board getBoardById(Long boardId);

    Page<BoardDto> searchBoardList(User user,String type,String keyword, int pageIdx);

    void createBoardLike(User userEmail, Long boardId);

    BoardDto modifyBoard(User user, BoardModifyPatchReq bmpr);

    void deleteBoardLike(User user, Long boardId);
}
