package com.example.nasa.service.Impl;


import com.example.nasa.entity.User;
import com.example.nasa.entity.UserAuthenticationDetails;
import com.example.nasa.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserAuthenticationService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserAuthenticationService.class);

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username);

        if(user == null){
            logger.error("User Not Found");
            throw new UsernameNotFoundException("User Not Found");

        }
        return new UserAuthenticationDetails(user);
    }
}