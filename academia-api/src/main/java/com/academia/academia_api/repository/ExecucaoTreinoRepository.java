package com.academia.academia_api.repository;

import com.academia.academia_api.entity.ExecucaoTreino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExecucaoTreinoRepository extends JpaRepository<ExecucaoTreino, Long> {

    List<ExecucaoTreino> findByAlunoIdOrderByDataExecucaoDesc(Long alunoId);

    List<ExecucaoTreino> findByTreinoIdOrderByDataExecucaoDesc(Long treinoId);
}
