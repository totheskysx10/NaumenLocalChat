package ru.naumen.naumenlocalchat.extern.api.dto;

import org.springframework.hateoas.RepresentationModel;

public class RegisterDTO extends RepresentationModel<RegisterDTO> {

    public String email;
    public String password;
    public String passwordConfirm;
    public String firstName;
    public String lastName;

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
