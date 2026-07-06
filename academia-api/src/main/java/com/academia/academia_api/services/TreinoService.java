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
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.infra.exceptions.ForbiddenException;
import com.academia.academia_api.infra.exceptions.ResourceNotFoundException;
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

import java.time.LocalDate;
import java.util.Collections;
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

    public PageResponseDTO<TreinoResponseDTO> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Treino> treinos = treinoRepository.findAll(pageable);

        return new PageResponseDTO<>(
                treinos.getContent().stream().map(treinoMapper::toResponseDTO).toList(),
                treinos.getNumber(),
                treinos.getSize(),
                treinos.getTotalElements(),
                treinos.getTotalPages()
        );
    }

    public TreinoResponseDTO findById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("O ID informado é inválido ou nulo.");
        }
        Treino treino = treinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado com o ID: " + id));

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
        if (nome == null || nome.trim().isEmpty()) {
            throw new BadRequestException("O nome informado para a busca é inválido ou vazio.");
        }

        List<Treino> treinos = treinoRepository.findByNomeContainingIgnoreCase(nome);
        if (treinos.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum treino encontrado com o termo: " + nome);
        }

        return treinos.stream()
                .map(treinoMapper::toResponseDTO)
                .toList();
    }

    public List<TreinoResponseDTO> findByPersonal(Long personalId) {
        if (personalId == null || personalId <= 0) {
            throw new BadRequestException("O ID do personal informado é inválido.");
        }

        if (!personalRepository.existsById(personalId)) {
            throw new ResourceNotFoundException("Personal não encontrado com o ID: " + personalId);
        }

        return treinoRepository.findByPersonalId(personalId).stream()
                .map(treinoMapper::toResponseDTO)
                .toList();
    }

    public TreinoResponseDTO findTreinoAtivoAluno(Long alunoId) {
        if (alunoId == null || alunoId <= 0) {
            throw new BadRequestException("O ID do aluno informado é inválido.");
        }

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com o ID: " + alunoId));

        validarAluno(aluno);

        Treino treino = treinoRepository.findByAlunoIdAndAtivoTrue(alunoId)
                .orElseThrow(() -> new ResourceNotFoundException("O aluno informado não possui treino ativo no momento."));

        return treinoMapper.toResponseDTO(treino);
    }

    public List<TreinoResponseDTO> getMeuHistorico(){
        Usuarios usuario = getUsuarioLogado();

        return treinoRepository
                .findByAlunoUsuarioIdOrderByDataInicioDesc(usuario.getId())
                .stream()
                .map(treinoMapper::toResponseDTO)
                .toList();
    }

    public List<TreinoResponseDTO> historicoAluno(Long alunoId){
        if (alunoId == null || alunoId <= 0) {
            throw new BadRequestException("O ID do aluno informado é inválido.");
        }
        List<Treino> treinos = treinoRepository.findByAlunoIdOrderByDataInicioDesc(alunoId);

        if(treinos == null || treinos.isEmpty()){
            throw new ResourceNotFoundException("Treinos não encontrados para o aluno: " +alunoId+ ".");
        }

        return treinos.stream()
                .map(treinoMapper::toResponseDTO)
                .toList();
     }

     public TreinoResponseDTO getMeutreino(){

         Usuarios usuario = getUsuarioLogado();

         Treino treino = treinoRepository
                 .findByAlunoIdAndAtivoTrue(usuario.getId())
                 .orElseThrow(() ->
                         new ResourceNotFoundException("Nenhum treino ativo encontrado."));

         return treinoMapper.toResponseDTO(treino);
     }

    public TreinoResponseDTO addTreino(@NonNull TreinoCreateDTO dto) {
        Usuarios usuario = getUsuarioLogado();

        if (usuario.getRole() != UserRole.PERSONAL
                && usuario.getRole() != UserRole.ADMIN
                && usuario.getRole() != UserRole.SUPER_ADMIN) {
            throw new ForbiddenException("Somente professores ou administradores podem cadastrar treinos.");
        }

        Personal personal = personalRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Vínculo de Personal não localizado para o usuário logado."));

        Aluno aluno = alunoRepository.findById(dto.getAlunoId())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com o ID: " + dto.getAlunoId()));

        if (treinoRepository.existsByAlunoIdAndAtivoTrue(aluno.getId())) {
            throw new BadRequestException("O aluno já possui um treino ativo no sistema.");
        }

        Treino treino = treinoMapper.toEntity(dto);
        treino.setPersonal(personal);
        treino.setAluno(aluno);
        treino.setAtivo(true);
        treino.setDataInicio(LocalDate.now());
        treino.setDataFim(null);

        Treino treinoSalvo = treinoRepository.save(treino);
        return treinoMapper.toResponseDTO(treinoSalvo);
    }

    public TreinoResponseDTO updateTreino(Long id, TreinoUpdateDTO dto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID inválido ou nulo para atualização.");
        }

        Treino treino = treinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado para atualização com o ID: " + id));

        validarPermissaoTreino(treino);

        treinoMapper.updateEntityFromDTO(dto, treino);
        Treino treinoAtualizado = treinoRepository.save(treino);

        return treinoMapper.toResponseDTO(treinoAtualizado);
    }

    public TreinoResponseDTO alterarStatus(Long id, Boolean ativo) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID inválido ou nulo para alteração de status.");
        }
        if (ativo == null) {
            throw new BadRequestException("O novo status ativo não pode ser nulo.");
        }

        Treino treino = treinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado com o ID: " + id));

        if (treino.getAtivo() != null && treino.getAtivo().equals(ativo)) {
            throw new BadRequestException("O treino já se encontra com o status informado.");
        }

        validarPermissaoTreino(treino);
        if (ativo) {

            treino.setDataInicio(LocalDate.now());
            treino.setDataFim(null);

        } else {

            treino.setDataFim(LocalDate.now());

        }
        treino.setAtivo(ativo);
        treinoRepository.save(treino);

        return treinoMapper.toResponseDTO(treino);
    }

    public TreinoResponseDTO deleteTreino(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID inválido ou nulo para exclusão.");
        }

        Treino treino = treinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado para exclusão com o ID: " + id));

        validarPermissaoTreino(treino);
        treinoRepository.delete(treino);

        return treinoMapper.toResponseDTO(treino);
    }

    private Usuarios getUsuarioLogado() {
        return (Usuarios) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private void validarPermissaoTreino(Treino treino) {
        Usuarios usuario = getUsuarioLogado();

        if (usuario.getRole() == UserRole.SUPER_ADMIN || usuario.getRole() == UserRole.ADMIN) {
            return;
        }

        boolean donoDoTreino = treino.getPersonal() != null
                && treino.getPersonal().getUsuario() != null
                && treino.getPersonal().getUsuario().getId().equals(usuario.getId());

        if (!donoDoTreino) {
            throw new ForbiddenException("Você não possui permissão para alterar ou remover este treino.");
        }
    }

    private void validarAluno(Aluno aluno) {
        Usuarios usuario = getUsuarioLogado();

        if (usuario.getRole() == UserRole.SUPER_ADMIN || usuario.getRole() == UserRole.ADMIN || usuario.getRole() == UserRole.PERSONAL) {
            return;
        }
        if (usuario.getRole() == UserRole.ALUNO && aluno.getId().equals(usuario.getId())) {
            return;
        }

        throw new ForbiddenException("Você não possui permissão para visualizar as informações deste aluno.");
    }
}