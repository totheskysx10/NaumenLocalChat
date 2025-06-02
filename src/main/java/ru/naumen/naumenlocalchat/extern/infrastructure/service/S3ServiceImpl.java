package ru.naumen.naumenlocalchat.extern.infrastructure.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import ru.naumen.naumenlocalchat.app.service.S3Service;
import ru.naumen.naumenlocalchat.exception.FileDuplicateException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

/**
 * Реализация интерфейса S3 хранилища
 */
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    private final String bucketName;
    private final Logger log = LoggerFactory.getLogger(S3ServiceImpl.class);

    public S3ServiceImpl(S3Client s3Client, @Value("${yandex.cloud.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public String uploadFile(MultipartFile multipartFile, String folderName) throws IOException, FileDuplicateException {
        String fileName = multipartFile.getOriginalFilename();
        int lastDot = fileName.lastIndexOf('.');
        String name = fileName.substring(0, lastDot);
        String extension = fileName.substring(lastDot);
        String mimeType = multipartFile.getContentType();

        String key = folderName + "/" + name + extension;

        if (!doesFileExist(key)) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(mimeType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));
            log.info("Файл добавлен в S3.");

            return String.format("https://storage.yandexcloud.net/%s/%s", bucketName, key);
        } else {
            throw new FileDuplicateException("Файл с таким именем уже был добавлен!");
        }
    }

    /**
     * Проверяет, существует ли файл в S3
     *
     * @param key ключ файла в S3
     * @return true, если файл существует, иначе false
     */
    private boolean doesFileExist(String key) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.headObject(headObjectRequest);

            return true;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;
            } else {
                throw e;
            }
        }
    }
}
