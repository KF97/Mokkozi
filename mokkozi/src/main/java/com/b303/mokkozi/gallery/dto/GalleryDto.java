package com.b303.mokkozi.gallery.dto;

import com.b303.mokkozi.entity.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GalleryDto {
    @ApiModelProperty(name = "PK")
    private long id;
    @ApiModelProperty(name = "S3 경로")
    private String file_path;
    @ApiModelProperty(name = "email")
    private String email;
    @ApiModelProperty(name = "boardId")
    private String boardId;
    @ApiModelProperty(name = "파일 이름")
    private String title;
}
