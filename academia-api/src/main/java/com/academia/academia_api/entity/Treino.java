package com.academia.academia_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "treinos")
@Getter
@Setter
public class Treino extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String observacoes;

    private Boolean ativo = true;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    @ManyToOne
    @JoinColumn(name = "personal_id")
    private Personal personal;

    @OneToOne
    @JoinColumn(name = "aluno_id", unique = true)
    private Aluno aluno;
}
