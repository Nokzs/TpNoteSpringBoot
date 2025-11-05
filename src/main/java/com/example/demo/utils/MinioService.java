package com.example.demo.utils;


import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MinioService {

    private final MinioClient minioClient;

    public MinioService(@Value("${minio.endpoint}") String endpoint,
                        @Value("${minio.accessKey}") String accessKey,
                        @Value("${minio.secretKey}") String secretKey)
            throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        this.minioClient =
                MinioClient.builder()
                        .endpoint(endpoint)
                        .credentials(accessKey, secretKey)
                        .build();

        log.info("Bucked coursm2 exists ? {}", this.minioClient.bucketExists(BucketExistsArgs.builder().bucket("coursm2").build()));
    }
    @Secured("ADMIN")
    public String getSignedUrlRestaurant(String key) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.PUT)
                        .bucket("coursm2")
                        .object("restaurantPicture" + key)
                        .expiry(24, TimeUnit.HOURS)
                        .build()
        );
    }

    public boolean objectExists(String bucketName, String objectKey) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            );
            return true;
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return false;
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur inattendue lors du traitement");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur inattendue lors du traitement");
        }
    }

    public String getPublicUrl(String key) {
        try {
            return this.minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket("coursm2")
                            .object(key)
                            .expiry(24, TimeUnit.HOURS)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Impossible de générer l'URL signée", e);
        }
    }

    public List<String> getSignedUrlEvaluation(ArrayList<String> keys, int nbPhotos) {
        List<String> urls = new ArrayList<>();

        for (int i = 0; i <= nbPhotos; i++) {

            String objectKey = keys.get(i);
            try {
                String signedUrl = minioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(Method.PUT)
                                .bucket("coursm2")
                                .object(objectKey)
                                .expiry(24, TimeUnit.HOURS)
                                .build()
                );
                urls.add(signedUrl);
            } catch (Exception e) {
                throw new RuntimeException("Impossible de générer l'URL signée pour " + objectKey, e);
            }
        }
        return urls;
    }

    public void deleteObject(String key) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket("coursm2")
                            .object(key)
                            .build()
            );
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new RuntimeException(e);
        }
    }
}
