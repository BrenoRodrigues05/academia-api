package com.academia.academia_api.repository;

import com.academia.academia_api.entity.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatriculaRepositoy extends JpaRepository<Matricula, Long> {

    Matricula findByAlunoIdAndAtiva(Long alunoId, boolean ativa);
    Matricula findByMatriculaAndAtiva(Long matriculaId, boolean ativa);
}
