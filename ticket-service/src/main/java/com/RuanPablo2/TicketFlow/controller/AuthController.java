package com.RuanPablo2.TicketFlow.controller;

import com.RuanPablo2.TicketFlow.dtos.request.*;
import com.RuanPablo2.TicketFlow.dtos.response.LoginResponseDTO;
import com.RuanPablo2.TicketFlow.entity.User;
import com.RuanPablo2.TicketFlow.security.TokenService;
import com.RuanPablo2.TicketFlow.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação & Identidade", description = "Endpoints para login, geração de tokens JWT e gestão de usuários do sistema")
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
    @Operation(summary = "Realiza o Login", description = "Autentica o usuário (Cliente, Suporte ou Admin) e devolve o Token JWT necessário para as rotas protegidas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login bem-sucedido. Retorna o JWT."),
            @ApiResponse(responseCode = "403", description = "Credenciais inválidas (E-mail ou senha incorretos)")
    })
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    @Operation(summary = "Cadastro de Cliente", description = "Cria uma nova conta pública. O usuário recebe automaticamente a role CLIENT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conta de cliente criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação (ex: e-mail já em uso ou senha fraca)")
    })
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO data) {
        authService.registerClient(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/admin/register-staff")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastro de Equipe (Staff)", description = "Rota exclusiva para Administradores criarem novas contas para agentes de Suporte ou novos Admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conta de funcionário criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação de dados"),
            @ApiResponse(responseCode = "403", description = "Acesso negado: Apenas ADMIN pode acessar esta rota")
    })
    public ResponseEntity<Void> registerStaff(@RequestBody @Valid StaffRegisterDTO data) {
        authService.registerStaff(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Solicitar recuperação de senha", description = "Gera um token temporário e dispara evento para envio de e-mail via RabbitMQ.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação processada com sucesso (retorna 200 mesmo se o e-mail não existir por segurança)")
    })
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordDTO data) {
        authService.requestPasswordReset(data.email());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Redefinir senha", description = "Valida o token temporário JWT e atualiza a senha do usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Token inválido, expirado ou usuário não encontrado")
    })
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordDTO data) {
        try {
            authService.resetPassword(data.token(), data.newPassword());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}