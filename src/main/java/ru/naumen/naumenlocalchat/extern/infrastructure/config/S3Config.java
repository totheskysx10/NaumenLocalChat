package ru.naumen.naumenlocalchat.extern.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

/**
 * Конфигурация S3
 */
@Component
public class S3Config {

    private final String accessKey;
    private final String secretKey;

    public S3Config(@Value("${yandex.cloud.access}") String accessKey,
                    @Value("${yandex.cloud.secret}") String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    /**
     * S3 клиент
     */
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.of("ru-central1"))
                .endpointOverride(URI.create("https://storage.yandexcloud.net"))
                .build();
    }
}
