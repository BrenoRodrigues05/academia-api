package com.academia.academia_api.entity;

import com.academia.academia_api.entity.enums.SexoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "alunos")
public class Aluno extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long  id;
    @Column(nullable = false, length = 100)
   private String nome;
    @Column(nullable = false, length = 100)
   private String email;
   private LocalDate dataNascimento;
   @Column(nullable = false, length = 15)
   private String telefone;
   @Column(nullable = false)
    @Enumerated(EnumType.STRING)
   private SexoEnum sexo;
    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuarios usuario;
}
