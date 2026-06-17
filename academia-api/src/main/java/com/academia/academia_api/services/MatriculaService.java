package com.academia.academia_api.services;
import com.academia.academia_api.DTOs.MatriculaCreateDTO;
import com.academia.academia_api.DTOs.MatriculaResponseDTO;
import com.academia.academia_api.DTOs.PageResponseDTO;
import com.academia.academia_api.entity.Matricula;
import com.academia.academia_api.mappings.MatriculaMapper;
import com.academia.academia_api.repository.AlunoRepository;
import com.academia.academia_api.repository.MatriculaRepositoy;
import com.academia.academia_api.repository.PlanoRepository;
import jakarta.persistence.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MatriculaService {

    private final MatriculaRepositoy matriculaRepositoy;
    private final AlunoRepository alunoRepository;
    private final PlanoRepository planoRepository ;
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

        Page<Matricula> matriculas =
                matriculaRepositoy.findAll(pageable);

        return new PageResponseDTO<>(
                matriculas.getContent()
                        .stream()
                        .map(matriculaMapper::toResponseDTO)
                        .toList(),

                matriculas.getNumber(),
                matriculas.getSize(),
                matriculas.getTotalElements(),
                matriculas.getTotalPages()
        );
    }

   public MatriculaResponseDTO findById(Long idMatricula) {
       if(idMatricula == null || idMatricula <= 0){
           throw new RuntimeException("Id invalido ou inexistente.");
       }

      Matricula matricula =  matriculaRepositoy.findById(idMatricula)
              .orElseThrow(() -> new RuntimeException("Matricula não encontrada."));
       return matriculaMapper.toResponseDTO(matricula);
   }

   public MatriculaResponseDTO criarMatricula(MatriculaCreateDTO dto, Long idAluno, Long idPlano) {
       if (idAluno == null || idAluno <= 0 || idPlano == null || idPlano <= 0) {
           throw new RuntimeException("Id invalido ou inexistente.");
       }

       var buscaAluno = alunoRepository.findById(idAluno);
       if(buscaAluno.isEmpty()) {
           throw new RuntimeException("Aluno não existente.");
       }

       var matriculaAtivaExistente = matriculaRepositoy.findByAlunoIdAndAtiva(idAluno, true);
       if (matriculaAtivaExistente != null) {
           throw new RuntimeException("Aluno já possui matricula ativa.");
       }
       var buscaPlano = planoRepository.findById(idPlano);

       if(buscaPlano.isEmpty()){
           throw new RuntimeException("Plano selecionado é inválido.");
       }

       Matricula novaMatricula = matriculaMapper.toEntity(dto);

       novaMatricula.setAluno(buscaAluno.get());
       novaMatricula.setPlano(buscaPlano.get());
       novaMatricula.setAtiva(true);

       Matricula salva = matriculaRepositoy.save(novaMatricula);

       return matriculaMapper.toResponseDTO(salva);
   }

   public MatriculaResponseDTO desativarMatricula(Long idMatricula) {
        if(idMatricula == null || idMatricula <= 0){
            throw new RuntimeException("Id invalido ou inexistente.");
        }
        var buscaMatricula =  matriculaRepositoy.findByMatriculaAndAtiva(idMatricula, true);

        if(buscaMatricula == null){
            throw new RuntimeException("Matricula inexistente ou já inativa.");
        }

       buscaMatricula.setAtiva(false);

       Matricula matriculaAtualizada =
               matriculaRepositoy.save(buscaMatricula);

       return matriculaMapper.toResponseDTO(matriculaAtualizada);
   }

}
