package com.academia.academia_api.entity;

import com.academia.academia_api.entity.enums.GrupoMuscular;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "exercicios")
@Getter
@Setter
public class Exercicio extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Enumerated(EnumType.STRING)
    private GrupoMuscular grupoMuscular;

    private String descricao;
}
