package com.academia.academia_api.entity;

import com.academia.academia_api.entity.enums.StatusPagamentoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pagamentos")
public class Pagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamentoEnum status;
    private String gatewayId;
    @Column(columnDefinition = "TEXT")
    private String codigoPix;
    @Column(columnDefinition = "TEXT")
    private String qrCodeBase64;
    private LocalDateTime dataCriacao;
    private LocalDateTime pagamento;
    private LocalDateTime expiracao;
}
