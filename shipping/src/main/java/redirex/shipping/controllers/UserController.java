package redirex.shipping.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.repositories.UserRepository;
import redirex.shipping.service.UserService;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        try {
            logger.info("Recebida requisição para listar todos os usuários");
            List<UserEntity> users = userService.getAllUsers();
            logger.info("Retornando {} usuários", users.size());
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erro ao listar usuários: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
