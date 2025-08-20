package redirex.shipping.controller.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.service.email.UserEmailServiceImpl;
import redirex.shipping.service.email.UserEmailDetailsUtil;

@RestController
@RequestMapping("/email")
public class UserEmailController {

    private static final Logger logger = LoggerFactory.getLogger(UserEmailController.class);

    @Autowired
    private UserEmailServiceImpl emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody UserEmailDetailsUtil details) {
        logger.info("Sending email to: {}", details.getRecipient());
        try {
            String result = emailService.sendSimpleMail(details);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error sending email: {}", e.getMessage());
            return ResponseEntity.status(500).body("Erro ao enviar email: " + e.getMessage());
        }
    }
}