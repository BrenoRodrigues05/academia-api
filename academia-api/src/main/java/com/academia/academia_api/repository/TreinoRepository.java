package com.academia.academia_api.repository;

import com.academia.academia_api.entity.Treino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TreinoRepository extends JpaRepository<Treino, Long> {
    List<Treino> findByAtivoTrue();

    List<Treino> findByAtivoFalse();

    List<Treino> findByNomeContainingIgnoreCase(String nome);

    List<Treino> findByPersonalId(Long personalId);

    List<Treino> findByAlunoId(Long alunoId);

    Optional<Treino> findByAlunoIdAndAtivoTrue(Long alunoId);

    List<Treino> findByPersonalIdAndAtivoTrue(Long personalId);

    List<Treino> findByAlunoIdOrderByDataInicioDesc(Long alunoId);

    List<Treino> findByAlunoUsuarioIdOrderByDataInicioDesc(Long usuarioId);

    boolean existsByAlunoIdAndAtivoTrue(Long alunoId);
}
