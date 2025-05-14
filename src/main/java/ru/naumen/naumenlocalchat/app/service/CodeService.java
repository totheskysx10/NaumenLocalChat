package ru.naumen.naumenlocalchat.app.service;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.stereotype.Service;
import ru.naumen.naumenlocalchat.domain.CodeType;
import ru.naumen.naumenlocalchat.exception.InvalidCodeException;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Сервис кодов приглашения
 */
@Service
public class CodeService {

    private final Cache<String, Long> chatCodeCache;
    private final Cache<String, Long> groupChatCodeCache;

    public CodeService(Cache<String, Long> chatCodeCache, Cache<String, Long> groupChatCodeCache) {
        this.chatCodeCache = chatCodeCache;
        this.groupChatCodeCache = groupChatCodeCache;
    }

    /**
     * Возвращает нужный кэш по типу кода
     * @param codeType тип кода
     */
    private Cache<String, Long> getCacheByCodeType(CodeType codeType) {
        return switch (codeType) {
            case BASIC -> chatCodeCache;
            case GROUP -> groupChatCodeCache;
        };
    }

    /**
     * Кладёт код приглашения в кэш
     * @param codeType тип кода
     * @param id идентификатор сущности
     * @return код
     */
    public String putCode(CodeType codeType, Long id) {
        String code = generateCode(codeType, id);
        Cache<String, Long> cache = getCacheByCodeType(codeType);
        cache.put(code, id);
        return code;
    }

    /**
     * Генерирует уникальный 8-значный числовой код из хэша
     */
    private String generateCode(CodeType codeType, Long id) {
        try {
            String input = codeType.toString() + ":" + id;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            BigInteger bigInt = new BigInteger(1, hash);

            long codeNum = bigInt.mod(BigInteger.valueOf(100_000_000)).longValue();

            return String.format("%08d", codeNum);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 error", e);
        }
    }

    /**
     * Возвращает id сущности по коду приглашения
     *
     * @param codeType тип кода
     * @param code код
     * @return ID сущности
     * @throws InvalidCodeException если код не найден
     */
    public Long getIdByCode(CodeType codeType, String code) throws InvalidCodeException {
        Long id = getCacheByCodeType(codeType).getIfPresent(code);
        if (id == null) {
            throw new InvalidCodeException("Код " + code + " не найден для типа " + codeType);
        }
        return id;
    }

    /**
     * Инвалидирует код приглашения
     * @param codeType тип кода
     * @param code код
     */
    public void invalidateCode(CodeType codeType, String code) {
        getCacheByCodeType(codeType).invalidate(code);
    }
}
