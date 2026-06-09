package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.RegisterAdminDTO;
import com.academia.academia_api.DTOs.RegisterAlunoDTO;
import com.academia.academia_api.DTOs.RegisterPersonalDTO;
import com.academia.academia_api.entity.Aluno;
import com.academia.academia_api.entity.Personal;
import com.academia.academia_api.entity.Usuarios;
import com.academia.academia_api.entity.enums.UserRole;
import com.academia.academia_api.repository.AlunoRepository;
import com.academia.academia_api.repository.PersonalRepository;
import com.academia.academia_api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PersonalRepository personalRepository;
    private final AlunoRepository alunoRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UsuarioRepository usuarioRepository,
            AlunoRepository alunoRepository,
            PasswordEncoder passwordEncoder,
            PersonalRepository personalRepository) {

        this.usuarioRepository = usuarioRepository;
        this.alunoRepository = alunoRepository;
        this.passwordEncoder = passwordEncoder;
        this.personalRepository = personalRepository;
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

    @Transactional
    public void registerPersonal(RegisterPersonalDTO dto) {

        if (usuarioRepository.existsByLogin(dto.login())) {
            throw new RuntimeException("Login já utilizado.");
        }

        Usuarios usuario = new Usuarios();

        usuario.setLogin(dto.login());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setRole(UserRole.PERSONAL);

        usuario = usuarioRepository.save(usuario);

        Personal personal = new Personal();

        personal.setNome(dto.nome());
        personal.setEmail(dto.email());
        personal.setTelefone(dto.telefone());
        personal.setCref(dto.cref());

        personal.setUsuario(usuario);

        personalRepository.save(personal);
    }

    @Transactional
    public void registerAdmin(RegisterAdminDTO dto) {

        if (usuarioRepository.existsByLogin(dto.login())) {
            throw new RuntimeException("Login já utilizado.");
        }

        Usuarios usuario = new Usuarios();

        usuario.setLogin(dto.login());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setRole(UserRole.ADMIN);

        usuarioRepository.save(usuario);
    }
}
