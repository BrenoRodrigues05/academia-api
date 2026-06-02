package com.academia.academia_api.services;
import com.academia.academia_api.DTOs.PlanoCreateDTO;
import com.academia.academia_api.DTOs.PlanoResponseDTO;
import com.academia.academia_api.DTOs.PlanoUpdateDTO;
import com.academia.academia_api.entity.Plano;
import com.academia.academia_api.entity.enums.TipoPlano;
import com.academia.academia_api.mappings.PlanoMapper;
import com.academia.academia_api.repository.PlanoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PlanoService {

    private final PlanoRepository planoRepository;
    private final PlanoMapper planoMapper;

    public PlanoService(PlanoRepository planoRepository, PlanoMapper planoMapper) {
        this.planoRepository = planoRepository;
        this.planoMapper = planoMapper;
    }

    public List<PlanoResponseDTO> findAll() {
        return planoRepository.findAll().stream()
                .map(planoMapper::toResponseDTO)
                .toList();
    }

    public PlanoResponseDTO findById(Long id) {
        if(id == null || id <= 0) {
            throw new RuntimeException("Id invalido ou inexistente");
        }
        Plano plano = planoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Id invalido ou inexistente"));

        return  planoMapper.toResponseDTO(plano);
    }

    public PlanoResponseDTO criarPlano(PlanoCreateDTO dto) {

        Plano  plano = planoMapper.toEntity(dto);
        plano = planoRepository.save(plano);
        return planoMapper.toResponseDTO(plano);
    }

    public PlanoResponseDTO atualizarPlano(Long id, PlanoUpdateDTO updateDTO) {

        if(id == null || id <= 0) {
            throw new RuntimeException("Id invalido ou inexistente");
        }

        var buscaPlano =  planoRepository.findById(id);
        if(buscaPlano == null) {
            throw new RuntimeException("Id invalido ou inexistente");
        }

        Plano planoExistente = buscaPlano.orElseThrow(() -> new RuntimeException("Id invalido ou inexistente"));

        planoMapper.updateEntityFromDTO(updateDTO,  planoExistente);

        Plano  planoAtualizado = planoRepository.save(planoExistente);

        return  planoMapper.toResponseDTO(planoAtualizado);
    }

    public PlanoResponseDTO deletarPlano(Long id) {
        if(id == null || id <= 0) {
            throw new RuntimeException("Id inexistente ou inválido.");
        }

        Plano planoDeletado = planoRepository.findById(id).orElse(null);

        if(planoDeletado != null) {
            planoRepository.delete(planoDeletado);
        }

        return planoMapper.toResponseDTO(planoDeletado);
    }

    public PlanoResponseDTO BuscarPeloNome(String nome) {
        if(nome == null || nome.isBlank()) {
            throw new RuntimeException("Nome do plano inválido ou não existe.");
        }
        Plano plano = planoRepository.findByNome(nome);

        if (plano == null) {
            throw new RuntimeException("Plano não encontrado.");
        }

        return planoMapper.toResponseDTO(plano);
    }

    public PlanoResponseDTO  BuscarPeloDescricao(String descricao) {
        if(descricao == null || descricao.isBlank()) {
            throw new RuntimeException("Descrição inválida ou não existe.");
        }

        Plano plano = planoRepository.findByDescricao(descricao);

        if (plano == null) {
            throw new RuntimeException("Plano não encontrado.");
        }

        return planoMapper.toResponseDTO(plano);
    }

    public PlanoResponseDTO  BuscarPeloTipo(TipoPlano tipo) {

        Plano plano = planoRepository.findByTipo(tipo);

        if (plano == null) {
            throw new RuntimeException("Plano não encontrado.");
        }

        return planoMapper.toResponseDTO(plano);
    }

    public PlanoResponseDTO BuscaPeloValor(BigDecimal valor) {
        if(valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor inválido ou nulo");
        }
        Plano plano = planoRepository.findByValor(valor);
        if(plano == null) {
            throw new RuntimeException("Não existe plano com o valor selecionado.");
        }
        return planoMapper.toResponseDTO(plano);
    }
}
