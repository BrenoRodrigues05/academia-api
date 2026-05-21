package com.academia.academia_api.repository;
import com.academia.academia_api.entity.Plano;
import com.academia.academia_api.entity.enums.TipoPlano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

@Repository
public interface PlanoRepository extends JpaRepository<Plano, Long> {

    Plano findByNome(String nome);
    Plano findByDescricao(String descricao);
    Plano findByTipo(TipoPlano tipo);
    Plano findByValor(BigDecimal valor);
}
