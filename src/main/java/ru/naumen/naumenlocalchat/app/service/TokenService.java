package ru.naumen.naumenlocalchat.app.service;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.stereotype.Service;
import ru.naumen.naumenlocalchat.domain.TokenType;

import java.util.Random;

// TODO это чисто для работы с токенами у юзеров.
//  Для приглашения в чаты создать отдельный класс или в чатСервисе реализовать логику

/**
 * Сервис работы с токенами у пользователей
 */
@Service
public class TokenService {

    private final Cache<String, Long> resetPasswordTokenCache;
    private final Cache<String, Long> emailConfirmTokenCache;

    public TokenService(Cache<String, Long> resetPasswordTokenCache, Cache<String, Long> emailConfirmTokenCache) {
        this.resetPasswordTokenCache = resetPasswordTokenCache;
        this.emailConfirmTokenCache = emailConfirmTokenCache;
    }

    /**
     * Возвращает нужный кэш по типу токена
     * @param tokenType тип токена
     */
    private Cache<String, Long> getCacheByTokenType(TokenType tokenType) {
        return switch (tokenType) {
            case EMAIL_CONFIRM -> emailConfirmTokenCache;
            case RESET_PASSWORD -> resetPasswordTokenCache;
        };
    }

    /**
     * Генерирует токен и кладёт его в кэш
     * @param tokenType тип токена
     * @param userId Id пользователя
     * @return токен
     */
    public String generateToken(TokenType tokenType, Long userId) {
        Random random = new Random();
        String symbols = "ABCDEFGHIJJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < 32; i++) {
            stringBuilder.append(symbols.charAt(random.nextInt(symbols.length())));
        }

        String token = stringBuilder.toString();
        getCacheByTokenType(tokenType).put(token, userId);

        return token;
    }

    /**
     * Проверяет, что токен соответствует переданному идентификатору пользователя
     * @param tokenType тип токена
     * @param token токен
     * @param userId идентификатор пользователя
     */
    public boolean isTokenValid(TokenType tokenType, String token, Long userId) {
        Long cachedUserId = getCacheByTokenType(tokenType).getIfPresent(token);
        return userId.equals(cachedUserId);
    }

    /**
     * Инвалидирует токен
     * @param tokenType тип токена
     * @param token токен
     */
    public void invalidateToken(TokenType tokenType, String token) {
        getCacheByTokenType(tokenType).invalidate(token);
    }
}
