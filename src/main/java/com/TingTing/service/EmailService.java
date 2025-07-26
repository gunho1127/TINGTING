package com.TingTing.service;

import com.TingTing.entity.User;
import com.TingTing.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void sendVerificationEmail(User user) {
        String token = jwtTokenProvider.createToken(user.getUsEmail());
        String link = "https://yourapp.com/api/auth/verify?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getUsEmail());
        message.setSubject("이메일 인증을 완료해주세요");
        message.setText("다음 링크를 클릭해 인증을 완료하세요:\n" + link);
        message.setFrom(senderEmail);

        mailSender.send(message);
    }
}
