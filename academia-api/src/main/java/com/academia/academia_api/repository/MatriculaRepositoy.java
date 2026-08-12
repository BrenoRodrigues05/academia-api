package com.academia.academia_api.repository;

import com.academia.academia_api.entity.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MatriculaRepositoy extends JpaRepository<Matricula, Long> {

    Matricula findByAlunoIdAndAtiva(Long alunoId, boolean ativa);
    boolean existsByAlunoIdAndAtivaTrue(Long alunoId);
    Optional<Matricula> findByAlunoUsuarioIdAndAtivaTrue(Long usuarioId);
    Matricula findByMatricula(Long matriculaId, boolean novoStatus);
}
