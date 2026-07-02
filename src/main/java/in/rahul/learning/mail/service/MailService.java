package in.rahul.learning.mail.service;

import in.rahul.learning.mail.utils.MailServiceUtil;
import in.rahul.learning.mail.utils.MailTemplate;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class MailService {

    private static Logger LOG = LoggerFactory.getLogger(MailService.class);

    private static final String EMAIL = "email";

    private final JavaMailSender javaMailSender;
    private final MailServiceUtil mailServiceUtil;
    private final MailTemplate mailTemplate;

    @Async
    public void sendMailTemplate(Map<String, Object> response, Resource template) throws MessagingException, UnsupportedEncodingException {

        //String email = (String) response.get("email");

         String email = (String) response.get(EMAIL);

        Map<String, String> mailBodyPlaceholders = new HashMap<>();

        if (response.get("name") != null) {
            mailBodyPlaceholders.put("{name}", response.get("name").toString());
        } else {
            mailBodyPlaceholders.put("{name}", "User");
        }

        if (response.get(EMAIL) != null) {
            mailBodyPlaceholders.put("{email}", response.get(EMAIL).toString());
        }else {
            mailBodyPlaceholders.put("{email}", "name@example.com");
        }

        if (response.get("phone") != null) {
            mailBodyPlaceholders.put("{phone}", response.get("phone").toString());
        } else {
            mailBodyPlaceholders.put("{phone}", "Not Provided");
        }

        String templateFile;
        String subject;

        // ✅ TEMPLATE DECISION LOGIC
        if (template == mailTemplate.welcomeEmail) {
            templateFile = mailServiceUtil.readMailTemplateFile(mailTemplate.welcomeEmail);
            subject = "Welcome to Platform";
        }
        else if (template == mailTemplate.emailVerification) {
            templateFile = mailServiceUtil.readMailTemplateFile(mailTemplate.emailVerification);
            subject = "Verify Your Email";
        }
        else if (template == mailTemplate.userDetailsUpdateTemplate) {
            templateFile = mailServiceUtil.readMailTemplateFile(mailTemplate.userDetailsUpdateTemplate);
            subject = "Profile Updated Successfully";
        }
        else {
            throw new IllegalArgumentException("Unknown email template");
        }

        String body = mailServiceUtil.replacePlaceholdersByPattern(
                templateFile,
                mailBodyPlaceholders
        );

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(body, true);

        // ✅ SET FROM NAME HERE
        mimeMessageHelper.setFrom(new InternetAddress(
                "your-email@gmail.com",
                "Demo Learning Platform"
        ));

        // ✅ CC (visible to user)
//        mimeMessageHelper.setCc(new String[]{
//                "support@yourapp.com",
//                "admin@yourapp.com"
//        });

        // 🔒 BCC (hidden recipients)
//        mimeMessageHelper.setBcc(new String[]{
//                "rahulakula2020@gmail.com",
//                "logs@yourapp.com"
//        });

        javaMailSender.send(message);

        LOG.info("Sent mail email to {}" , email);

    }



    public CompletableFuture<Void> sendWelcomeMail(Map<String, Object> response) {

        return CompletableFuture.runAsync(() -> {
            try {
                this.sendMailTemplate(response, mailTemplate.welcomeEmail);
                this.sendWelcomeMail(response)
                        .thenRun(() -> System.out.println("Email sent successfully"))
                        .exceptionally(ex -> {
                            System.out.println("Email failed: " + ex.getMessage());
                            return null;
                        });
            } catch (Exception e) {
                System.err.println("Email failed: " + e.getMessage());
            }
        });
    }

    /**
     * mailAsyncService.sendWelcomeMail(response)
     *     .thenRun(() -> System.out.println("Email sent successfully"))
     *     .exceptionally(ex -> {
     *         System.out.println("Email failed: " + ex.getMessage());
     *         return null;
     *     });
     * */

//    @Autowired
//    private JavaMailSender mailSender;
//
//    @Autowired
//    private TemplateEngine templateEngine;
//
//    public void sendWelcomeEmail(String toEmail, String name, String phone) {
//
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//
//            // 👇 Prepare Thymeleaf variables
//            Context context = new Context();
//            context.setVariable("name", name);
//            context.setVariable("email", toEmail);
//            context.setVariable("phone", phone);
//
//            // 👇 Load HTML template
//            String html = templateEngine.process("welcome-email", context);
//
//            helper.setTo(toEmail);
//            helper.setSubject("Welcome to Our Platform");
//            helper.setText(html, true); // true = HTML email
//
//            mailSender.send(message);
//
//        } catch (Exception e) {
//            throw new RuntimeException("Email sending failed", e);
//        }
//    }
}
