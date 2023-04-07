package com.b303.mokkozi.gallery.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@ToString
public class ImageWrapper {
    private List<MultipartFile> files;
    private String email;
    private String boardId;
    private String sort;
}
