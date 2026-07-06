package com.academia.academia_api.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExecucaoTreinoResponseDTO {

    private Long id;

    private Long treinoId;

    private String treino;

    private LocalDateTime dataExecucao;

    private Boolean concluido;

    private String observacoes;
}
