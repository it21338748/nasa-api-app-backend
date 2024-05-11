package com.example.nasa.service.Impl;

import com.example.nasa.entity.User;
import com.example.nasa.repository.UserRepository;
import com.example.nasa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
