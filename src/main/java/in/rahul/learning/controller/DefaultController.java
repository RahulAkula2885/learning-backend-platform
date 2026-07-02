package in.rahul.learning.controller;

import in.rahul.learning.commons.BaseResponse;
import in.rahul.learning.mail.service.MailService;
import in.rahul.learning.mail.utils.MailTemplate;
import in.rahul.learning.model.entity.User;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Hidden
@RestController
@RequiredArgsConstructor
public class DefaultController implements ErrorController {


    private final MailService mailService;
    private final MailTemplate mailTemplate;

    private static final String PATH = "/error";

    @GetMapping
    public ResponseEntity<BaseResponse> getWelcomeMessage() {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        createMethod(arr);
//        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
//        list.add(11);
//        System.out.println(list);

        return ResponseEntity.ok(BaseResponse.builder()
                .status(200)
                .message("Welcome to Full stack Learning")
                .timestamp(Instant.now())
                .build());
    }


    @RequestMapping(value = PATH)
    public ResponseEntity<BaseResponse> handleError(HttpServletRequest request) {

        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");

        Map<String, Object> response = new HashMap<>();
        response.put("path", request.getAttribute("jakarta.servlet.error.request_uri"));

        return ResponseEntity.ok(BaseResponse.builder()
                .status(statusCode)
                .message("Something went wrong or API not found")
                .data(response)
                .timestamp(Instant.now())
                .build());
    }

    private void createMethod(int... arr) {
        System.out.println("Array: " + Arrays.toString(arr));
    }


    @GetMapping("/send/mail")
    public ResponseEntity<BaseResponse> sendMail(HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        String subject = request.getParameter("subject");
        String body = request.getParameter("body");
        String to = request.getParameter("to");
        String cc = request.getParameter("cc");
        String bcc = request.getParameter("bcc");
        String from = request.getParameter("from");

        System.out.println("From: " + from + ", to: " + to + ", cc: " + cc + ", bcc: " + bcc + ", subject: " + subject);

        Map<String, Object> response = new HashMap<>();
        response.put("name","Rahul");
        response.put("email","rahulakula2015@gmail.com");
        response.put("phone","123456789");
        mailService.sendMailTemplate(response,mailTemplate.welcomeEmail);

        return ResponseEntity.ok(BaseResponse.builder()
                .status(200)
                .message("SUCCESS")
                .data("")
                .timestamp(Instant.now())
                .build());
    }


}
