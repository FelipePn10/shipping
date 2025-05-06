package redirex.shipping.controllers;

import ch.qos.logback.core.model.Model;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import redirex.shipping.dto.LoginDTO;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.repositories.UserRepository;
import redirex.shipping.security.JwtUtil;
import redirex.shipping.service.email.UserEmailService;
import redirex.shipping.service.email.UserEmailServiceImpl;
import redirex.shipping.util.email.UserEmailDetailsUtil;

import java.util.Optional;

@Controller
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getSenha())
            );
            String token = jwtUtil.generateToken(loginDTO.getEmail());
            return ResponseEntity.ok(token);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar login");
        }
    }

    @RestController
    @RequestMapping("/email/")
    public class EmailController {

        @Autowired
        private UserEmailServiceImpl emailService;

        @PostMapping("/send")
        public String sendEmail(@RequestBody UserEmailDetailsUtil details) {
            try {
                return emailService.sendSimpleMail(details);
            } catch (Exception e) {
                return "Erro ao enviar email: " + e.getMessage();
            }
        }
    }

    // Geramos o token único, invoca emailService.sendPasswordResetEmail com o email do usuário e o token gerado. A EmailServiceImpl lida com a construção e o envio do email.
    @PostMapping("/forgot-password")
    public String forgotPassword(String userEmail) {
        @Autowired
        private UserEmailService emailService;

        // Gerar um token para redefinição de senha (implementação omitida)
        String token = "generated-token";

        // Enviar o email de redefinição de senha
        emailService.sendPasswordResetEmail(userEmail, token);

        // Exibir uma mensagem para confirmação
        return "password_reset_confirmation";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("email") String email, RedirectAttributes attributes, Model model) {
        Optional<UserEntity> userEntity = userRepository.findByEmail(email);
        if (userEntity.isPresent()) {
            UserEntity user = userEntity.get();
        } else {
            attributes.addAttribute("error", "Nenhum usuário com esse email foi encontrado");
            return "redirect:/usuarios";
        }
        return "redirect:/login";
    }
}
