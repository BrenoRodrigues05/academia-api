package com.academia.academia_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "matriculas")
public class Matricula extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matricula;
    @ManyToOne
    @JoinColumn(name = "plano_id", nullable = false)
    private Plano plano;
    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;
    @Column(nullable = false)
    private boolean ativa;
    @OneToMany(mappedBy = "matricula", cascade = CascadeType.ALL)
    private List<Pagamento> pagamentos = new ArrayList<>();
}
