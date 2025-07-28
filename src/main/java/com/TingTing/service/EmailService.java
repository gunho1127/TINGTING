package com.TingTing.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String code) {
        String subject = "[TingTing] 이메일 인증 코드";
        String content = """
                <div style="font-family: Arial, sans-serif; font-size: 16px;">
                    <p><strong>TingTing 회원가입 이메일 인증</strong></p>
                    <p>인증 코드는 다음과 같습니다:</p>
                    <div style="font-size: 24px; font-weight: bold; color: #2d6cdf;">%s</div>
                    <p>인증 코드는 3분 동안 유효합니다.</p>
                </div>
                """.formatted(code);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true); // true = HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new IllegalStateException("이메일 전송 실패", e);
        }
    }
}