package com.academia.academia_api.services;
import com.academia.academia_api.entity.Matricula;
import com.academia.academia_api.repository.AlunoRepository;
import com.academia.academia_api.repository.MatriculaRepositoy;
import com.academia.academia_api.repository.PlanoRepository;
import jakarta.persistence.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatriculaService {

    private final MatriculaRepositoy matriculaRepositoy;
    private final AlunoRepository alunoRepository;
    private final PlanoRepository planoRepository ;

    public MatriculaService(MatriculaRepositoy matriculaRepositoy, AlunoRepository alunoRepository,
                            PlanoRepository planoRepository) {
        this.matriculaRepositoy = matriculaRepositoy;
        this.alunoRepository = alunoRepository;
        this.planoRepository = planoRepository;
    }

    public List<Matricula> listarMatriculas() {

        return matriculaRepositoy.findAll();
    }

   public Matricula findById(Long idMatricula) {
       if(idMatricula == null || idMatricula <= 0){
           throw new RuntimeException("Id invalido ou inexistente.");
       }

      return matriculaRepositoy.findById(idMatricula)
               .orElseThrow(() -> new RuntimeException("Matricula não encontrada."));
   }

   public Matricula criarMatricula(Matricula matricula, Long idAluno, Long idPlano) {
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
       matricula.setAluno(buscaAluno.get());
       matricula.setPlano(buscaPlano.get());
       matricula.setAtiva(true);
       return  matriculaRepositoy.save(matricula);
   }

   public Matricula desativarMatricula(Long idMatricula) {
        if(idMatricula == null || idMatricula <= 0){
            throw new RuntimeException("Id invalido ou inexistente.");
        }
        var buscaMatricula =  matriculaRepositoy.findByMatriculaAndAtiva(idMatricula, true);

        if(buscaMatricula == null){
            throw new RuntimeException("Matricula inexistente ou já inativa.");
        }

        buscaMatricula.setAtiva(false);
        return matriculaRepositoy.save(buscaMatricula);
   }

}
