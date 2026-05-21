package com.academia.academia_api.services;

import com.academia.academia_api.entity.Plano;
import com.academia.academia_api.entity.enums.TipoPlano;
import com.academia.academia_api.repository.PlanoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public Plano BuscarPeloNome(String nome) {
        if(nome == null || nome.isEmpty()) {
            throw new RuntimeException("Nome do plano inválido ou não existe.");
        }
        return planoRepository.findByNome(nome);
    }

    public Plano  BuscarPeloDescricao(String descricao) {
        if(descricao == null || descricao.isEmpty()) {
            throw new RuntimeException("Descrição inválida ou não existe.");
        }
        return  planoRepository.findByDescricao(descricao);
    }

    public Plano  BuscarPeloTipo(TipoPlano tipo) {
        var bucaPlano = planoRepository.findByTipo(tipo);
        if(bucaPlano == null) {
            throw new RuntimeException("Não existe plano com o tipo selecionado.");
        }
        return bucaPlano;
    }

    public Plano BuscaPeloValor(BigDecimal valor) {
        if(valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor inválido ou nulo");
        }
        var bucaPlano = planoRepository.findByValor(valor);
        if(bucaPlano == null) {
            throw new RuntimeException("Não existe plano com o valor selecionado.");
        }
        return bucaPlano;
    }
}
