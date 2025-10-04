package com.example.MyGmail.data.models;

public class RegisterRequest {
    private String username;
    private String first_name;
    private String sur_name;
    private String password;

    public RegisterRequest(String username, String first_name, String sur_name, String password) {
        this.username = username;
        this.first_name = first_name;
        this.sur_name = sur_name;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getSur_name() {
        return sur_name;
    }

    public String getPassword() {
        return password;
    }

}
