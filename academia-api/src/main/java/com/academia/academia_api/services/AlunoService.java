package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.AlunoCreateDTO;
import com.academia.academia_api.DTOs.AlunoResponseDTO;
import com.academia.academia_api.DTOs.AlunoUpdateDTO;
import com.academia.academia_api.DTOs.PageResponseDTO;
import com.academia.academia_api.entity.Aluno;
import com.academia.academia_api.entity.Usuarios;
import com.academia.academia_api.entity.enums.SexoEnum;
import com.academia.academia_api.entity.enums.UserRole;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.infra.exceptions.ForbiddenException;
import com.academia.academia_api.infra.exceptions.ResourceNotFoundException;
import com.academia.academia_api.mappings.AlunoMapper;
import com.academia.academia_api.repository.AlunoRepository;
import com.academia.academia_api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final AlunoMapper alunoMapper;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AlunoService(AlunoRepository alunoRepository, AlunoMapper alunoMapper, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.alunoRepository = alunoRepository;
        this.alunoMapper = alunoMapper;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public PageResponseDTO<AlunoResponseDTO> findAll(
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(page, size, Sort.by("nome").ascending());

        Page<Aluno> alunos =
                alunoRepository.findAll(pageable);

        return new PageResponseDTO<>(
                alunos.getContent()
                        .stream()
                        .map(alunoMapper::toResponseDTO)
                        .toList(),

                alunos.getNumber(),
                alunos.getSize(),
                alunos.getTotalElements(),
                alunos.getTotalPages()
        );
    }

    public AlunoResponseDTO findById(Long id) {
        if (id == null) {
            throw new BadRequestException("O ID fornecido não pode ser nulo.");
        }
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado."));

        return alunoMapper.toResponseDTO(aluno);
    }

    @Transactional
    public AlunoResponseDTO addAluno(AlunoCreateDTO createDTO) {

        Usuarios novoUsuario = new Usuarios();
        novoUsuario.setLogin(createDTO.getEmail());

        novoUsuario.setSenha(passwordEncoder.encode("Aluno@123"));
        novoUsuario.setRole(UserRole.ALUNO);
        novoUsuario.setAtivo(true);

        Usuarios usuarioSalvo = usuarioRepository.save(novoUsuario);

        Aluno alunoEntity = alunoMapper.toEntity(createDTO);
        alunoEntity.setUsuario(usuarioSalvo);

        Aluno alunoSalvo = alunoRepository.save(alunoEntity);

        return alunoMapper.toResponseDTO(alunoSalvo);
    }

    public AlunoResponseDTO updateAluno(Long id, AlunoUpdateDTO updateDTO) {
        if (id == null) {
            throw new BadRequestException("ID inválido para atualização.");
        }

        Aluno alunoExistente = alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado para atualização."));

        validarPermissaoAluno(alunoExistente);

        alunoMapper.updateEntityFromDTO(updateDTO, alunoExistente);

        Aluno alunoAtualizado = alunoRepository.save(alunoExistente);

        return alunoMapper.toResponseDTO(alunoAtualizado);
    }

    public AlunoResponseDTO deleteAluno(Long id) {
        if (id == null) {
            throw new BadRequestException("O ID fornecido para exclusão não pode ser nulo.");
        }

        Aluno alunoDeletado = alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado para exclusão."));

        alunoRepository.delete(alunoDeletado);
        return alunoMapper.toResponseDTO(alunoDeletado);
    }

    @Transactional
    public AlunoResponseDTO alternarStatusAluno(Long id, boolean novoStatus) {
        if (id == null) {
            throw new BadRequestException("O ID fornecido não pode ser nulo.");
        }

        Aluno buscaAluno = alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado."));

        if (buscaAluno.getUsuario().getAtivo() == novoStatus) {
            throw new BadRequestException("Aluno já possui o status selecionado.");
        }

        buscaAluno.getUsuario().setAtivo(novoStatus);
        alunoRepository.save(buscaAluno);

        return alunoMapper.toResponseDTO(buscaAluno);
    }

    public List<AlunoResponseDTO> findByNome(String nome) {
        if (nome == null || nome.isEmpty()) {
            throw new BadRequestException("Nome inválido para a busca.");
        }
        List<Aluno> buscaAlunos = alunoRepository.findByNomeContainingIgnoreCase(nome);
        if (buscaAlunos.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum aluno encontrado com esse nome.");
        }

        return buscaAlunos.stream()
                .map(alunoMapper::toResponseDTO)
                .toList();
    }

    public List<AlunoResponseDTO> findByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new BadRequestException("E-mail nulo ou vazio.");
        }
        List<Aluno> buscaAlunos = alunoRepository.findByEmailContainingIgnoreCase(email);
        if (buscaAlunos.isEmpty()) {
            throw new ResourceNotFoundException("Aluno não encontrado.");
        }

        return buscaAlunos.stream()
                .map(alunoMapper::toResponseDTO)
                .toList();
    }

    public PageResponseDTO<AlunoResponseDTO> findBySexo(SexoEnum sexo, int page,  int size) {
        if (sexo == null) {
            throw new BadRequestException("Sexo nulo ou vazio.");
        }
        Pageable pageable =
                PageRequest.of(page, size);

        Page<Aluno> alunos =
                alunoRepository.findBySexo(
                        sexo,
                        pageable
                );

        return new PageResponseDTO<>(
                alunos.getContent()
                        .stream()
                        .map(alunoMapper::toResponseDTO)
                        .toList(),
                    alunos.getNumber(),
                    alunos.getSize(),
                    alunos.getTotalElements(),
                    alunos.getTotalPages()
        );
    }

    public List<AlunoResponseDTO> findByIdade(int idade) {
        if (idade <= 0 || idade >= 100) {
            throw new BadRequestException("Idade inválida.");
        }

        List<Aluno> buscaAlunos = alunoRepository.findByIdadeCustom(idade);

        if (buscaAlunos.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum aluno com a idade selecionada.");
        }

        return buscaAlunos.stream()
                .map(alunoMapper::toResponseDTO)
                .toList();
    }

    private Usuarios getUsuarioLogado() {

        return (Usuarios)
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();
    }

    private void validarPermissaoAluno(Aluno aluno) {

        Usuarios usuario = getUsuarioLogado();

        if (usuario.getRole() == UserRole.SUPER_ADMIN || usuario.getRole() == UserRole.ADMIN) {
            return;
        }
        if (usuario.getRole() == UserRole.ALUNO) {

            if (aluno.getId().equals(usuario.getId())) {
                return;
            }
        }
        throw new ForbiddenException("Você não possui permissão para realizar essa ação.");
    }
}