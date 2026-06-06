package com.academia.academia_api.repository;

import com.academia.academia_api.entity.Exercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExercicioRepository extends JpaRepository<Exercicio, Long> {

    List<Exercicio> findByNomeContainingIgnoreCase(String nome);

    List<Exercicio> findByGrupoMuscularIgnoreCase(String grupoMuscular);

    boolean existsByNomeIgnoreCase(String nome);
}