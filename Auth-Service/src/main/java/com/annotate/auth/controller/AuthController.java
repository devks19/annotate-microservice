package com.annotate.auth.controller;

import com.annotate.auth.dto.AuthResponse;
import com.annotate.auth.dto.LoginRequest;
import com.annotate.auth.dto.RegisterRequest;
import com.annotate.auth.dto.UserValidationResponse;
import com.annotate.auth.entity.User;
import com.annotate.auth.repository.UserRepository;
import com.annotate.auth.security.JwtUtil;
import com.annotate.auth.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

//@RestController
//@RequestMapping("/api/auth")
//@RequiredArgsConstructor
////@CrossOrigin(origins = "*")
//public class AuthController {
//    private final UserService userService;
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtUtil jwtUtil;
//
//    @PostMapping("/register")
//    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
//        User user = userService.registerUser(request);
//        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole().name());
//
//        return ResponseEntity.ok(AuthResponse.builder()
//                .token(token)
//                .userId(user.getId())
//                .email(user.getEmail())
//                .name(user.getName())
//                .role(user.getRole())
//                .teamId(user.getTeam() != null ? user.getTeam().getId() : null)
//                .build());
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
//
//        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//            throw new RuntimeException("Invalid credentials");
//        }
//
//        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole().name());
//
//        return ResponseEntity.ok(AuthResponse.builder()
//                .token(token)
//                .userId(user.getId())
//                .email(user.getEmail())
//                .name(user.getName())
//                .role(user.getRole())
//                .teamId(user.getTeam() != null ? user.getTeam().getId() : null)
//                .build());
//    }
//}

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {

        User user = userService.registerUser(request);

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getId(),
                user.getRole().name()
        );

        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .teamId(user.getTeamId()) // simple Long
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getId(),
                user.getRole().name()
        );

        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .teamId(user.getTeamId())
                .build());
    }
    @GetMapping("/validate")
    public ResponseEntity<UserValidationResponse> validate(@RequestParam Long userId) {

        User user = userService.getUserById(userId);

        return ResponseEntity.ok(
                new UserValidationResponse(
                        user.getId(),
                        user.getRole(),
                        user.getIsActive()
                )
        );
    }
}


