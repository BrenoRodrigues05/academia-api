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
@Table(name = "personais")
public class Personal extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 100)
    private String sobrenome;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 15)
    private String telefone;

    @Column(nullable = false, unique = true, length = 20)
    private String cref;

    @Column(length = 100)
    private String especialidade;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SexoEnum sexo;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuarios usuario;

}
