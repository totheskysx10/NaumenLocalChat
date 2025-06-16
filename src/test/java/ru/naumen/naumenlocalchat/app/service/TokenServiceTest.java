package ru.naumen.naumenlocalchat.app.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import com.github.benmanes.caffeine.cache.Cache;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.naumen.naumenlocalchat.domain.TokenType;

/**
 * Тест сервиса работы с токенами
 */
class TokenServiceTest {

    private TokenService tokenService;

    @Mock
    private Cache<String, Long> resetPasswordTokenCache;

    @Mock
    private Cache<String, Long> emailConfirmTokenCache;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tokenService = new TokenService(resetPasswordTokenCache, emailConfirmTokenCache);
    }

    /**
     * Тест генерации токена
     */
    @Test
    void testGenerateToken() {
        String token = tokenService.generateToken(TokenType.EMAIL_CONFIRM, 1L);

        Assertions.assertTrue(token.matches("^[A-Z0-9]{32}$"));
        Mockito.verify(emailConfirmTokenCache).put(Mockito.eq(token), Mockito.eq(1L));
    }

    /**
     * Тест валидации токена
     */
    @Test
    void testIsTokenValid() {
        Mockito.when(resetPasswordTokenCache.getIfPresent("token")).thenReturn(1L);
        boolean tokenValid = tokenService.isTokenValid(TokenType.RESET_PASSWORD, "token", 1L);
        Assertions.assertTrue(tokenValid);
    }

    /**
     * Тест валидации токена, если он невалиден
     */
    @Test
    void testIsTokenValidWhenInvalidToken() {
        Mockito.when(resetPasswordTokenCache.getIfPresent("token")).thenReturn(2L);
        boolean tokenValid = tokenService.isTokenValid(TokenType.RESET_PASSWORD, "token", 1L);
        Assertions.assertFalse(tokenValid);
    }

    /**
     * Тест валидации токена, если такой записи нет в кэше
     */
    @Test
    void testIsTokenValidWhenNoRecordInCache() {
        boolean tokenValid = tokenService.isTokenValid(TokenType.RESET_PASSWORD, "token", 1L);
        Assertions.assertFalse(tokenValid);
    }

    /**
     * Тест инвалидации токена
     */
    void testInvalidateToken() {
        tokenService.invalidateToken(TokenType.EMAIL_CONFIRM, "token");
        Mockito.verify(emailConfirmTokenCache).invalidate(Mockito.eq("token"));
    }
}