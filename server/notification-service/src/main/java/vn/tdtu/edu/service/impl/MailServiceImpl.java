package vn.tdtu.edu.service.impl;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import vn.tdtu.edu.dto.MailDetails;
import vn.tdtu.edu.service.interfaces.MailService;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void sendMail(MailDetails details) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            helper.setFrom(senderEmail, "BuddyBlend");
            helper.setTo(details.getSendTo());
            helper.setText(details.getText(), true);
            helper.setSubject(details.getSubject());

            mailSender.send(message);
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
