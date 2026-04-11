package com.RuanPablo2.TicketFlow.service;

import com.RuanPablo2.TicketFlow.dtos.request.RegisterDTO;
import com.RuanPablo2.TicketFlow.dtos.request.StaffRegisterDTO;
import com.RuanPablo2.TicketFlow.entity.User;
import com.RuanPablo2.TicketFlow.entity.enums.Role;
import com.RuanPablo2.TicketFlow.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public void registerClient(RegisterDTO data) {
        if (userRepository.findByEmail(data.email()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        String encryptedPassword = passwordEncoder.encode(data.password());

        User newUser = new User(null, data.name(), data.email(), encryptedPassword, Role.CLIENT);

        userRepository.save(newUser);
    }

    public void registerStaff(StaffRegisterDTO data) {
        if (userRepository.findByEmail(data.email()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        String encryptedPassword = passwordEncoder.encode(data.password());

        User newStaff = new User(null, data.name(), data.email(), encryptedPassword, data.role());

        userRepository.save(newStaff);
    }
}