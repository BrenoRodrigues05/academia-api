package com.academia.academia_api.repository;

import com.academia.academia_api.entity.Pedido;
import com.academia.academia_api.entity.enums.StatusPagamentoEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByAlunoId(Long alunoId);
    List<Pedido> findByStatus(StatusPagamentoEnum status);
}
