package com.academia.academia_api.services;

import com.academia.academia_api.entity.Aluno;
import com.academia.academia_api.entity.enums.SexoEnum;
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

    public List<Aluno> findByNome(String nome){
        if(nome == null || nome.isEmpty()){
            throw new RuntimeException("Nome inválido.");
        }
        List<Aluno> buscaAlunos = alunoRepository.findByNome(nome);
        if(buscaAlunos.isEmpty()){
            throw new RuntimeException("Nenhum aluno encontrado com esse nome.");
        }
        return buscaAlunos;
    }

    public Aluno findByEmail(String email){
        if(email==null || email.isEmpty()){
            throw new RuntimeException("E-mail nulo ou vazio.");
        }
        var  buscaAluno = alunoRepository.findByEmail(email);
        if(buscaAluno==null){
            throw new RuntimeException("Aluno não  encontrado.");
        }
        return buscaAluno;
    }

    public List<Aluno> findBySexo(SexoEnum sexo){
        if(sexo == null){
            throw new RuntimeException("Sexo nulo ou vazio.");
        }
        List<Aluno> buscaAlunos = alunoRepository.findBySexo(sexo);
        if(buscaAlunos.isEmpty()){
            throw new RuntimeException("Nenhum aluno encontrado.");
        }
        return buscaAlunos;
    }

    public List<Aluno> findByIdade(int idade) {
        if (idade <= 0 || idade >= 100) {
            throw new RuntimeException("Idade inválida.");
        }

        List<Aluno> buscaAlunos = alunoRepository.findByIdadeCustom(idade);

        if (buscaAlunos.isEmpty()) {
            throw new RuntimeException("Nenhum aluno com a idade selecionada.");
        }

        return buscaAlunos;
    }
}
