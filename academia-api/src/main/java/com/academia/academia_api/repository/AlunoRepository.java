package com.academia.academia_api.repository;

import com.academia.academia_api.entity.Aluno;
import com.academia.academia_api.entity.enums.SexoEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    @Query("SELECT a FROM Aluno a WHERE YEAR(CURRENT_DATE) - YEAR(a.dataNascimento) = :idade")
    List<Aluno> findByIdadeCustom(@Param("idade") int idade);
    List<Aluno> findByNome(String nome);
    Page<Aluno> findBySexo(SexoEnum sexo, Pageable pageable);
    Aluno findByEmail(String email);
}