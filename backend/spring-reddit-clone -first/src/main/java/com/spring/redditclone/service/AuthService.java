package com.spring.redditclone.service;

import com.spring.redditclone.model.AuthenticationResponse;
import com.spring.redditclone.model.LoginRequest;
import com.spring.redditclone.model.RegisterRequest;

public interface AuthService {
    void signup(RegisterRequest registerRequest);
    void accountVerification(String token);
    AuthenticationResponse login(LoginRequest loginRequest);
}
