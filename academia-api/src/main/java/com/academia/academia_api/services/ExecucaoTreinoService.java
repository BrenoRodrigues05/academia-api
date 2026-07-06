package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.ExecucaoTreinoResponseDTO;
import com.academia.academia_api.entity.ExecucaoTreino;
import com.academia.academia_api.entity.Treino;
import com.academia.academia_api.entity.Usuarios;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.infra.exceptions.ResourceNotFoundException;
import com.academia.academia_api.mappings.ExecucaoTreinoMapper;
import com.academia.academia_api.repository.ExecucaoTreinoRepository;
import com.academia.academia_api.repository.TreinoRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ExecucaoTreinoService {

    private final ExecucaoTreinoRepository execucaoTreinoRepository;
    private final TreinoRepository treinoRepository;
    private final ExecucaoTreinoMapper mapper;

    public ExecucaoTreinoService(ExecucaoTreinoRepository execucaoTreinoRepository, TreinoRepository treinoRepository, ExecucaoTreinoMapper mapper) {
        this.execucaoTreinoRepository = execucaoTreinoRepository;
        this.treinoRepository = treinoRepository;
        this.mapper = mapper;
    }

    public ExecucaoTreinoResponseDTO iniciarTreino(Long treinoId){
        Usuarios usuario = getUsuarioLogado();

        Treino treino = treinoRepository.findById(treinoId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Treino não encontrado."));

        if (!treino.getAluno().getUsuario().getId().equals(usuario.getId())) {
            throw new BadRequestException(
                    "Você só pode executar seus próprios treinos.");
        }

        ExecucaoTreino execucao = ExecucaoTreino.builder()
                .treino(treino)
                .aluno(treino.getAluno())
                .dataExecucao(LocalDateTime.now())
                .concluido(false)
                .build();

        return mapper.toResponseDTO(
                execucaoTreinoRepository.save(execucao)
        );
    }

    private Usuarios getUsuarioLogado() {
        return (Usuarios) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
