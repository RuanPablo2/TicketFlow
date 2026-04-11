package com.RuanPablo2.TicketFlow.controller;

import com.RuanPablo2.TicketFlow.dtos.request.LoginDTO;
import com.RuanPablo2.TicketFlow.dtos.request.RegisterDTO;
import com.RuanPablo2.TicketFlow.dtos.request.StaffRegisterDTO;
import com.RuanPablo2.TicketFlow.dtos.response.LoginResponseDTO;
import com.RuanPablo2.TicketFlow.entity.User;
import com.RuanPablo2.TicketFlow.repository.UserRepository;
import com.RuanPablo2.TicketFlow.security.TokenService;
import com.RuanPablo2.TicketFlow.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final AuthService authService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService, AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO data) {
        authService.registerClient(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/admin/register-staff")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> registerStaff(@RequestBody @Valid StaffRegisterDTO data) {
        authService.registerStaff(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}