package com.academia.academia_api.repository;

import com.academia.academia_api.entity.Matricula;
import com.academia.academia_api.entity.Pagamento;
import com.academia.academia_api.entity.enums.StatusPagamentoEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    Optional<Pagamento> findByGatewayId(String gatewayId);
    List<Pagamento> findByMatriculaMatriculaAndStatus(Long matricula, StatusPagamentoEnum status);
    Optional<Matricula> findFirstByMatriculaMatriculaAndStatusOrderByMatriculaDesc(Long matricula, StatusPagamentoEnum status);
    Optional<Pagamento> findFirstByMatriculaMatriculaAndStatusOrderByDataCriacaoDesc(Long matricula, StatusPagamentoEnum status);
}
