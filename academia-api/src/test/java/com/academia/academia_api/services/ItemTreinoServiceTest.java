package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.ItemTreinoCreateDTO;
import com.academia.academia_api.DTOs.ItemTreinoResponseDTO;
import com.academia.academia_api.DTOs.ItemTreinoUpdateDTO;
import com.academia.academia_api.entity.Exercicio;
import com.academia.academia_api.entity.ItemTreino;
import com.academia.academia_api.entity.Treino;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.infra.exceptions.ResourceNotFoundException;
import com.academia.academia_api.mappings.ItemTreinoMapper;
import com.academia.academia_api.repository.ExercicioRepository;
import com.academia.academia_api.repository.ItemTreinoRepository;
import com.academia.academia_api.repository.TreinoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Itens de Treino (ItemTreinoService)")
class ItemTreinoServiceTest {

    @Mock
    private ItemTreinoRepository itemTreinoRepository;

    @Mock
    private TreinoRepository treinoRepository;

    @Mock
    private ExercicioRepository exercicioRepository;

    @Mock
    private ItemTreinoMapper itemTreinoMapper;

    @InjectMocks
    private ItemTreinoService itemTreinoService;

    private ItemTreino itemTreino;
    private Treino treino;
    private Exercicio exercicio;
    private ItemTreinoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        treino = new Treino();
        treino.setId(10L);

        exercicio = new Exercicio();
        exercicio.setId(20L);
        exercicio.setNome("Agachamento Livre");

        itemTreino = new ItemTreino();
        itemTreino.setId(1L);
        itemTreino.setTreino(treino);
        itemTreino.setExercicio(exercicio);

        responseDTO = new ItemTreinoResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setTreinoId(10L);
        responseDTO.setExercicioId(20L);
    }

    @Nested
    @DisplayName("Cenários de Listagem Geral (findAll)")
    class FindAllTests {

        @Test
        @DisplayName("Deve retornar uma lista de itens de treino com sucesso")
        void deveRetornarTodosOsItens() {
            when(itemTreinoRepository.findAll()).thenReturn(List.of(itemTreino));
            when(itemTreinoMapper.toResponseDTO(itemTreino)).thenReturn(responseDTO);

            List<ItemTreinoResponseDTO> resultado = itemTreinoService.findAll();

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(itemTreinoRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Cenários de Busca por ID (findById)")
    class FindByIdTests {

        @Test
        @DisplayName("Deve retornar o item de treino se o ID existir")
        void deveRetornarItemPorId() {
            when(itemTreinoRepository.findById(1L)).thenReturn(Optional.of(itemTreino));
            when(itemTreinoMapper.toResponseDTO(itemTreino)).thenReturn(responseDTO);

            ItemTreinoResponseDTO resultado = itemTreinoService.findById(1L);

            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o ID for nulo ou inválido")
        void deveValidarIdInvalido() {
            assertThrows(BadRequestException.class, () -> itemTreinoService.findById(null));
            assertThrows(BadRequestException.class, () -> itemTreinoService.findById(0L));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se o item não for encontrado")
        void deveLancarErroSeItemNaoEncontrado() {
            when(itemTreinoRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> itemTreinoService.findById(99L));
        }
    }

    @Nested
    @DisplayName("Cenários de Busca por Treino (findByTreino)")
    class FindByTreinoTests {

        @Test
        @DisplayName("Deve retornar itens vinculados a um treino existente")
        void deveBuscarPorTreinoComSucesso() {
            when(treinoRepository.existsById(10L)).thenReturn(true);
            when(itemTreinoRepository.findByTreinoId(10L)).thenReturn(List.of(itemTreino));
            when(itemTreinoMapper.toResponseDTO(itemTreino)).thenReturn(responseDTO);

            List<ItemTreinoResponseDTO> resultado = itemTreinoService.findByTreino(10L);

            assertFalse(resultado.isEmpty());
            assertEquals(10L, resultado.get(0).getTreinoId());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se o treino pai não existir")
        void deveLancarErroSeTreinoNaoExistir() {
            when(treinoRepository.existsById(99L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class, () -> itemTreinoService.findByTreino(99L));
            verify(itemTreinoRepository, never()).findByTreinoId(any());
        }
    }

    @Nested
    @DisplayName("Cenários de Busca por Exercício (findByExercicio)")
    class FindByExercicioTests {

        @Test
        @DisplayName("Deve retornar itens vinculados a um exercício existente")
        void deveBuscarPorExercicioComSucesso() {
            when(exercicioRepository.existsById(20L)).thenReturn(true);
            when(itemTreinoRepository.findByExercicioId(20L)).thenReturn(List.of(itemTreino));
            when(itemTreinoMapper.toResponseDTO(itemTreino)).thenReturn(responseDTO);

            List<ItemTreinoResponseDTO> resultado = itemTreinoService.findByExercicio(20L);

            assertFalse(resultado.isEmpty());
            assertEquals(20L, resultado.get(0).getExercicioId());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se o exercício associado não existir")
        void deveLancarErroSeExercicioNaoExistir() {
            when(exercicioRepository.existsById(99L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class, () -> itemTreinoService.findByExercicio(99L));
            verify(itemTreinoRepository, never()).findByExercicioId(any());
        }
    }

    @Nested
    @DisplayName("Cenários de Atualização (updateItemTreino)")
    class UpdateItemTreinoTests {

        @Test
        @DisplayName("Deve atualizar as propriedades de um item com sucesso")
        void deveAtualizarComSucesso() {
            ItemTreinoUpdateDTO updateDTO = new ItemTreinoUpdateDTO();

            when(itemTreinoRepository.findById(1L)).thenReturn(Optional.of(itemTreino));
            when(itemTreinoRepository.save(itemTreino)).thenReturn(itemTreino);
            when(itemTreinoMapper.toResponseDTO(itemTreino)).thenReturn(responseDTO);

            ItemTreinoResponseDTO resultado = itemTreinoService.updateItemTreino(1L, updateDTO);

            assertNotNull(resultado);
            verify(itemTreinoMapper).updateEntityFromDTO(updateDTO, itemTreino);
            verify(itemTreinoRepository).save(itemTreino);
        }
    }

    @Nested
    @DisplayName("Cenários de Exclusão (deleteItemTreino)")
    class DeleteItemTreinoTests {

        @Test
        @DisplayName("Deve remover um item de treino com sucesso")
        void deveDeletarComSucesso() {
            when(itemTreinoRepository.findById(1L)).thenReturn(Optional.of(itemTreino));
            when(itemTreinoMapper.toResponseDTO(itemTreino)).thenReturn(responseDTO);

            ItemTreinoResponseDTO resultado = itemTreinoService.deleteItemTreino(1L);

            assertNotNull(resultado);
            verify(itemTreinoRepository, times(1)).delete(itemTreino);
        }
    }
}