package com.academia.academia_api.repository;

import com.academia.academia_api.entity.Aluno;
import com.academia.academia_api.entity.enums.SexoEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    Aluno findByNome(String nome);
    Aluno findByIdade(int idade);
    Aluno findBySexo(SexoEnum sexo);
    Aluno findByEmail(String email);
}