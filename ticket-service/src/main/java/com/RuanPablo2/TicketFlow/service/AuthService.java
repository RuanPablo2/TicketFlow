package com.RuanPablo2.TicketFlow.service;

import com.RuanPablo2.TicketFlow.config.RabbitMQConfig;
import com.RuanPablo2.TicketFlow.dtos.request.RegisterDTO;
import com.RuanPablo2.TicketFlow.dtos.request.StaffRegisterDTO;
import com.RuanPablo2.TicketFlow.entity.User;
import com.RuanPablo2.TicketFlow.entity.enums.Role;
import com.RuanPablo2.TicketFlow.events.PasswordResetRequestedEvent;
import com.RuanPablo2.TicketFlow.repository.UserRepository;
import com.RuanPablo2.TicketFlow.security.TokenService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final RabbitTemplate rabbitTemplate;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService, RabbitTemplate rabbitTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.rabbitTemplate = rabbitTemplate;
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

    public void requestPasswordReset(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = tokenService.generatePasswordResetToken(email);

            PasswordResetRequestedEvent event = new PasswordResetRequestedEvent(
                    user.getEmail(),
                    user.getName(),
                    token
            );

            System.out.println("Sending password reset event to RabbitMQ: " + email);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.TICKET_EXCHANGE,
                    RabbitMQConfig.PASSWORD_RESET_ROUTING_KEY,
                    event
            );
        }
    }

    public void resetPassword(String token, String newPassword) {
        String email = tokenService.validatePasswordResetToken(token);

        if (email == null) {
            throw new IllegalArgumentException("Invalid or expired token.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}