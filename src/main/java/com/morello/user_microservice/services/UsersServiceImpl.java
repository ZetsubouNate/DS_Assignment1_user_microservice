package com.morello.user_microservice.services;

import com.morello.user_microservice.entities.Users;
import com.morello.user_microservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UserRepository userRepository;

    @Override
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<Users> getUserByID(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<Users> getUserByName(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void createUser(Users user) {
        userRepository.save(user);
    }

    @Override
    public void updateUser(Integer id, Users user) {
        Optional<Users> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            userRepository.save(user);
        }
    }

    @Override
    public void deleteUser(Users user) {
        userRepository.delete(user);
    }

    @Override
    public boolean isUsernameValid(String username) {
        //username should not be already existing
        Optional<Users> existingUser = userRepository.findByUsername(username);
        return existingUser.isEmpty();
    }

    @Override
    public void changePassword(Users user, String oldPass, String newPass) {
        if (user.getPassword().equals(oldPass)) {
            user.setPassword(newPass);
            userRepository.save(user);
        }
    }

    @Override
    public void replaceUsername(Users user, String oldUsername, String newUsername) {
        if (user.getUsername().equals(oldUsername)) {
            user.setUsername(newUsername);
            userRepository.save(user);
        }
    }

    @Override
    public Optional<Users> authenticateUser(String username, String password) {
        Optional<Users> user = userRepository.findByUsername(username);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user;
        }
        return Optional.empty();
    }
}
