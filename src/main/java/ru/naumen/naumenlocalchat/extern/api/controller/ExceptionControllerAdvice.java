package ru.naumen.naumenlocalchat.extern.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.naumen.naumenlocalchat.extern.api.dto.ErrorDTO;

/**
 * Обработчик исключений
 */
@ControllerAdvice
public class ExceptionControllerAdvice {

    /**
     * Обрабатывает java.lang.Exception
     * @param e исключение
     */
    @ExceptionHandler(java.lang.Exception.class)
    public ResponseEntity<ErrorDTO> handleException(Exception e) {
        ErrorDTO errorDTO = new ErrorDTO(e.getMessage());
        return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
