package com.academia.academia_api.controllers;
import com.academia.academia_api.DTOs.*;
import com.academia.academia_api.entity.Usuarios;
import com.academia.academia_api.infra.security.TokenService;
import com.academia.academia_api.repository.UsuarioRepository;
import com.academia.academia_api.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Autenticação",
        description = "Endpoints responsáveis pela autenticação e gerenciamento de usuários."
)
@RestController
@RequestMapping("/api/auth")
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

    @Operation(
            summary = "Realizar Login",
            description = "Autentica um usuário e retorna um JWT válido."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Login ou senha inválidos")
    })
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

    @Operation(
            summary = "Registrar Aluno",
            description = "Cria uma nova conta para um aluno."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Aluno criado"),
            @ApiResponse(responseCode = "409", description = "Login já existente")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody @Valid RegisterAlunoDTO dto) {

        authService.registerAluno(dto);

        return ResponseEntity.status(201).build();
    }

    @Operation(
            summary = "Cadastrar Personal",
            description = "Cria um Personal Trainer. Apenas SUPER_ADMIN."
    )
    @PostMapping("/personais")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createPersonal(
            @RequestBody RegisterPersonalDTO dto) {

        authService.registerPersonal(dto);

        return ResponseEntity.status(201).build();
    }

    @Operation(
            summary = "Cadastrar Administrador",
            description = "Cria um Administrador. Apenas SUPER_ADMIN."
    )
    @PostMapping("/admins")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createAdmin(
            @RequestBody RegisterAdminDTO dto) {

        authService.registerAdmin(dto);

        return ResponseEntity.status(201).build();
    }

    @Operation(
            summary = "Usuário autenticado",
            description = "Retorna as informações do usuário autenticado."
    )
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> me() {

        Usuarios usuario =
                (Usuarios) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        return ResponseEntity.ok(
                new UsuarioResponseDTO(
                        usuario.getId(),
                        usuario.getLogin(),
                        usuario.getRole(),
                        usuario.getAtivo()
                )
        );
    }
}
