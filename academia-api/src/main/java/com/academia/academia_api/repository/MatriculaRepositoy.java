package com.academia.academia_api.repository;

import com.academia.academia_api.entity.Matricula;
import com.academia.academia_api.entity.enums.MatriculaStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatriculaRepositoy extends JpaRepository<Matricula, Long> {

    Matricula findByAlunoIdAndAtiva(Long alunoId, boolean ativa);
    boolean existsByAlunoIdAndAtivaTrue(Long alunoId);
    Optional<Matricula> findByAlunoUsuarioIdAndAtivaTrue(Long usuarioId);
    Matricula findByMatricula(Long matriculaId, boolean novoStatus);

    @Query("select m.matricula from Matricula m where m.status in :status and m.dataVencimento <= :data")
    List<Long> findIdsVencidos(@Param("status") Collection<MatriculaStatusEnum> status,
                               @Param("data") LocalDate data);

    @Query("select m.matricula from Matricula m where m.status = :status and m.dataVencimento < :limite")
    List<Long> findIdsComVencimentoAntesDe(@Param("status") MatriculaStatusEnum status,
                                           @Param("limite") LocalDate limite);

    @Query("select m.matricula from Matricula m where m.status = :status and m.dataBloqueio <= :limite")
    List<Long> findIdsBloqueadosAte(@Param("status") MatriculaStatusEnum status,
                                    @Param("limite") LocalDate limite);

    Optional<Matricula> findFirstByAlunoUsuarioIdOrderByMatriculaDesc(Long usuarioId);

    boolean existsByAlunoIdAndStatusIn(Long alunoId, Collection<MatriculaStatusEnum> status);
}
