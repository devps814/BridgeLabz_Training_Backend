package com.greet.service;

import com.greet.model.User;
import com.greet.repository.UserRepository;
import com.greet.util.HashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
        }

        String hashedPassword = HashUtil.sha256(password);
        return hashedPassword.equals(user.getPassword()) ? user : null;
    }

    @Override
    public boolean register(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return false;
        }

        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("USER");
        }
        user.setPassword(HashUtil.sha256(user.getPassword()));
        return userRepository.save(user);
    }
}
