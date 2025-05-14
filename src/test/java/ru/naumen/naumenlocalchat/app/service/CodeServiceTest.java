package ru.naumen.naumenlocalchat.app.service;

import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.naumen.naumenlocalchat.domain.CodeType;
import ru.naumen.naumenlocalchat.exception.InvalidCodeException;

/**
 * Тест сервиса кодов приглашения
 */
class CodeServiceTest {

    private CodeService codeService;

    @Mock
    private Cache<String, Long> chatCodeCache;

    @Mock
    private Cache<String, Long> groupChatCodeCache;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        codeService = new CodeService(chatCodeCache, groupChatCodeCache);
    }

    /**
     * Тест добавления кода в кэш
     */
    @Test
    void testPutCode() {
        Mockito.when(groupChatCodeCache.getIfPresent("40635474")).thenReturn(null);

        String code = codeService.putCode(CodeType.GROUP, 2L);

        Assertions.assertNotNull(code);
        Mockito.verify(groupChatCodeCache).put(code, 2L);
    }

    /**
     * Тест получения Id по коду
     */
    @Test
    void testGetIdByCode() throws InvalidCodeException {
        String testCode = "12345678";
        Mockito.when(chatCodeCache.getIfPresent(testCode)).thenReturn(1L);

        Long id = codeService.getIdByCode(CodeType.BASIC, testCode);

        Assertions.assertEquals(1L, id);
    }

    /**
     * Тест ошибки при получении Id по несуществующему коду
     */
    @Test
    void testGetIdByCodeNotFound() {
        String testCode = "12345678";
        Mockito.when(chatCodeCache.getIfPresent(testCode)).thenReturn(null);

        Exception e = Assertions.assertThrows(InvalidCodeException.class, () -> codeService.getIdByCode(CodeType.BASIC, testCode));

        Assertions.assertEquals("Код 12345678 не найден для типа BASIC", e.getMessage());
    }

    /**
     * Тест инвалидации кода
     */
    @Test
    void testInvalidateCode() {
        String testCode = "12345678";
        codeService.invalidateCode(CodeType.BASIC, testCode);
        Mockito.verify(chatCodeCache).invalidate(testCode);
    }

    /**
     * Тест, что коды разные для разных входных данных
     */
    @Test
    void testGenerateDifferentCodesForDifferentInputs() {
        String code1 = codeService.putCode(CodeType.BASIC, 1L);
        String code2 = codeService.putCode(CodeType.GROUP, 1L);
        String code3 = codeService.putCode(CodeType.BASIC, 2L);

        Assertions.assertNotEquals(code1, code2);
        Assertions.assertNotEquals(code1, code3);
        Assertions.assertNotEquals(code2, code3);
    }

    /**
     * Тест, что коды одинаковы для одинаковых входных данных
     */
    @Test
    void testGenerateSameCodesForSameInputs() {
        String code1 = codeService.putCode(CodeType.BASIC, 1L);
        String code2 = codeService.putCode(CodeType.BASIC, 1L);

        Assertions.assertEquals(code1, code2);
    }
}