package com.academia.academia_api.services;

import com.academia.academia_api.entity.Plano;
import com.academia.academia_api.repository.PlanoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanoService {

    private final PlanoRepository planoRepository;

    public PlanoService(PlanoRepository planoRepository) {
        this.planoRepository = planoRepository;
    }

    public List<Plano> findAll() {
        return planoRepository.findAll();
    }

    public Plano findById(Long id) {
        if(id == null || id <= 0) {
            throw new RuntimeException("Id invalido ou inexistente");
        }
        return  planoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado."));
    }

    public Plano criarPlano(Plano plano) {

        return planoRepository.save(plano);
    }

    public Plano atualizarPlano(Plano plano) {
        return planoRepository.save(plano);
    }

    public void deletarPlano(Long id) {
        if(id == null || id <= 0) {
            throw new RuntimeException("Id inexistente ou inválido.");
        }
        planoRepository.deleteById(id);
    }

}
