package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.PlanoCreateDTO;
import com.academia.academia_api.DTOs.PlanoResponseDTO;
import com.academia.academia_api.DTOs.PlanoUpdateDTO;
import com.academia.academia_api.entity.Plano;
import com.academia.academia_api.entity.enums.TipoPlano;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.infra.exceptions.ResourceNotFoundException;
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
        if (id == null || id <= 0) {
            throw new BadRequestException("O ID informado é inválido ou nulo.");
        }
        Plano plano = planoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plano não encontrado com o ID: " + id));

        return planoMapper.toResponseDTO(plano);
    }

    public PlanoResponseDTO criarPlano(PlanoCreateDTO dto) {
        if (dto.getNome() == null || dto.getNome().trim().isEmpty()) {
            throw new BadRequestException("O nome do plano não pode ser vazio.");
        }

        Plano plano = planoMapper.toEntity(dto);
        plano = planoRepository.save(plano);
        return planoMapper.toResponseDTO(plano);
    }

    public PlanoResponseDTO atualizarPlano(Long id, PlanoUpdateDTO updateDTO) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID inválido ou nulo para atualização.");
        }

        Plano planoExistente = planoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plano não encontrado para atualização com o ID: " + id));

        planoMapper.updateEntityFromDTO(updateDTO, planoExistente);
        Plano planoAtualizado = planoRepository.save(planoExistente);

        return planoMapper.toResponseDTO(planoAtualizado);
    }

    public PlanoResponseDTO deletarPlano(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID inválido ou nulo para exclusão.");
        }

        Plano planoDeletado = planoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plano não encontrado para exclusão com o ID: " + id));

        planoRepository.delete(planoDeletado);
        return planoMapper.toResponseDTO(planoDeletado);
    }

    public PlanoResponseDTO BuscarPeloNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new BadRequestException("O nome informado para busca é inválido ou vazio.");
        }
        Plano plano = planoRepository.findByNome(nome);

        if (plano == null) {
            throw new ResourceNotFoundException("Plano não encontrado com o nome: " + nome);
        }

        return planoMapper.toResponseDTO(plano);
    }

    public PlanoResponseDTO BuscarPeloDescricao(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new BadRequestException("A descrição informada para busca é inválida ou vazia.");
        }

        Plano plano = planoRepository.findByDescricao(descricao);

        if (plano == null) {
            throw new ResourceNotFoundException("Plano não encontrado com a descrição informada.");
        }

        return planoMapper.toResponseDTO(plano);
    }

    public PlanoResponseDTO BuscarPeloTipo(TipoPlano tipo) {
        if (tipo == null) {
            throw new BadRequestException("O tipo de plano informado não pode ser nulo.");
        }

        Plano plano = planoRepository.findByTipo(tipo);

        if (plano == null) {
            throw new ResourceNotFoundException("Plano não encontrado para o tipo selecionado: " + tipo);
        }

        return planoMapper.toResponseDTO(plano);
    }

    public PlanoResponseDTO BuscaPeloValor(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("O valor do plano deve ser maior que zero.");
        }
        Plano plano = planoRepository.findByValor(valor);
        if (plano == null) {
            throw new ResourceNotFoundException("Nenhum plano encontrado com o valor de: R$ " + valor);
        }
        return planoMapper.toResponseDTO(plano);
    }
}