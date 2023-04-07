package com.b303.mokkozi.config;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Uploader {
    private final AmazonS3Client amazonS3Client;


    // Bucket 이름
    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    // MultipartFile을 File로 변환하고, 로컬 스토리지에 저장한다.
    public String upload(MultipartFile multipartFile, String dirName) throws IOException{
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("Error: MultipartFile -> FIle conver Fail"));

        return uploadToS3(uploadFile, dirName);
    }

    // S3에 올릴 파일 이름 설정하고, 올린 후 S3 URL 받아 반환하기.
    public String uploadToS3(File fileToUpload, String dirName) throws IOException {
        String fileName = dirName + "/" + UUID.randomUUID() + fileToUpload.getName();

        String uploadImageUrl = putS3(fileToUpload, fileName);
        removeNewFile(fileToUpload);
        return uploadImageUrl;
    }

    // S3에 업로드
    public String putS3(File file, String fileName) {
        // bucket : S3에 생성한 버킷 이름
        // fileName : S3 서버에 올릴 파일의 이름.
        // file : S3 서버에 올릴 파일.
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, file)
                // 파일의 권한을 설정한다. public으로 사용하여 누구나 읽고 쓸 수 있게 한다.
                .withCannedAcl(CannedAccessControlList.PublicReadWrite));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // 로컬에 파일 저장하기.
    // 로컬에 파일 저장하는 이유 : S3는 로컬에 저장된 파일을 업로드하기 때문에!
    private Optional<File> convert(MultipartFile file) throws IOException {
        // System.getProperty("user.dir") : 현재 프로젝트의 절대 경로를 꺼내올 수 있다.
        File convertFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());

        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    // 로컬에 저장된 이미지 지우기
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File Delete Success");
            return;
        }
        log.info("File Delete Fail");
    }

    // S3에서 파일 삭제하기.
    public void delete(String key) {
        key = key.replaceAll("https://mokkozi-s3.s3.ap-northeast-2.amazonaws.com/", "");
        log.info("삭제할 이미지의 Key값:{}", key);
        try {
            amazonS3Client.deleteObject(this.bucket, key);
        } catch (AmazonS3Exception e) {
            log.info("결국 S3에선 삭제할 수 없었다...");
        }

    }
}
