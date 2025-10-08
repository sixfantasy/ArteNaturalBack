package com.artenatural.Back.controllers;

import com.artenatural.Back.repositories.UserRepository;
import com.artenatural.Back.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.artenatural.Back.entities.User;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @GetMapping("/me")
    public User getUserByToken(@RequestHeader("Authorization") String token) {
        String userID = jwtTokenUtil.getUserIdFromToken(token.substring(7));
        return userRepository.findById(Integer.parseInt(userID)).get();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userRepository.findById(id).orElse(null);
    }
    @PutMapping()
    public User updateUser(@RequestBody User user, @RequestHeader("Authorization") String token) {
        return userRepository.save(user);
    }
    @DeleteMapping()
    public void deleteUser(@RequestBody User user) {
        userRepository.delete(user);
    }
}
