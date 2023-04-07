package com.b303.mokkozi.board.dto;

import com.b303.mokkozi.comment.dto.CommentDto;
import com.b303.mokkozi.common.response.BaseResponseBody;
import com.b303.mokkozi.gallery.dto.GalleryDto;
import com.b303.mokkozi.gallery.dto.GalleryListDto;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@ApiModel("BoardListResponse")
public class BoardListDto extends BaseResponseBody {

    private Page<BoardDto> boardList;

    private GalleryListDto galleryListDto;

    private List<List<CommentDto>> commentLists;

    public static BoardListDto of(Integer statusCode, String message,
                                  Page<BoardDto> boardList, GalleryListDto galleryListDto,
                                  List<List<CommentDto>> commentLists) {
        BoardListDto res = new BoardListDto();
        res.setStatusCode(statusCode);
        res.setMessage(message);
        res.setBoardList(boardList);
        res.setGalleryListDto(galleryListDto);
        res.setCommentLists(commentLists);
        return res;
    }

}
