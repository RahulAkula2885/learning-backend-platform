package in.rahul.learning.mail.utils;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Data
@Component
public class MailTemplate {

    @Value("classpath:templates/emailVerification.html")
    public Resource emailVerification;
    @Value("classpath:templates/welcome-email.html")
    public Resource welcomeEmail;
    @Value("classpath:templates/userDetailsUpdateTemplate.html")
    public Resource userDetailsUpdateTemplate;
}
