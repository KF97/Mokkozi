package com.b303.mokkozi.board;

import com.b303.mokkozi.board.dto.BoardDto;
import com.b303.mokkozi.board.request.BoardModifyPatchReq;
import com.b303.mokkozi.board.request.BoardWritePostReq;
import com.b303.mokkozi.comment.CommentService;
import com.b303.mokkozi.comment.dto.CommentDto;
import com.b303.mokkozi.config.S3Uploader;
import com.b303.mokkozi.entity.*;
import com.b303.mokkozi.gallery.GalleryService;
import com.b303.mokkozi.gallery.dto.GalleryDto;
import com.b303.mokkozi.gallery.request.GalleryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private S3Uploader s3Uploader;

    @Autowired
    GalleryService galleryService;

    @Autowired
    CommentService commentService;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    UserBoardLikeRepository ublRepository;

    @Override
    public Page<BoardDto> getBoardList(User user, int pageIdx) {

        int size = 10;
        int page = pageIdx <= 0 ? 0 : pageIdx - 1;

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<Board> pageTuts = boardRepository.findByActiveLike(pageable, "활동");
        Page<BoardDto> boardList = pageTuts.map(m -> new BoardDto(m, ublRepository.findByUserIdAndBoardId(user.getId(), m.getId()).isPresent(),ublRepository.countByBoardId(m.getId())));

        return boardList;
    }

    @Override
    public BoardDto createBoard(User user, BoardWritePostReq bwpr) {

        // 1. 먼저 Board 객체를 저장한다.
        Board board = new Board();
        board.setContent(bwpr.getContent());
        board.setUser(user);
        board.setActive("활동");
        // DB에 저장한 객체를 반환한다.
        board = boardRepository.save(board);
        log.info("BoardServiceImpl.createBoard 67 : 저장한 게시글 정보는 {}", board.getId());

        boolean boardLike = ublRepository.findByUserIdAndBoardId(user.getId(), board.getId()).isPresent();

        // 파일이 없는 경우도 있을 수 있다! 이 경우에는 끝난다.
        if (bwpr.getFiles() == null) {
            log.info("BoardServiceImpl.createBoard 72 : 첨부한 이미지가 없습니다.");
        }
        // 1개 이상의 파일을 첨부한 경우
        else {
            log.info("BoardServiceImpl.createBoard 76 : 첨부한 이미지가 존재합니다. 업로드 시작합니다.");
            // 파일 하나하나 S3에 업로드 - DB에 저장
            for (MultipartFile file:bwpr.getFiles()) {
                // 1. S3에 업로드하기.
                String file_path = "";
                try {
                    file_path = s3Uploader.upload(file, "board");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // 2. DB에 저장하자.
                GalleryVO galleryVO = new GalleryVO();
                galleryVO.setSort("board");
                galleryVO.setFilePath(file_path);
                galleryVO.setTitle(file.getOriginalFilename());

                log.info("BoardServiceImpl.createBoard 94 : DB에 갤러리 정보 저장합니다. {} | {}", galleryVO, board.getId());
                galleryService.galleryCreate(galleryVO, board.getId().toString());
            }
        }
        long likeCount = ublRepository.countByBoardId(board.getId());
        return new BoardDto(board, boardLike, likeCount);


    }

    @Override
    public void deleteBoard(Long boardId) {

        // 1. Board 객체를 불러온다.
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NoSuchElementException("not found"));

        // S3에 업로드된 이미지도 지우기 위해, List<Gallery>를 불러온다.
        List<GalleryDto> galleryList = galleryService.getGalleryList(board);

        // 하나하나 Key값을 이용해 S3에서 지운다.
        for (GalleryDto galleryDto:galleryList) {
            String key = galleryDto.getFile_path();
            s3Uploader.delete(key);

            // 이미지 삭제하기
            galleryService.deleteGallery(galleryDto.getId());
        }

        // Board의 상태를 변경한다.
        board.setActive("삭제"); // 삭제된 걸 의미한다.
        boardRepository.save(board);
    }

    @Override
    public BoardDto getBoardDetail(User user, Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NoSuchElementException("not found"));
        boolean boardLike = ublRepository.findByUserIdAndBoardId(user.getId(), board.getId()).isPresent();
        long likeCount = ublRepository.countByBoardId(board.getId());
        return new BoardDto(board, boardLike,likeCount);
    }

    @Override
    public Board getBoardById(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new NoSuchElementException("not found"));
    }

    @Override
    public Page<BoardDto> searchBoardList(User user, String type, String keyword, int pageIdx) {

        int size = 10;
        Pageable pageable = PageRequest.of(pageIdx, size, Sort.by(Sort.Direction.DESC, "id"));
        if (type.equals("writer")) {
            Page<Board> pageTuts = boardRepository.findByUserIdContaining(pageable, keyword);
            Page<BoardDto> boardList = pageTuts.map(m -> new BoardDto(m, ublRepository.findByUserIdAndBoardId(user.getId(), m.getId()).isPresent(),ublRepository.countByBoardId(m.getId())));
            return boardList;
        } else if (type.equals("tag")) {
            //태그..?
            //Page<Board> pageTuts = boardRepository.findByTitleContaining(pageable,keyword);
        }


        return null;
    }

    @Override
    public void createBoardLike(User user, Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NoSuchElementException("not found"));
        UserBoardLike bl = new UserBoardLike();
        bl.setUser(user);
        bl.setBoard(board);
        if (!ublRepository.findByUserIdAndBoardId(user.getId(), boardId).isPresent()) {
            ublRepository.save(bl);
        }
    }

    @Override
    public BoardDto modifyBoard(User user, BoardModifyPatchReq model) {
        Board board = boardRepository.findById(model.getId()).orElseThrow(() -> new NoSuchElementException("not found"));

        // 게시글 작성자만 수정 가능하다.
        if (board.getUser().getId() == user.getId()) {
            board.setContent(model.getContent());
            board = boardRepository.save(board);

            // 1. S3에서 이미지 삭제하기
            for (String galleryId:model.getDeleteFilesIndex()) {
                // 찾지 못하면 NoSuchElementException이 발생한다.
                Gallery gallery = galleryService.getGallery(Long.parseLong(galleryId));

                String key = gallery.getFilePath();
                log.info("BoardServiceImpl.modifyBoard 187 : 삭제할 이미지의 Key값은:{}", key);
                s3Uploader.delete(key);
            }
            // 2. DB에서 이미지 삭제하기
            for (String galleryId:model.getDeleteFilesIndex()) {
                galleryService.deleteGallery(Long.parseLong(galleryId));
            }

            int count = 0;
            // 3. S3에 새로운 이미지 업로드하기

            if (model.getNewFiles() != null) {
                for (MultipartFile file:model.getNewFiles()) {
                    try {
                        String file_path = s3Uploader.upload(file, "board");

                        // 4. DB에 새로운 이미지 추가하기
                        GalleryVO galleryVO = new GalleryVO();
                        galleryVO.setFilePath(file_path);
                        galleryVO.setTitle(file.getOriginalFilename());
                        galleryVO.setSort("board");

                        galleryService.galleryCreate(galleryVO, model.getId().toString());
                        count++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            // newFiles가 null인 경우에는 올릴 대상이 없다.

            log.info("BoardServiceImpl.modifyBoard 182: {}개의 새로운 파일을 업로드하였습니다.", count);

            boolean boardLike = ublRepository.findByUserIdAndBoardId(user.getId(), board.getId()).isPresent();
            long likeCount = ublRepository.countByBoardId(board.getId());
            return new BoardDto(board, boardLike,likeCount);

        } else throw new AccessDeniedException("");
    }

    @Override
    public void deleteBoardLike(User user, Long boardId) {
        UserBoardLike ubl = ublRepository.findByUserIdAndBoardId(user.getId(), boardId).orElseThrow(() -> new NoSuchElementException("not found"));
        ublRepository.delete(ubl);
    }

}
