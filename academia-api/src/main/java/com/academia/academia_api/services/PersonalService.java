package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.PageResponseDTO;
import com.academia.academia_api.DTOs.PersonalCreateDTO;
import com.academia.academia_api.DTOs.PersonalResponseDTO;
import com.academia.academia_api.DTOs.PersonalUpdateDTO;
import com.academia.academia_api.entity.Personal;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.infra.exceptions.ResourceNotFoundException;
import com.academia.academia_api.mappings.PersonalMapper;
import com.academia.academia_api.repository.PersonalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonalService {

    private final PersonalRepository personalRepository;
    private final PersonalMapper personalMapper;

    public PersonalService(PersonalRepository personalRepository, PersonalMapper personalMapper) {
        this.personalRepository = personalRepository;
        this.personalMapper = personalMapper;
    }

    public PageResponseDTO<PersonalResponseDTO> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nome").ascending());
        Page<Personal> personals = personalRepository.findAll(pageable);

        return new PageResponseDTO<>(
                personals.getContent().stream().map(personalMapper::toResponseDTO).toList(),
                personals.getNumber(),
                personals.getSize(),
                personals.getTotalElements(),
                personals.getTotalPages()
        );
    }

    public PersonalResponseDTO findById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("O ID informado é inválido ou nulo.");
        }
        Personal personal = personalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Personal não encontrado com o ID: " + id));

        return personalMapper.toResponseDTO(personal);
    }

    public PersonalResponseDTO findByCref(String cref) {
        if (cref == null || cref.trim().isEmpty()) {
            throw new BadRequestException("O CREF informado é inválido ou vazio.");
        }

        Personal personal = personalRepository.findByCref(cref)
                .orElseThrow(() -> new ResourceNotFoundException("Personal não encontrado com o CREF: " + cref));

        return personalMapper.toResponseDTO(personal);
    }

    public PersonalResponseDTO findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BadRequestException("O e-mail informado é inválido ou vazio.");
        }

        Personal personal = personalRepository.findByEmailContainingIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Personal não encontrado com o e-mail: " + email));

        return personalMapper.toResponseDTO(personal);
    }

    public List<PersonalResponseDTO> findByNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new BadRequestException("O nome informado é inválido ou vazio.");
        }

        List<Personal> buscaPersonal = personalRepository.findByNomeContainingIgnoreCase(nome);
        if (buscaPersonal.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum personal encontrado com o nome: " + nome);
        }

        return buscaPersonal.stream()
                .map(personalMapper::toResponseDTO)
                .toList();
    }

    public List<PersonalResponseDTO> findByAtivoFalse() {
        List<Personal> buscaPersonal = personalRepository.findByAtivoFalse();
        if (buscaPersonal.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum personal inativo encontrado.");
        }
        return buscaPersonal.stream()
                .map(personalMapper::toResponseDTO)
                .toList();
    }

    public List<PersonalResponseDTO> findByAtivoTrue() {
        List<Personal> buscaPersonal = personalRepository.findByAtivoTrue();
        if (buscaPersonal.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum personal ativo encontrado.");
        }
        return buscaPersonal.stream()
                .map(personalMapper::toResponseDTO)
                .toList();
    }

    public PersonalResponseDTO addPersonal(PersonalCreateDTO personalCreateDTO) {
        if (personalRepository.findByEmailContainingIgnoreCase(personalCreateDTO.getEmail()).isPresent()) {
            throw new BadRequestException("Já existe um personal cadastrado com este e-mail.");
        }

        if (personalRepository.findByCref(personalCreateDTO.getCref()).isPresent()) {
            throw new BadRequestException("Já existe um personal cadastrado com este CREF.");
        }

        Personal personalEntity = personalMapper.toEntity(personalCreateDTO);
        Personal novoPersonal = personalRepository.save(personalEntity);

        return personalMapper.toResponseDTO(novoPersonal);
    }

    public PersonalResponseDTO updatePersonal(Long id, PersonalUpdateDTO personalUpdateDTO) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID inválido ou nulo para atualização.");
        }

        Personal personalExistente = personalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Personal não encontrado para atualização."));

        personalMapper.updateEntityFromDTO(personalUpdateDTO, personalExistente);
        Personal personalAtualizado = personalRepository.save(personalExistente);

        return personalMapper.toResponseDTO(personalAtualizado);
    }

    public PersonalResponseDTO atualizarAtivoPersonal(Long id, boolean ativo) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID inválido ou nulo.");
        }

        Personal buscaPersonal = personalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Personal não encontrado para alteração de status."));

        if (buscaPersonal.getAtivo() != null && buscaPersonal.getAtivo().equals(ativo)) {
            throw new BadRequestException("O personal selecionado já possui o status informado.");
        }

        buscaPersonal.setAtivo(ativo);
        personalRepository.save(buscaPersonal);
        return personalMapper.toResponseDTO(buscaPersonal);
    }

    public PersonalResponseDTO deletePersonal(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID inválido ou nulo para exclusão.");
        }

        Personal personalDeletado = personalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Personal não encontrado para exclusão."));

        if (Boolean.TRUE.equals(personalDeletado.getAtivo())) {
            throw new BadRequestException("O personal está ativo. Para deletar, primeiro altere o status para inativo.");
        }

        personalRepository.delete(personalDeletado);
        return personalMapper.toResponseDTO(personalDeletado);
    }
}