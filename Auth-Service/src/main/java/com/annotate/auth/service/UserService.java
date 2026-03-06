package com.annotate.auth.service;


import com.annotate.auth.dto.RegisterRequest;
import com.annotate.auth.entity.User;
import com.annotate.auth.repository.UserRepository;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//@Service
//@RequiredArgsConstructor
//public class UserService {
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Transactional
//    public User registerUser(RegisterRequest request) {
//        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
//            throw new RuntimeException("Email already exists");
//        }
//
//        User user = User.builder()
//                .email(request.getEmail())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .name(request.getName())
//                .role(request.getRole())
//                .isActive(true)
//                .build();
//
////        if (request.getTeamId() != null) {
////            Team team = teamRepository.findById(request.getTeamId())
////                    .orElseThrow(() -> new RuntimeException("Team not found"));
////            user.setTeam(team);
////        }
//
//        return userRepository.save(user);
//    }
//
//    public User getUserById(Long id) {
//        return userRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//    }
//
//    public List<User> getUsersByTeam(Long teamId) {
//        return userRepository.findByTeamId(teamId);
//    }
//
//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }
//}


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(request.getRole())
                .teamId(request.getTeamId()) // just store ID
                .isActive(true)
                .build();

        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getUsersByTeam(Long teamId) {
        return userRepository.findByTeamId(teamId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}