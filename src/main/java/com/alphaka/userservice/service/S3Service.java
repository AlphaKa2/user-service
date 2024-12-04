package com.alphaka.userservice.service;

import com.alphaka.userservice.dto.request.S3PresignedUrlRequest;
import com.alphaka.userservice.dto.response.S3PresignedUrlResponse;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import java.net.URL;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${cloud.aws.bucket}")
    private String bucketName;

    private final AmazonS3 s3Client;

    private static final long S3_URL_EXPIRATION_TIME = 1000 * 60 * 5;


    public S3PresignedUrlResponse generatePreSignedUrl(S3PresignedUrlRequest request) {

        String fileName = request.getFileName();
        String contentType = request.getContentType();

        Date expiration = new Date();
        expiration.setTime(expiration.getTime() + S3_URL_EXPIRATION_TIME);

        String filePath = "profile/" + UUID.randomUUID() + fileName;
        log.info("파일 경로:{}", filePath);

        GeneratePresignedUrlRequest presignedPostRequest = new GeneratePresignedUrlRequest(bucketName, filePath)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration)
                .withContentType(contentType);

        URL uploadUrl = s3Client.generatePresignedUrl(presignedPostRequest);
        log.info("업로드 URL:{}", uploadUrl);

        return new S3PresignedUrlResponse(uploadUrl.toString());
    }

}
