package ru.naumen.naumenlocalchat.extern.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserUpdatePasswordDTO {

    @NotBlank
    @Size(min = 8, message = "Не меньше 8 знаков")
    @Pattern(regexp = ".*[A-ZА-Я].*", message = "Пароль должен содержать хотя бы одну заглавную букву (русскую или английскую)")
    @Pattern(regexp = ".*[a-zа-я].*", message = "Пароль должен содержать хотя бы одну строчную букву (русскую или английскую)")
    @Pattern(regexp = ".*\\d.*", message = "Пароль должен содержать хотя бы одну цифру")
    private String password;

    @NotBlank
    @Size(min = 8, message = "Не меньше 8 знаков")
    @Pattern(regexp = ".*[A-ZА-Я].*", message = "Пароль должен содержать хотя бы одну заглавную букву (русскую или английскую)")
    @Pattern(regexp = ".*[a-zа-я].*", message = "Пароль должен содержать хотя бы одну строчную букву (русскую или английскую)")
    @Pattern(regexp = ".*\\d.*", message = "Пароль должен содержать хотя бы одну цифру")
    private String passwordConfirm;

    public UserUpdatePasswordDTO(String password, String passwordConfirm) {
        this.password = password;
        this.passwordConfirm = passwordConfirm;
    }

    public UserUpdatePasswordDTO() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}
