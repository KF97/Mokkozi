package com.b303.mokkozi.gallery.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class ProfileWrapper {
    private MultipartFile file;
    private String email;
}
