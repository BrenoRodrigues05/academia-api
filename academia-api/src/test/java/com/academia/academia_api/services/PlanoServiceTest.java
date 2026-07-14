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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Planos (PlanoService)")
class PlanoServiceTest {

    @Mock
    private PlanoRepository planoRepository;

    @Mock
    private PlanoMapper planoMapper;

    @InjectMocks
    private PlanoService planoService;

    private Plano plano;
    private PlanoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        plano = new Plano();
        plano.setId(1L);
        plano.setNome("Plano Mensal");
        plano.setDescricao("Acesso livre à musculação");
        plano.setTipo(TipoPlano.MENSAL);
        plano.setValor(new BigDecimal("120.00"));

        responseDTO = new PlanoResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("Plano Mensal");
        responseDTO.setDescricao("Acesso livre à musculação");
        responseDTO.setTipo(TipoPlano.MENSAL);
        responseDTO.setValor(new BigDecimal("120.00"));
    }

    @Nested
    @DisplayName("Cenários de Listagem Geral (findAll)")
    class FindAllTests {

        @Test
        @DisplayName("Deve retornar uma lista de todos os planos")
        void deveRetornarTodosOsPlanos() {
            when(planoRepository.findAll()).thenReturn(List.of(plano));
            when(planoMapper.toResponseDTO(plano)).thenReturn(responseDTO);

            List<PlanoResponseDTO> resultado = planoService.findAll();

            assertFalse(resultado.isEmpty());
            assertEquals(1, resultado.size());
            verify(planoRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Cenários de Busca por ID (findById)")
    class FindByIdTests {

        @Test
        @DisplayName("Deve retornar o plano se o ID existir")
        void deveRetornarPlanoSeIdExistir() {
            when(planoRepository.findById(1L)).thenReturn(Optional.of(plano));
            when(planoMapper.toResponseDTO(plano)).thenReturn(responseDTO);

            PlanoResponseDTO resultado = planoService.findById(1L);

            assertNotNull(resultado);
            assertEquals("Plano Mensal", resultado.getNome());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o ID for inválido ou nulo")
        void deveValidarIdInvalido() {
            assertThrows(BadRequestException.class, () -> planoService.findById(null));
            assertThrows(BadRequestException.class, () -> planoService.findById(0L));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se o plano não for encontrado")
        void deveLancarErroSeNaoEncontrado() {
            when(planoRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> planoService.findById(99L));
        }
    }

    @Nested
    @DisplayName("Cenários de Cadastro (criarPlano)")
    class CriarPlanoTests {

        @Test
        @DisplayName("Deve salvar um novo plano com sucesso")
        void deveCriarPlanoComSucesso() {
            PlanoCreateDTO createDTO = new PlanoCreateDTO();
            createDTO.setNome("Plano Mensal");

            when(planoMapper.toEntity(createDTO)).thenReturn(plano);
            when(planoRepository.save(plano)).thenReturn(plano);
            when(planoMapper.toResponseDTO(plano)).thenReturn(responseDTO);

            PlanoResponseDTO resultado = planoService.criarPlano(createDTO);

            assertNotNull(resultado);
            assertEquals("Plano Mensal", resultado.getNome());
            verify(planoRepository, times(1)).save(plano);
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o nome enviado for nulo ou em branco")
        void deveValidarNomeObrigatorio() {
            PlanoCreateDTO dtoInvalido = new PlanoCreateDTO();
            dtoInvalido.setNome("   ");

            assertThrows(BadRequestException.class, () -> planoService.criarPlano(dtoInvalido));
            verify(planoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Cenários de Atualização e Exclusão")
    class UpdateAndDeleteTests {

        @Test
        @DisplayName("Deve atualizar dados do plano com sucesso")
        void deveAtualizarComSucesso() {
            PlanoUpdateDTO updateDTO = new PlanoUpdateDTO();

            when(planoRepository.findById(1L)).thenReturn(Optional.of(plano));
            when(planoRepository.save(plano)).thenReturn(plano);
            when(planoMapper.toResponseDTO(plano)).thenReturn(responseDTO);

            PlanoResponseDTO resultado = planoService.atualizarPlano(1L, updateDTO);

            assertNotNull(resultado);
            verify(planoMapper).updateEntityFromDTO(updateDTO, plano);
        }

        @Test
        @DisplayName("Deve remover um plano do banco com sucesso")
        void deveDeletarComSucesso() {
            when(planoRepository.findById(1L)).thenReturn(Optional.of(plano));
            when(planoMapper.toResponseDTO(plano)).thenReturn(responseDTO);

            PlanoResponseDTO resultado = planoService.deletarPlano(1L);

            assertNotNull(resultado);
            verify(planoRepository, times(1)).delete(plano);
        }
    }

    @Nested
    @DisplayName("Cenários de Consultas Customizadas (Nome, Descrição, Tipo, Valor)")
    class CustomQueriesTests {

        @Test
        @DisplayName("Deve buscar plano pelo nome exato com sucesso")
        void deveBuscarPorNome() {
            when(planoRepository.findByNomeContainingIgnoreCase("Plano Mensal")).thenReturn(plano);
            when(planoMapper.toResponseDTO(plano)).thenReturn(responseDTO);

            PlanoResponseDTO resultado = planoService.BuscarPeloNome("Plano Mensal");

            assertNotNull(resultado);
            assertEquals("Plano Mensal", resultado.getNome());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se a busca por nome retornar nulo")
        void deveTratarNomeNaoEncontrado() {
            when(planoRepository.findByNomeContainingIgnoreCase("Inexistente")).thenReturn(null);
            assertThrows(ResourceNotFoundException.class, () -> planoService.BuscarPeloNome("Inexistente"));
        }

        @Test
        @DisplayName("Deve buscar plano por sua descrição")
        void deveBuscarPorDescricao() {
            when(planoRepository.findByDescricaoContainingIgnoreCase("Acesso livre à musculação")).thenReturn(plano);
            when(planoMapper.toResponseDTO(plano)).thenReturn(responseDTO);

            PlanoResponseDTO resultado = planoService.BuscarPeloDescricao("Acesso livre à musculação");

            assertNotNull(resultado);
            assertEquals("Acesso livre à musculação", resultado.getDescricao());
        }

        @Test
        @DisplayName("Deve buscar plano através do Enum TipoPlano")
        void deveBuscarPorTipo() {
            when(planoRepository.findByTipo(TipoPlano.MENSAL)).thenReturn(plano);
            when(planoMapper.toResponseDTO(plano)).thenReturn(responseDTO);

            PlanoResponseDTO resultado = planoService.BuscarPeloTipo(TipoPlano.MENSAL);

            assertNotNull(resultado);
            assertEquals(TipoPlano.MENSAL, resultado.getTipo());
        }

        @Test
        @DisplayName("Deve buscar plano pelo valor monetário correspondente")
        void deveBuscarPorValor() {
            BigDecimal valorAlvo = new BigDecimal("120.00");
            when(planoRepository.findByValor(valorAlvo)).thenReturn(plano);
            when(planoMapper.toResponseDTO(plano)).thenReturn(responseDTO);

            PlanoResponseDTO resultado = planoService.BuscaPeloValor(valorAlvo);

            assertNotNull(resultado);
            assertEquals(valorAlvo, resultado.getValor());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o valor de busca for nulo ou menor que zero")
        void deveValidarValorBusca() {
            assertThrows(BadRequestException.class, () -> planoService.BuscaPeloValor(null));
            assertThrows(BadRequestException.class, () -> planoService.BuscaPeloValor(new BigDecimal("-10.00")));
        }
    }
}