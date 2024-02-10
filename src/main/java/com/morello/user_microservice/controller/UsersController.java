package com.morello.user_microservice.controller;

import com.morello.user_microservice.entities.Users;
import com.morello.user_microservice.security.AuthenticationResponse;
import com.morello.user_microservice.security.JwtTokenUtil;
import com.morello.user_microservice.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;
    private final RestTemplate restTemplate;
    private final JwtTokenUtil jwtTokenUtil;

    @GetMapping("/all")
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = usersService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponse> signIn(@RequestParam String username, @RequestParam String password) {
        Optional<Users> user = usersService.authenticateUser(username, password);

        if (user.isPresent()) {
            String token = jwtTokenUtil.generateToken(user.get());
            AuthenticationResponse response = new AuthenticationResponse(token);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<Users> signup(@RequestParam String username, @RequestParam String password) {
        if (usersService.isUsernameValid(username)) {
            Users user = new Users(username, password, "Client");
            usersService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @PostMapping("/admin/users")
    public ResponseEntity<Users> addUser(@RequestBody Users user) {
        if (usersService.isUsernameValid(user.getUsername())) {
            usersService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @GetMapping("/id")
    public ResponseEntity<Users> getUser(@RequestParam Integer id) {
        Optional<Users> user = usersService.getUserByID(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
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

    @GetMapping("/find")
    public ResponseEntity<Users> getUserByUsername(@RequestParam String username) {
        Optional<Users> user = usersService.getUserByName(username);
        if (user.isPresent()) {
            Users auth = user.get();
            return ResponseEntity.ok(auth);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Users> updateUserAdmin(@PathVariable Integer id, @RequestBody Users user) {
        Optional<Users> existingUser = usersService.getUserByID(id);

        if (existingUser.isPresent()) {
            Users updatedUser = existingUser.get();
            updatedUser.setUsername(user.getUsername());
            updatedUser.setPassword(user.getPassword());
            updatedUser.setRole(user.getRole());
            usersService.updateUser(id, updatedUser);
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(@RequestParam String username) {
        Optional<Users> user = usersService.getUserByName(username);

        if (user.isPresent()) {
            usersService.deleteUser(user.get());
            restTemplate.exchange(
                    "http://localhost:8081/api/devices/update-username?username={username}",
                    HttpMethod.PUT,
                    null,
                    Void.class,
                    username
            );
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{username}/replace-username")
    public ResponseEntity<Void> replaceUsername(@PathVariable String username, @RequestParam String newUsername) {
        Optional<Users> user = usersService.getUserByName(username);

        if (user.isPresent()) {
            usersService.replaceUsername(user.get(), username, newUsername);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
