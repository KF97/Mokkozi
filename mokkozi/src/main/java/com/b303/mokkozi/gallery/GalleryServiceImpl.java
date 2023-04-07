package com.b303.mokkozi.gallery;

import com.b303.mokkozi.board.BoardService;
import com.b303.mokkozi.board.dto.BoardDto;
import com.b303.mokkozi.entity.Board;
import com.b303.mokkozi.entity.Gallery;
import com.b303.mokkozi.entity.User;
import com.b303.mokkozi.gallery.dto.GalleryDto;
import com.b303.mokkozi.gallery.dto.GalleryListDto;
import com.b303.mokkozi.gallery.request.GalleryVO;
import com.b303.mokkozi.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
public class GalleryServiceImpl implements GalleryService{
    @Autowired
    BoardService boardService;

    @Autowired
    UserService userService;

    @Autowired
    GalleryRepository galleryRepository;

    @Override
    public Gallery galleryCreate(GalleryVO galleryVo, String id) {
        // sort 통해 확인해보자.
        // id는 이메일이 될 수도 있고, board가 될 수도 있다.
        Gallery gallery = new Gallery();
        gallery.setFilePath(galleryVo.getFilePath());
        gallery.setTitle(galleryVo.getTitle());
        gallery.setSort(galleryVo.getSort());

        if (galleryVo.getSort().equals("board")) {
            log.info("GalleryServiceImpl.gallerycreate : 42 : 게시글에 첨부된 이미지입니다!!!!");
            Board board = boardService.getBoardById(Long.parseLong(id));
            gallery.setBoard(board);
        } else {
            log.info("GalleryServiceImpl.gallerycreate : 42 : 사용자 프로필에 첨부된 이미지입니다!!!");
            User user = userService.findByEmail(id).get();
            gallery.setUser(user);
        }

        return galleryRepository.save(gallery);
    }

    @Override
    public Gallery getGallery(Long id) {
        return galleryRepository.findById(id).orElseThrow(() -> new NoSuchElementException("not found"));
    }

    /**
     * 여러 개의 게시글 목록을 받아서, 하나하나에 대한 이미지 목록을 불러온다.
     * @param boardList
     * @return
     */
    @Override
    public GalleryListDto getGalleryLists(Page<BoardDto> boardList) {
        log.info("GallerrServiceImpl.getGalleryLists 59");
        GalleryListDto result = new GalleryListDto();
        List<List<GalleryDto>> tmp = new ArrayList<>(boardList.getSize());

        for (BoardDto boardDto:boardList) {
            log.info("GallerrServiceImpl.getGalleryLists 65 : 이미지를 가져올 게시글은 : {}", boardDto);

            List<Gallery> galleryList = galleryRepository.findAllByBoardId(boardDto.getId());

            List<GalleryDto> galleryDtoList = new ArrayList<>();

            // 게시글에 이미지가 첨부된 경우
            if (galleryList.size() > 0) {
                log.info("GallerrServiceImpl.getGalleryLists 70 : 게시글에 이미지가 첨부되었습니다. {}", galleryList);
                // gallery를 galleryDto로 변환

                for (Gallery gallery:galleryList) {
                    GalleryDto galleryDto = new GalleryDto();
                    galleryDto.setFile_path(gallery.getFilePath());
                    galleryDto.setTitle(gallery.getTitle());
                    galleryDto.setBoardId(gallery.getBoard().getId().toString());
                    // DTO로 변환했으면 넣자.
                    galleryDtoList.add(galleryDto);
                }
            }
            else {
                log.info("GallerrServiceImpl.getGalleryLists 70 : 게시글에 첨부된 이미지가 없습니다.");
            }
            // 빈 배열이라도 반드시 넣어준다.
            tmp.add(galleryDtoList);
        }
        result.setGalleryList(tmp);
        return result;
    }

    @Override
    public List<GalleryDto> getGalleryList(Board board) {
        // 게시글 번호를 통해 이미지 받아온다.
        List<Gallery> galleryList = galleryRepository.findAllByBoardId(board.getId());
        // Dto로 전환한다.
        List<GalleryDto> result = new ArrayList<>();

        // 게시글에 첨부된 이미지가 있는 경우.
        if (galleryList.size() > 0) {
            for (Gallery gallery : galleryList) {
                GalleryDto galleryDto = new GalleryDto();
                galleryDto.setFile_path(gallery.getFilePath());
                galleryDto.setBoardId(gallery.getBoard().getId().toString());
                galleryDto.setTitle(gallery.getTitle());
                galleryDto.setId(gallery.getId());

                result.add(galleryDto);
            }
        }
        // 빈 리스트를 반환할 수도 있다.
        return result;
    }

    @Override
    public List<GalleryDto> getGalleryListByUser(User user) {
        List<Gallery> galleryList = galleryRepository.findAllByUserId(user.getId());
        
        List<GalleryDto> result = new ArrayList<>();
        // GalleryDto로 변환한다.
        for (Gallery gallery:galleryList) {
            GalleryDto galleryDto = new GalleryDto();
            galleryDto.setFile_path(gallery.getFilePath());
            galleryDto.setTitle(gallery.getTitle());
            galleryDto.setEmail(gallery.getUser().getEmail());
            galleryDto.setId(gallery.getId());

            result.add(galleryDto);
        }

        return result;
    }

    @Override
    public void deleteGallery(Long id) {
        Gallery gallery = galleryRepository.findById(id).orElseThrow(() -> new NoSuchElementException("not found"));
        galleryRepository.delete(gallery);
        log.info("GalleryServiceImpl.deleteGallerys 146 : 전체 Gallery 파일 삭제 완료");
    }
}
