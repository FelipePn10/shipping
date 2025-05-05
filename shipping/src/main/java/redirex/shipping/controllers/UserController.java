package redirex.shipping.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.dto.RegisterUserDTO;
import redirex.shipping.entity.User;
import redirex.shipping.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserDTO registerUserDTO) {
        try {
            logger.info("Recebida requisição para criar usuário: {}", registerUserDTO.getEmail());
            User newUser = userService.registerNewUser(registerUserDTO);
            logger.info("Usuário criado com sucesso: {}", newUser.getEmail());
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            logger.error("Erro de integridade de dados: {}", e.getMessage(), e);
            return new ResponseEntity<>("Erro: Email, CPF ou outro campo único já existe", HttpStatus.CONFLICT);
        } catch (Exception e) {
            logger.error("Erro ao criar usuário: {}", e.getMessage(), e);
            return new ResponseEntity<>("Erro ao criar usuário: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            logger.info("Recebida requisição para listar todos os usuários");
            List<User> users = userService.getAllUsers();
            logger.info("Retornando {} usuários", users.size());
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erro ao listar usuários: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
