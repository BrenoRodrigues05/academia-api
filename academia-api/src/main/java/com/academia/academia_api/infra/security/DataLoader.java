package com.academia.academia_api.infra.security;

import com.academia.academia_api.entity.Usuarios;
import com.academia.academia_api.entity.enums.UserRole;
import com.academia.academia_api.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!usuarioRepository.existsByLogin("superadmin")) {
            Usuarios superAdmin = new Usuarios();
            superAdmin.setLogin("superadmin");
            superAdmin.setSenha(passwordEncoder.encode("Academia@2026!"));
            superAdmin.setRole(UserRole.SUPER_ADMIN);
            superAdmin.setAtivo(true);

            usuarioRepository.save(superAdmin);
            System.out.println(">>> [DataLoader] Usuário 'superadmin' criado com sucesso!");
        }
    }
}