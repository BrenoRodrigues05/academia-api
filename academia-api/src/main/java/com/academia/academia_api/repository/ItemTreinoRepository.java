package com.academia.academia_api.repository;

import com.academia.academia_api.entity.ItemTreino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemTreinoRepository extends JpaRepository<ItemTreino, Long> {

    List<ItemTreino> findByTreinoId(Long treinoId);

    List<ItemTreino> findByExercicioId(Long exercicioId);

    @Modifying
    void deleteByTreinoId(Long treinoId);

}