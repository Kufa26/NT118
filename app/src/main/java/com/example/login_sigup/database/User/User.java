package com.example.login_sigup.database.User;

public class User {
    private Long idUser;
    private String fullName;
    private String email;
    private String password;
    private String dob;
    private String gender;
    private String country;
    private String phoneNumber;
    private String avatarUrl;

    public User(Long idUser, String fullName, String email, String password, String dob, String gender, String country, String phoneNumber, String avatarUrl) {
        super();
        this.idUser = idUser;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.dob = dob;
        this.gender = gender;
        this.country = country;
        this.phoneNumber = phoneNumber;
        this.avatarUrl = avatarUrl;
    }

    public User(String fullName, String email, String password, String dob, String gender, String country, String phoneNumber, String avatarUrl) {
        super();
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.dob = dob;
        this.gender = gender;
        this.country = country;
        this.phoneNumber = phoneNumber;
        this.avatarUrl = avatarUrl;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
