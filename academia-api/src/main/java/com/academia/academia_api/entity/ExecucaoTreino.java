package com.academia.academia_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "execucoes_treino")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecucaoTreino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treino_id", nullable = false)
    private Treino treino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @Column(nullable = false)
    private LocalDateTime dataExecucao;

    @Column(nullable = false)
    private Boolean concluido;

    @Column(length = 500)
    private String observacoes;
}
