package com.b303.mokkozi.gallery.dto;

import com.b303.mokkozi.entity.Gallery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GalleryListDto {
    @ApiModelProperty(name = "모든 게시글에 대한 파일 목록이다.")
    private List<List<GalleryDto>> galleryList;
}
