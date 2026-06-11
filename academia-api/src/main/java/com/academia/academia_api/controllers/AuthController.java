package com.academia.academia_api.controllers;
import com.academia.academia_api.DTOs.*;
import com.academia.academia_api.entity.Usuarios;
import com.academia.academia_api.infra.security.TokenService;
import com.academia.academia_api.repository.UsuarioRepository;
import com.academia.academia_api.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthService authService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity login (@RequestBody @Valid AuthDTO data) {

        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.senha());
        var authentication = this.authenticationManager.authenticate(usernamePassword);
        var usuario =
                (Usuarios) authentication.getPrincipal();

        String token =
                tokenService.generateToken(usuario);

        return ResponseEntity.ok(
                new LoginResponseDTO(token)
        );
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody @Valid RegisterAlunoDTO dto) {

        authService.registerAluno(dto);

        return ResponseEntity.status(201).build();
    }

    @PostMapping("/personais")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createPersonal(
            @RequestBody RegisterPersonalDTO dto) {

        authService.registerPersonal(dto);

        return ResponseEntity.status(201).build();
    }

    @PostMapping("/admins")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createAdmin(
            @RequestBody RegisterAdminDTO dto) {

        authService.registerAdmin(dto);

        return ResponseEntity.status(201).build();
    }
}
