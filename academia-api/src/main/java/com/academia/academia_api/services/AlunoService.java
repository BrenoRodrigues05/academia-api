package com.academia.academia_api.services;

import com.academia.academia_api.entity.Aluno;
import com.academia.academia_api.repository.AlunoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;

    public  AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    public List<Aluno> listarAlunos(){
        return alunoRepository.findAll();
    }

    public Aluno fIndByid(Long id){
       if(id==null){
           return null;
       }
        return alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado."));
    }

    public Aluno addAluno(Aluno aluno){
        return alunoRepository.save(aluno);
    }

    public Aluno updateAluno(Aluno aluno){
        return alunoRepository.save(aluno);
    }

    public Aluno deleteAluno(Long id){
        if (id == null) {
            return null;
        }

        Aluno alunoDeletado = alunoRepository.findById(id).orElse(null);

        if (alunoDeletado != null) {
            alunoRepository.deleteById(id);
        }

        return alunoDeletado;
    }
}
