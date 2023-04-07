package com.b303.mokkozi.gallery.dto;

import com.b303.mokkozi.common.response.BaseResponseBody;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GalleriesDto extends BaseResponseBody {
    private List<GalleryDto> galleryList;

    public static BaseResponseBody of(Integer statusCode, String message, List<GalleryDto> galleryList) {
        GalleriesDto res = new GalleriesDto();
        res.setStatusCode(statusCode);
        res.setMessage(message);
        res.setGalleryList(galleryList);
        return res;
    }
}
