package com.example.capsulee_backend.aws.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Uploader {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    /**
     * 파일 업로드 후 URL 반환
     */
    public String upload(MultipartFile file, String dirName) {

        String fileName = dirName + "/" + createFileName(file.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
//                    .acl("public-read")  // 퍼블릭 읽기 허용 (S3 정책 필요)
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(file.getBytes())
            );

        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }

        return getFileUrl(fileName);
    }



    private String createFileName(String originalName) {
        String ext = originalName.substring(originalName.lastIndexOf("."));
        return UUID.randomUUID().toString() + ext;
    }

    private String getFileUrl(String key) {
        return "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/" + key;
    }
}