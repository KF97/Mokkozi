package com.b303.mokkozi.gallery;

import com.b303.mokkozi.board.dto.BoardDto;
import com.b303.mokkozi.entity.Board;
import com.b303.mokkozi.entity.Gallery;
import com.b303.mokkozi.entity.User;
import com.b303.mokkozi.gallery.dto.GalleryDto;
import com.b303.mokkozi.gallery.dto.GalleryListDto;
import com.b303.mokkozi.gallery.request.GalleryVO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface GalleryService {
    // Gallery 객체를 DB에 저장한다.
    Gallery galleryCreate(GalleryVO galleryVo, String id);

    // Gallery 객체 반환한다.
    Gallery getGallery(Long id);

    // 하나의 게시글에 대한 이미지 목록을 받아온다.
    List<GalleryDto> getGalleryList(Board board);

    // 사용자 PK를 이용하여 이미지 목록을 받아온다.
    List<GalleryDto> getGalleryListByUser(User user);

    // 게시글 목록을 받아서, 게시글 목록에 대한 전체 이미지를 찾는다.
    GalleryListDto getGalleryLists(Page<BoardDto> boardList);

    // Gallery PK를 받아 해당 이미지를 삭제한다.
    void deleteGallery(Long id);
}
