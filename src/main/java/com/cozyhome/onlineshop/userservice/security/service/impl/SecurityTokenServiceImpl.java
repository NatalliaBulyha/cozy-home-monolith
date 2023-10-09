package com.cozyhome.onlineshop.userservice.security.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import com.cozyhome.onlineshop.exception.DataNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cozyhome.onlineshop.dto.EmailMessageDto;
import com.cozyhome.onlineshop.emailservice.Emailservice;
import com.cozyhome.onlineshop.userservice.model.User;
import com.cozyhome.onlineshop.userservice.model.token.PasswordResetToken;
import com.cozyhome.onlineshop.userservice.model.token.SecurityToken;
import com.cozyhome.onlineshop.userservice.model.token.TokenTypeE;
import com.cozyhome.onlineshop.userservice.repository.SecurityTokenRepository;
import com.cozyhome.onlineshop.userservice.repository.UserRepository;
import com.cozyhome.onlineshop.userservice.security.service.SecurityTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SecurityTokenServiceImpl implements SecurityTokenService {
    private final Emailservice emailService;
    private final UserRepository userRepository;
    private final SecurityTokenRepository securityTokenRepository;

    @Value("${activation.message.link}")
    private String activationLink;
    @Value("${activation.message.subject}")
    private String activationEmailSubject;
    @Value("${activation.message.text}")
    private String activationEmailText;

    @Value("${reset-password.message.link}")
    private String resetPasswordEmailLink;
    @Value("${reset-password.message.subject}")
    private String resetPasswordEmailSubject;
    @Value("${reset-password.message.text}")
    private String resetPasswordEmailText;

    @Override
    public void createActivationUserToken(User user) {
        String activationToken = generateRandomToken();
        String link = activationLink + activationToken;
        SecurityToken token = SecurityToken.builder()
				.token(activationToken)
				.user(user)
                .expiration(calculateExpiryDate())
				.tokenType(TokenTypeE.ACTIVATE_USER)
				.build();
        log.info("[ON createActivationUserToken] :: Token is created " + token);
        securityTokenRepository.save(token);

        EmailMessageDto activationEmail = EmailMessageDto.builder()
				.link(link)
				.mailTo(user.getEmail())
                .subject(activationEmailSubject)
				.text(activationEmailText + link)
				.build();
        emailService.sendEmail(activationEmail);
    }

    @Override
    public void createPasswordResetToken(String userEmail, String ipAddress) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new DataNotFoundException(String.format("User with email %s doesn't found.", userEmail)));
        log.info("[ON createPasswordResetToken] :: got user by email " + userEmail);
        String token = generateRandomToken();
        String link = resetPasswordEmailLink + token;
        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
				.token(token)
				.user(user)
                .expiration(calculateExpiryDate())
				.tokenType(TokenTypeE.RESET_PASSWORD)
                .ipAddress(ipAddress)
                .build();
        log.info("[ON createPasswordResetToken] :: Token is created " + token);
        securityTokenRepository.save(passwordResetToken);

        EmailMessageDto activationEmail = EmailMessageDto.builder()
				.link(link)
				.mailTo(user.getEmail())
                .subject(resetPasswordEmailSubject)
				.text(resetPasswordEmailText + link)
				.build();
        emailService.sendEmail(activationEmail);
    }

    private LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plus(24, ChronoUnit.HOURS);
    }

    private String generateRandomToken() {
        return UUID.randomUUID().toString();
    }

}
