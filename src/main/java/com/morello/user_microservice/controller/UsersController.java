package com.morello.user_microservice.controller;

import com.morello.user_microservice.entities.Users;
import com.morello.user_microservice.response.UsersResponse;
import com.morello.user_microservice.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @PostMapping("/login")
    public ResponseEntity<UsersResponse> signIn(@RequestParam String username, @RequestParam String password) {
        Optional<Users> user = usersService.authenticateUser(username, password);

        if(user.isPresent()) {
            Users auth = user.get();
            UsersResponse res = new UsersResponse(auth.getId(), auth.getUsername(), auth.getPassword(), auth.getRole());
            return ResponseEntity.ok(res);
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<UsersResponse> signup(@RequestParam String username, @RequestParam String password) {
        if(usersService.isUsernameValid(username)) {
            Users user = new Users(username, password, "Client");
            UsersResponse response = new UsersResponse(user.getId(), user.getUsername(), user.getPassword(), user.getRole());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsersResponse> getUser(@PathVariable Integer id) {
        Optional<Users> user = usersService.getUserByID(id);
        if (user.isPresent()) {
            Users auth = user.get();
            UsersResponse res = new UsersResponse(auth.getId(), auth.getUsername(), auth.getPassword(), auth.getRole());
            return ResponseEntity.ok(res);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<Void> changePasswordAsClient(@PathVariable Integer id, @RequestParam String oldPass, @RequestParam String newPass) {
        Optional<Users> user = usersService.getUserByID(id);

        if (user.isPresent()) {
            usersService.changePassword(user.get(), oldPass, newPass);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<UsersResponse> getUserByUsername(@PathVariable String username) {
        Optional<Users> user = usersService.getUserByName(username);
        if (user.isPresent()) {
            Users auth = user.get();
            UsersResponse res = new UsersResponse(auth.getId(), auth.getUsername(), auth.getPassword(), auth.getRole());
            return ResponseEntity.ok(res);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity<UsersResponse> updateUserAdmin(@PathVariable Integer id, @RequestBody Users user) {
        Optional<Users> existingUser = usersService.getUserByID(id);

        if (existingUser.isPresent()) {
            Users updatedUser = existingUser.get();
            updatedUser.setUsername(user.getUsername());
            updatedUser.setPassword(user.getPassword());
            updatedUser.setRole(user.getRole());
            usersService.updateUser(id, updatedUser);
            UsersResponse response = new UsersResponse(updatedUser.getId(), updatedUser.getUsername(), updatedUser.getPassword(), updatedUser.getRole());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        Optional<Users> user = usersService.getUserByID(id);

        if (user.isPresent()) {
            usersService.deleteUser(user.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{username}/replace-username")
    public ResponseEntity<Void> replaceUsername(@PathVariable String oldUsername, @RequestParam String newUsername) {
        Optional<Users> user = usersService.getUserByName(oldUsername);

        if (user.isPresent()) {
            usersService.replaceUsername(user.get(), oldUsername, newUsername);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
