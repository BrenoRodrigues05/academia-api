package com.academia.academia_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "treino")
    private List<ExecucaoTreino> execucoes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "personal_id")
    private Personal personal;

    @OneToOne
    @JoinColumn(name = "aluno_id", unique = true)
    private Aluno aluno;
}
