package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.*;
import com.academia.academia_api.entity.Aluno;
import com.academia.academia_api.entity.Personal;
import com.academia.academia_api.entity.Usuarios;
import com.academia.academia_api.entity.enums.UserRole;
import com.academia.academia_api.infra.exceptions.BadRequestException;
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
    private final MatriculaService matriculaService;
    private final AlunoRepository alunoRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UsuarioRepository usuarioRepository,
            AlunoRepository alunoRepository,
            PasswordEncoder passwordEncoder,
            PersonalRepository personalRepository, MatriculaService matriculaService) {

        this.usuarioRepository = usuarioRepository;
        this.alunoRepository = alunoRepository;
        this.passwordEncoder = passwordEncoder;
        this.personalRepository = personalRepository;
        this.matriculaService = matriculaService;
    }

    @Transactional
    public MatriculaResponseDTO registerAluno(RegisterAlunoDTO dto) {

        if (usuarioRepository.existsByLogin(dto.login())) {
            throw new BadRequestException("Login já utilizado.");
        }

        if (alunoRepository.existsByEmailIgnoreCase(dto.email())) {
            throw new BadRequestException("O e-mail informado já está cadastrado.");
        }

        Usuarios usuario = new Usuarios();
        usuario.setLogin(dto.login());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setRole(UserRole.ALUNO);
        usuario.setAtivo(false);

        usuario = usuarioRepository.save(usuario);

        Aluno aluno = new Aluno();
        aluno.setNome(dto.nome());
        aluno.setEmail(dto.email());
        aluno.setTelefone(dto.telefone());
        aluno.setSexo(dto.sexo());
        aluno.setDataNascimento(dto.dataNascimento());
        aluno.setUsuario(usuario);

        aluno = alunoRepository.save(aluno);

        MatriculaCreateDTO matriculaCreateDTO = new MatriculaCreateDTO();
        matriculaCreateDTO.setAlunoId(aluno.getId());
        matriculaCreateDTO.setPlanoId(dto.planoId());
        matriculaCreateDTO.setAtiva(false);

        return matriculaService.criarMatricula(matriculaCreateDTO);
    }

    @Transactional
    public void registerPersonal(RegisterPersonalDTO dto) {

        if (usuarioRepository.existsByLogin(dto.login())) {
            throw new BadRequestException("Login já utilizado.");
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
            throw new BadRequestException("Login já utilizado.");
        }

        Usuarios usuario = new Usuarios();

        usuario.setLogin(dto.login());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setRole(UserRole.ADMIN);

        usuarioRepository.save(usuario);
    }
}
