package com.b303.mokkozi.gallery.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.annotation.Nullable;

@Getter
@Setter
@ToString
public class GalleryVO {

    @ApiModelProperty(name = "S3에 업로드한 URL")
    private String filePath;

    @ApiModelProperty(name = "파일명")
    private String title;

    @ApiModelProperty(name = "파일 유형. 사용자 프로필에 올라간 이미지인지, 게시글 첨부 파일인지 구분.", example = "프로필 or 게시글")
    private String sort;

    @Nullable
    @ApiModelProperty(name = "사용자 프로필 이미지인 경우 사용한다.", example = "3")
    private String email;

    @Nullable
    @ApiModelProperty(name = "게시글 이미지인 경우 사용한다.", example = "50")
    private Long boardId;

}
