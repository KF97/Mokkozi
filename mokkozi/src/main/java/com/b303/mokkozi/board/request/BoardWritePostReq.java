package com.b303.mokkozi.board.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
@ApiModel("BoardWritePostRequest")
public class BoardWritePostReq {

    @ApiModelProperty(name = "이미지 리스트")
    private List<MultipartFile> files;

    @ApiModelProperty(name = "게시글 content", example = "게시글 내용")
    private String content;

}
