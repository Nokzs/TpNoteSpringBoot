package com.example.demo.utils;


import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    public String getSignedUrlRestaurant(String key) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
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
            throw new RuntimeException("Erreur lors de la vérification de l'objet", e);
        } catch (Exception e) {
            throw new RuntimeException("Erreur MinIO", e);
        }
    }

    public String getPublicUrl(String key) {
        try {
            String coursm2 = this.minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)       // méthode GET pour récupérer le fichier
                            .bucket("coursm2")
                            .object(key)
                            .expiry(24, TimeUnit.HOURS)  // durée de validité de l'URL
                            .build()
            );
            return coursm2;
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
                                .method(Method.GET)
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
}
