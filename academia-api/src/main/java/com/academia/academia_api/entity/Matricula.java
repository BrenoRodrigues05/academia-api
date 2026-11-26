package com.academia.academia_api.entity;

import com.academia.academia_api.entity.enums.MatriculaStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;
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
    @JoinColumn(name = "plano_agendado_id")
    private Plano planoAgendado;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @Column(nullable = false)
    private boolean ativa;

    @OneToMany(mappedBy = "matricula", cascade = CascadeType.ALL)
    private List<Pagamento> pagamentos = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private MatriculaStatusEnum status;

    @Range(min = 1, max = 25)
    @Column(name = "dia_vencimento")
    private Integer diaVencimento;

    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;
}
