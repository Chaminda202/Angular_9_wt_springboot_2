package com.spring.redditclone.service.impl;

import com.spring.redditclone.exception.SpringRedditException;
import com.spring.redditclone.jwt.JwtProvider;
import com.spring.redditclone.mapper.UserMapper;
import com.spring.redditclone.model.AuthenticationResponse;
import com.spring.redditclone.model.LoginRequest;
import com.spring.redditclone.model.NotificationEmail;
import com.spring.redditclone.model.RegisterRequest;
import com.spring.redditclone.model.entity.User;
import com.spring.redditclone.model.entity.VerificationToken;
import com.spring.redditclone.repository.UserRepository;
import com.spring.redditclone.repository.VerificationTokenRepository;
import com.spring.redditclone.service.AuthService;
import com.spring.redditclone.service.MailContentBuilder;
import com.spring.redditclone.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailContentBuilder mailContentBuilder;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @Override
    public void signup(RegisterRequest registerRequest) {
        User user = this.userMapper.mapToEntity(registerRequest);
        user.setPassword(this.passwordEncoder.encode(registerRequest.getPassword()));
        this.userRepository.save(user);
        String token = generateVerificationToken(user);
        String message = "Thank you for signing up to Spring Reddit, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8070/api/auth/accountVerification/" + token;
        NotificationEmail notificationEmail = NotificationEmail.builder()
                .body(this.mailContentBuilder.build(message))
                .recipient(user.getEmail())
                .subject("Active your account")
                .build();
        this.mailService.sendMail(notificationEmail);
    }

    @Override
    public void accountVerification(String token) {
        Optional<VerificationToken> optionalVerificationToken = this.verificationTokenRepository.findByToken(token);
        if (optionalVerificationToken.isPresent()) {
            User user = optionalVerificationToken.get().getUser();
            user.setEnabled(true);
            this.userRepository.save(user);
        } else {
            throw new SpringRedditException("Invalid Token");
        }
    }

    @Override
    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        // Store authenticate object
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = this.jwtProvider.generateToken(authentication);
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .username(loginRequest.getUsername())
                .build();

    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .build();
        this.verificationTokenRepository.save(verificationToken);
        return token;
    }
}
