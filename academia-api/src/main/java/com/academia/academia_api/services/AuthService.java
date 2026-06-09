package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.RegisterAlunoDTO;
import com.academia.academia_api.entity.Aluno;
import com.academia.academia_api.entity.Usuarios;
import com.academia.academia_api.entity.enums.UserRole;
import com.academia.academia_api.repository.AlunoRepository;
import com.academia.academia_api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final AlunoRepository alunoRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UsuarioRepository usuarioRepository,
            AlunoRepository alunoRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.alunoRepository = alunoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerAluno(RegisterAlunoDTO dto) {

        if(usuarioRepository.existsByLogin(dto.login())) {
            throw new RuntimeException("Login já utilizado.");
        }

        Usuarios usuario = new Usuarios();

        usuario.setLogin(dto.login());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setRole(UserRole.ALUNO);
        usuario.setAtivo(true);

        usuario = usuarioRepository.save(usuario);

        Aluno aluno = new Aluno();

        aluno.setNome(dto.nome());
        aluno.setEmail(dto.email());
        aluno.setTelefone(dto.telefone());
        aluno.setSexo(dto.sexo());
        aluno.setDataNascimento(dto.dataNascimento());
        aluno.setUsuario(usuario);

        alunoRepository.save(aluno);
    }
}
