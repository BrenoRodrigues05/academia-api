package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.PersonalCreateDTO;
import com.academia.academia_api.DTOs.PersonalResponseDTO;
import com.academia.academia_api.DTOs.PersonalUpdateDTO;
import com.academia.academia_api.entity.Personal;
import com.academia.academia_api.mappings.PersonalMapper;
import com.academia.academia_api.repository.PersonalRepository;
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

    public List<PersonalResponseDTO> findAll() {
        return personalRepository.findAll().stream()
                .map(personalMapper :: toResponseDTO)
                .toList();
    }

    public PersonalResponseDTO findById(Long id) {
        if(id == null || id <= 0){
           throw new  RuntimeException("Id invalido ou nulo.");
        }
        Personal personal = personalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personal não encontrado."));

        return personalMapper.toResponseDTO(personal);
    }

    public PersonalResponseDTO findByCref(String cref) {
        if(cref == null || cref.isBlank()){
            throw new   RuntimeException("Cref invalido ou vazio.");
        }

        var buscaPersonal = personalRepository.findByCref(cref);

        if(buscaPersonal.isEmpty()){
           throw new RuntimeException("Personal não encontrado.");
        }
        return personalMapper.toResponseDTO(buscaPersonal.get());
    }

    public PersonalResponseDTO findByEmail(String email) {
        if(email == null || email.isBlank()){
            throw  new   RuntimeException("Email invalido ou vazio.");
        }
        var buscaPersonal = personalRepository.findByEmail(email);
        if(buscaPersonal.isEmpty()){
            throw new RuntimeException("Personal não encontrado.");
        }
        return personalMapper.toResponseDTO(buscaPersonal.get());
    }

    public List<PersonalResponseDTO> findByNome(String nome) {
        if(nome == null || nome.isBlank()){
            throw   new   RuntimeException("Nome invalido ou vazio.");
        }
        var buscaPersonal = personalRepository.findByNome(nome);
        if(buscaPersonal.isEmpty()){
            throw new RuntimeException("Personal não encontrado.");
        }
        return buscaPersonal.stream()
                .map(personalMapper::toResponseDTO)
                .toList();
    }

    public List<PersonalResponseDTO> findByAtivoFalse() {
        var buscaPersonal = personalRepository.findByAtivoFalse();
        if(buscaPersonal.isEmpty()){
            throw new RuntimeException("Nenhum personal inativo encontrado.");
        }
        return buscaPersonal.stream()
                .map(personalMapper::toResponseDTO)
                .toList();
    }

    public  List<PersonalResponseDTO> findByAtivoTrue() {
        var buscaPersonal = personalRepository.findByAtivoTrue();
        if(buscaPersonal.isEmpty()){
            throw new RuntimeException("Nenhum personal ativo encontrado.");
        }
        return buscaPersonal.stream()
                .map(personalMapper::toResponseDTO)
                .toList();
    }

    public PersonalResponseDTO addPersonal(PersonalCreateDTO  personalCreateDTO) {

        if (personalRepository.findByEmail(personalCreateDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Já existe um personal com esse e-mail.");
        }

        if (personalRepository.findByCref(personalCreateDTO.getCref()).isPresent()) {
            throw new RuntimeException("Já existe um personal com esse CREF.");
        }

        Personal personalEntity = personalMapper.toEntity(personalCreateDTO);

        Personal novoPersonal  = personalRepository.save(personalEntity);

        return  personalMapper.toResponseDTO(novoPersonal);
    }

    public PersonalResponseDTO updatePersonal(Long id, PersonalUpdateDTO personalUpdateDTO) {
        if(id == null || id <= 0){
            throw new RuntimeException("Id inválido ou nulo.");
        }

        Personal personalExistente = personalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personal não encontrado para atualização."));

        personalMapper.updateEntityFromDTO(personalUpdateDTO, personalExistente);
        Personal personalAtualizado =  personalRepository.save(personalExistente);

        return   personalMapper.toResponseDTO(personalAtualizado);
    }

    public  PersonalResponseDTO atualizarAtivoPersonal(Long id, boolean ativo) {
        if(id == null || id <= 0){
            throw new RuntimeException("Id invalido ou nulo.");
        }

        var buscaPersonal = personalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personal não encontrado para alteração."));

        if (buscaPersonal.getAtivo().equals(ativo)) {
            throw new RuntimeException("O personal selecionado já está com esse status.");
        }

        buscaPersonal.setAtivo(ativo);
        personalRepository.save(buscaPersonal);
        return personalMapper.toResponseDTO(buscaPersonal);
    }

    public  PersonalResponseDTO deletePersonal(Long id) {
        if(id == null || id <= 0){
            throw new RuntimeException("Id invalido ou nulo.");
        }

        Personal personalDeletado = personalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personal não encontrado para deletar."));

        if(personalDeletado.getAtivo() == true){
            throw new RuntimeException("O personal está ativo. Para deletar, primeiro desative.");
        }
        personalRepository.delete(personalDeletado);
        return personalMapper.toResponseDTO(personalDeletado);
    }


}
