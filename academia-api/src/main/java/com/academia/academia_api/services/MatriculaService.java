package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.MatriculaCreateDTO;
import com.academia.academia_api.DTOs.MatriculaResponseDTO;
import com.academia.academia_api.DTOs.PageResponseDTO;
import com.academia.academia_api.entity.Aluno;
import com.academia.academia_api.entity.Matricula;
import com.academia.academia_api.entity.Plano;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.infra.exceptions.ResourceNotFoundException;
import com.academia.academia_api.mappings.MatriculaMapper;
import com.academia.academia_api.repository.AlunoRepository;
import com.academia.academia_api.repository.MatriculaRepositoy;
import com.academia.academia_api.repository.PlanoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MatriculaService {

    private final MatriculaRepositoy matriculaRepositoy;
    private final AlunoRepository alunoRepository;
    private final PlanoRepository planoRepository;
    private final MatriculaMapper matriculaMapper;

    public MatriculaService(MatriculaRepositoy matriculaRepositoy, AlunoRepository alunoRepository,
                            PlanoRepository planoRepository, MatriculaMapper matriculaMapper) {
        this.matriculaRepositoy = matriculaRepositoy;
        this.alunoRepository = alunoRepository;
        this.planoRepository = planoRepository;
        this.matriculaMapper = matriculaMapper;
    }

    public PageResponseDTO<MatriculaResponseDTO> listarMatriculas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Matricula> matriculas = matriculaRepositoy.findAll(pageable);

        return new PageResponseDTO<>(
                matriculas.getContent().stream().map(matriculaMapper::toResponseDTO).toList(),
                matriculas.getNumber(),
                matriculas.getSize(),
                matriculas.getTotalElements(),
                matriculas.getTotalPages()
        );
    }

    public MatriculaResponseDTO findById(Long idMatricula) {
        if (idMatricula == null || idMatricula <= 0) {
            throw new BadRequestException("O ID da matrícula informado é inválido.");
        }

        Matricula matricula = matriculaRepositoy.findById(idMatricula)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada com o ID: " + idMatricula));

        return matriculaMapper.toResponseDTO(matricula);
    }

    public MatriculaResponseDTO criarMatricula(MatriculaCreateDTO dto) {
        Long idAluno = dto.getAlunoId();
        Long idPlano = dto.getPlanoId();

        if (idAluno == null || idAluno <= 0 || idPlano == null || idPlano <= 0) {
            throw new BadRequestException("Os IDs de aluno e plano devem ser válidos e maiores que zero.");
        }

        Aluno aluno = alunoRepository.findById(idAluno)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com o ID: " + idAluno));

        var matriculaAtivaExistente = matriculaRepositoy.findByAlunoIdAndAtiva(idAluno, true);
        if (matriculaAtivaExistente != null) {
            throw new BadRequestException("O aluno informado já possui uma matrícula ativa.");
        }

        Plano plano = planoRepository.findById(idPlano)
                .orElseThrow(() -> new ResourceNotFoundException("Plano não encontrado com o ID: " + idPlano));

        Matricula novaMatricula = matriculaMapper.toEntity(dto);
        novaMatricula.setAluno(aluno);
        novaMatricula.setPlano(plano);
        novaMatricula.setAtiva(dto.isAtiva());

        Matricula salva = matriculaRepositoy.save(novaMatricula);
        return matriculaMapper.toResponseDTO(salva);
    }

    public  MatriculaResponseDTO editarPlanoMatricula(Long idMatricula, Long idPlano){
        if (idMatricula == null || idMatricula <= 0) {
            throw new BadRequestException("O ID da matrícula informado é inválido.");
        }

        Matricula matricula = matriculaRepositoy.findById(idMatricula)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada com o ID: " + idMatricula));

        Plano plano = planoRepository.findById(idPlano)
                .orElseThrow(() -> new ResourceNotFoundException("Plano não encontrado com o ID: " + idPlano));

        matricula.setPlano(plano);
        matriculaRepositoy.save(matricula);

        return matriculaMapper.toResponseDTO(matricula);
    }

    public MatriculaResponseDTO alterarStatus(Long idMatricula, boolean novoStatus) {
        if (idMatricula == null || idMatricula <= 0) {
            throw new BadRequestException("O ID da matrícula informado é inválido.");
        }

        var buscaMatricula = matriculaRepositoy.findByMatricula(idMatricula, novoStatus);
        if (buscaMatricula == null || novoStatus == buscaMatricula.isAtiva() ) {
            throw new ResourceNotFoundException("Matrícula não encontrada ou já possui o status selecionado.");
        }

        buscaMatricula.setAtiva(novoStatus);
        Matricula matriculaAtualizada = matriculaRepositoy.save(buscaMatricula);

        return matriculaMapper.toResponseDTO(matriculaAtualizada);
    }
}