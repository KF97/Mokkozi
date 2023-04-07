package com.b303.mokkozi.board.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@ApiModel("BoardModifyPatchRequest")
public class BoardModifyPatchReq {

    @ApiModelProperty(name="게시글 PK",example = "1")
    private Long id;
    @ApiModelProperty(name = "게시글 content", example = "게시글 내용")
    private String content;
    @ApiModelProperty(name = "삭제할 게시글 번호")
    private List<String> deleteFilesIndex;
    @ApiModelProperty(name = "추가할 이미지 파일")
    private List<MultipartFile> newFiles;

}
