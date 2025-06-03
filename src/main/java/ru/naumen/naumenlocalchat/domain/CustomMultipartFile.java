package ru.naumen.naumenlocalchat.domain;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Кастомный файл для загрузки на S3
 */
public class CustomMultipartFile implements MultipartFile {

    /**
     * Название файла
     */
    private final String fileName;

    /**
     * Массив байт файла
     */
    private final byte[] bytes;

    /**
     * Тип файла
     */
    private final String contentType;

    public CustomMultipartFile(String fileName, byte[] bytes, String contentType) {
        this.fileName = fileName;
        this.bytes = bytes;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public String getOriginalFilename() {
        return fileName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return bytes == null || bytes.length == 0;
    }

    @Override
    public long getSize() {
        return bytes.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return bytes;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
    }
}
