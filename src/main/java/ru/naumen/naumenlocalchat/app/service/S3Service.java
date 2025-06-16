package ru.naumen.naumenlocalchat.app.service;

import org.springframework.web.multipart.MultipartFile;
import ru.naumen.naumenlocalchat.exception.FileDuplicateException;

import java.io.IOException;

/**
 * Интерфейс S3 хранилища
 */
public interface S3Service {

    /**
     * Загружает файл в хранилище
     * @param multipartFile файл
     * @param folderName папка (id пользователя)
     * @return ссылка на файл
     */
    String uploadFile(MultipartFile multipartFile, String folderName) throws IOException, FileDuplicateException;
}
