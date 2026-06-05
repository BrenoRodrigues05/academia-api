package com.academia.academia_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "treinos")
public class Treino {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String observacoes;

    private Boolean ativo = true;

    @ManyToOne
    @JoinColumn(name = "personal_id")
    private Personal personal;

    @OneToOne
    @JoinColumn(name = "aluno_id", unique = true)
    private Aluno aluno;
}
