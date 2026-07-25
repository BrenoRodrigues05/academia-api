package com.academia.academia_api.repository;

import com.academia.academia_api.entity.Exercicio;
import com.academia.academia_api.entity.enums.GrupoMuscular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExercicioRepository extends JpaRepository<Exercicio, Long> {

    List<Exercicio> findByNomeContainingIgnoreCase(String nome);

    List<Exercicio> findByGrupoMuscularIgnoreCase(GrupoMuscular grupoMuscular);

    boolean existsByNomeIgnoreCase(String nome);
}