package com.academia.academia_api.entity;

import com.academia.academia_api.entity.enums.SexoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "alunos")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long  id;
   private String nome;
   private String email;
   private int idade;
   private String telefone;
    @Enumerated(EnumType.STRING)
   private SexoEnum sexo;
}
