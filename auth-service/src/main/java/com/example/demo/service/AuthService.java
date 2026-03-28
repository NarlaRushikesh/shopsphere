package com.example.demo.service;

public interface AuthService {

    String signup(String name, String email, String password, String role);

    String login(String email, String password);
}