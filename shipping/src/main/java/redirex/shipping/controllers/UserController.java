package redirex.shipping.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.dto.RegisterUserDTO;
import redirex.shipping.entity.User;
import redirex.shipping.service.UserService;

@RestController
@RequestMapping("/usuarios")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> registerUser(@RequestBody RegisterUserDTO registerUserDTO) {
        User newUser = userService.registerNewUser(registerUserDTO);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
}
