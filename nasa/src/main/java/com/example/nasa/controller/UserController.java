package com.example.nasa.controller;


import com.example.nasa.entity.Role;
import com.example.nasa.entity.User;
import com.example.nasa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public User register(@RequestBody User user){
        String username  = user.getUsername();
        String password = user.getPassword();
        Integer id = user.getId();
        String email = user.getEmail();


        String encryptedPassword = passwordEncoder.encode(password);

        User newUser = new User(id, email, username,  encryptedPassword);

        return userService.saveUser(newUser);
    }
}
