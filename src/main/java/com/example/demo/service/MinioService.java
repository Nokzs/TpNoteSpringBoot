package com.example.demo.service;
import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
@Slf4j
public class MinioService {

    private final MinioClient minioClient;

    public MinioService(@Value("${minio.endpoint}") String endpoint,
                        @Value("${minio.accessKey}") String accessKey,
                        @Value("${minio.secretKey}") String secretKey)
            throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        log.info("log"+endpoint);
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();

        log.info("Bucked coursm2 exists ? {}", minioClient.bucketExists(BucketExistsArgs.builder().bucket("coursm2").build()));
    }

    public String getSignedUrlCover(String cover) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return this.minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket("coursm2").object(cover+"/cover").method(Method.PUT).expiry(5, TimeUnit.MINUTES).build());
    }

}