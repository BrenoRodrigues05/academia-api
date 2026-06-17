package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.PageResponseDTO;
import com.academia.academia_api.DTOs.TreinoCreateDTO;
import com.academia.academia_api.DTOs.TreinoResponseDTO;
import com.academia.academia_api.DTOs.TreinoUpdateDTO;
import com.academia.academia_api.entity.Aluno;
import com.academia.academia_api.entity.Personal;
import com.academia.academia_api.entity.Treino;
import com.academia.academia_api.entity.Usuarios;
import com.academia.academia_api.entity.enums.UserRole;
import com.academia.academia_api.mappings.TreinoMapper;
import com.academia.academia_api.repository.AlunoRepository;
import com.academia.academia_api.repository.PersonalRepository;
import com.academia.academia_api.repository.TreinoRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TreinoService {

    private final TreinoRepository treinoRepository;
    private final TreinoMapper treinoMapper;
    private final PersonalRepository personalRepository;
    private final AlunoRepository alunoRepository;

    public TreinoService(
            TreinoRepository treinoRepository,
            TreinoMapper treinoMapper,
            PersonalRepository personalRepository,
            AlunoRepository alunoRepository) {

        this.treinoRepository = treinoRepository;
        this.treinoMapper = treinoMapper;
        this.personalRepository = personalRepository;
        this.alunoRepository = alunoRepository;
    }

    public PageResponseDTO<TreinoResponseDTO> findAll(int  page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Treino> treinos =
                treinoRepository.findAll(pageable);

        return new PageResponseDTO<>(
                treinos.getContent()
                        .stream()
                        .map(treinoMapper::toResponseDTO)
                        .toList(),

                treinos.getNumber(),
                treinos.getSize(),
                treinos.getTotalElements(),
                treinos.getTotalPages()
        );
    }

    public TreinoResponseDTO findById(Long id) {

        if (id == null || id <= 0) {
            throw new RuntimeException("Id inválido.");
        }
        Treino treino = treinoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Treino não encontrado."));

        validarAluno(treino.getAluno());

        return treinoMapper.toResponseDTO(treino);
    }

    public List<TreinoResponseDTO> findByAtivoTrue() {

        return treinoRepository.findByAtivoTrue().stream()
                .map(treinoMapper::toResponseDTO)
                .toList();
    }

    public List<TreinoResponseDTO> findByAtivoFalse() {

        return treinoRepository.findByAtivoFalse().stream()
                .map(treinoMapper::toResponseDTO)
                .toList();
    }

    public List<TreinoResponseDTO> findByNome(String nome) {

        if (nome == null || nome.isBlank()) {
            throw new RuntimeException("Nome inválido.");
        }

        List<Treino> treinos =
                treinoRepository.findByNomeContainingIgnoreCase(nome);

        if (treinos.isEmpty()) {
            throw new RuntimeException("Nenhum treino encontrado.");
        }

        return treinos.stream()
                .map(treinoMapper::toResponseDTO)
                .toList();
    }

    public List<TreinoResponseDTO> findByPersonal(Long personalId) {

        if (personalId == null || personalId <= 0) {
            throw new RuntimeException("Id do personal inválido.");
        }

        return treinoRepository.findByPersonalId(personalId)
                .stream()
                .map(treinoMapper::toResponseDTO)
                .toList();
    }

    public TreinoResponseDTO findTreinoAtivoAluno(Long alunoId) {

        if (alunoId == null || alunoId <= 0) {
            throw new RuntimeException("Id do aluno inválido.");
        }

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado."));

        validarAluno(aluno);

        Treino treino = treinoRepository
                .findByAlunoIdAndAtivoTrue(alunoId)
                .orElseThrow(() ->
                        new RuntimeException("Aluno não possui treino ativo."));

        return treinoMapper.toResponseDTO(treino);
    }

    public TreinoResponseDTO addTreino(@NonNull TreinoCreateDTO dto) {

        Usuarios usuario = getUsuarioLogado();

        if (usuario.getRole() != UserRole.PERSONAL
                && usuario.getRole() != UserRole.ADMIN
                && usuario.getRole() != UserRole.SUPER_ADMIN) {

            throw new RuntimeException(
                    "Somente personal ou administradores podem criar treinos.");
        }

        Personal personal = personalRepository
                .findByUsuarioId(usuario.getId())
                .orElseThrow(() ->
                        new RuntimeException("Personal não encontrado."));

        Aluno aluno = alunoRepository.findById(dto.getAlunoId())
                .orElseThrow(() ->
                        new RuntimeException("Aluno não encontrado."));

        if (treinoRepository.existsByAlunoIdAndAtivoTrue(aluno.getId())) {
            throw new RuntimeException(
                    "O aluno já possui um treino ativo.");
        }

        Treino treino = treinoMapper.toEntity(dto);

        treino.setPersonal(personal);
        treino.setAluno(aluno);
        treino.setAtivo(true);

        Treino treinoSalvo = treinoRepository.save(treino);

        return treinoMapper.toResponseDTO(treinoSalvo);
    }

    public TreinoResponseDTO updateTreino(
            Long id,
            TreinoUpdateDTO dto) {

        if (id == null || id <= 0) {
            throw new RuntimeException("Id inválido.");
        }

        Treino treino = treinoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Treino não encontrado."));

        validarPermissaoTreino(treino);

        treinoMapper.updateEntityFromDTO(dto, treino);

        Treino treinoAtualizado = treinoRepository.save(treino);

        return treinoMapper.toResponseDTO(treinoAtualizado);
    }

    public TreinoResponseDTO alterarStatus(
            Long id,
            Boolean ativo) {

        if (id == null || id <= 0) {
            throw new RuntimeException("Id inválido.");
        }

        Treino treino = treinoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Treino não encontrado."));

        if (treino.getAtivo().equals(ativo)) {
            throw new RuntimeException(
                    "O treino já está com esse status.");
        }

        validarPermissaoTreino(treino);
        treino.setAtivo(ativo);

        treinoRepository.save(treino);

        return treinoMapper.toResponseDTO(treino);
    }

    public TreinoResponseDTO deleteTreino(Long id) {

        if (id == null || id <= 0) {
            throw new RuntimeException("Id inválido.");
        }

        Treino treino = treinoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Treino não encontrado."));

        validarPermissaoTreino(treino);

        treinoRepository.delete(treino);

        return treinoMapper.toResponseDTO(treino);
    }

    private Usuarios getUsuarioLogado() {

        return (Usuarios)
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();
    }

    private void validarPermissaoTreino(Treino treino) {

        Usuarios usuario = getUsuarioLogado();

        if (usuario.getRole() == UserRole.SUPER_ADMIN) {
            return;
        }

        if (usuario.getRole() == UserRole.ADMIN) {
            return;
        }

        boolean donoDoTreino =
                treino.getPersonal()
                        .getUsuario()
                        .getId()
                        .equals(usuario.getId());

        if (!donoDoTreino) {
            throw new RuntimeException(
                    "Você não possui permissão para alterar este treino."
            );
        }
    }

    private void validarAluno(Aluno aluno) {
        Usuarios usuario = getUsuarioLogado();
        if (usuario.getRole() == UserRole.SUPER_ADMIN || usuario.getRole() == UserRole.ADMIN) {
            return;
        }
        if (usuario.getRole() == UserRole.ALUNO && aluno.getId().equals(usuario.getId())) {
            return;
        }

        throw new RuntimeException("Você não possui permissão para visualizar estes treinos.");
    }
}