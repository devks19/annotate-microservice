package com.annotate.media.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final S3Presigner presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.presigned-url-expiration}")
    private long expirySeconds;

//    public String generateUploadUrl(Long videoId, String fileName) {
//
//        String key = buildKey(videoId, fileName);
//
//        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                .bucket(bucketName)
//                .key(key)
//                .contentType("video/mp4")
//                .build();
//
//        PutObjectPresignRequest presignRequest =
//                PutObjectPresignRequest.builder()
//                        .signatureDuration(Duration.ofSeconds(expirySeconds))
//                        .putObjectRequest(putObjectRequest)
//                        .build();
//
//        return presigner.presignPutObject(presignRequest)
//                .url()
//                .toString();
//    }

    public String generateUploadUrl(String s3Key) {

        String key = buildKey(s3Key);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("video/mp4")
                .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofSeconds(expirySeconds))
                        .putObjectRequest(putObjectRequest)
                        .build();

        return presigner.presignPutObject(presignRequest)
                .url()
                .toString();
    }

    public String generateStreamUrl(String s3Key) {

        String key = buildKey(s3Key);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofSeconds(expirySeconds))
                        .getObjectRequest(getObjectRequest)
                        .build();

        return presigner.presignGetObject(presignRequest)
                .url()
                .toString();
    }

    public String generateGetUrl(String key) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10)) // adjust if needed
                        .getObjectRequest(getObjectRequest)
                        .build();

        PresignedGetObjectRequest presignedRequest =
                presigner.presignGetObject(presignRequest);

        return presignedRequest.url().toString();
    }

//    private String buildKey(Long videoId, String fileName) {
//        return "videos/" + videoId + "/" + fileName;
//    }

    private String buildKey(String key) {
        return key;
    }

}