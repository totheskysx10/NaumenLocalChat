package ru.naumen.naumenlocalchat.extern.api.dto;

import jakarta.validation.constraints.*;
import org.springframework.hateoas.RepresentationModel;

public class RegisterDTO extends RepresentationModel<RegisterDTO> {

    @Email
    private String email;

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

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    public RegisterDTO(String email, String password, String passwordConfirm, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public RegisterDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
