package com.cozyhome.onlineshop.emailservice;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class Emailservice {

	private final JavaMailSender mailSender;
	
	public void sendActivationEmail(String emailTo, String link) {		
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailTo);
        message.setSubject("Cozy Home - Activate Your Account");
        message.setText("Please click the following link to activate your account: " + link);
        log.info("[ON sendActivationEmail] :: The message has been sent to: " + emailTo);
        mailSender.send(message);
    }
}
