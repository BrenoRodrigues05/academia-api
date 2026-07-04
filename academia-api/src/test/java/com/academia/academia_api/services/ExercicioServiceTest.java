package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.ExercicioCreateDTO;
import com.academia.academia_api.DTOs.ExercicioResponseDTO;
import com.academia.academia_api.DTOs.ExercicioUpdateDTO;
import com.academia.academia_api.DTOs.PageResponseDTO;
import com.academia.academia_api.entity.Exercicio;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.infra.exceptions.ResourceNotFoundException;
import com.academia.academia_api.mappings.ExercicioMapper;
import com.academia.academia_api.repository.ExercicioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Exercícios (ExercicioService)")
class ExercicioServiceTest {

    @Mock
    private ExercicioRepository exercicioRepository;

    @Mock
    private ExercicioMapper exercicioMapper;

    @InjectMocks
    private ExercicioService exercicioService;

    private Exercicio exercicio;
    private ExercicioResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        exercicio = new Exercicio();
        exercicio.setId(1L);
        exercicio.setNome("Supino Reto");
        exercicio.setGrupoMuscular("Peitoral");

        responseDTO = new ExercicioResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("Supino Reto");
        responseDTO.setGrupoMuscular("Peitoral");
    }

    @Nested
    @DisplayName("Cenários de Listagem Geral (findAll)")
    class FindAllTests {

        @Test
        @DisplayName("Deve retornar uma página de exercícios com sucesso")
        void deveRetornarPaginaDeExercicios() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Exercicio> page = new PageImpl<>(List.of(exercicio));

            when(exercicioRepository.findAll(pageable)).thenReturn(page);
            when(exercicioMapper.toResponseDTO(exercicio)).thenReturn(responseDTO);

            PageResponseDTO<ExercicioResponseDTO> resultado = exercicioService.findAll(0, 10);

            assertNotNull(resultado);
            assertEquals(1, resultado.content().size());
            assertEquals(0, resultado.page());
            assertEquals(1, resultado.totalElements());
            verify(exercicioRepository, times(1)).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("Cenários de Busca por ID (findById)")
    class FindByIdTests {

        @Test
        @DisplayName("Deve retornar o exercício se o ID existir")
        void deveRetornarExercicioPorId() {
            when(exercicioRepository.findById(1L)).thenReturn(Optional.of(exercicio));
            when(exercicioMapper.toResponseDTO(exercicio)).thenReturn(responseDTO);

            ExercicioResponseDTO resultado = exercicioService.findById(1L);

            assertNotNull(resultado);
            assertEquals("Supino Reto", resultado.getNome());
            verify(exercicioRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o ID for nulo ou menor/igual a zero")
        void deveValidarIdInvalido() {
            assertThrows(BadRequestException.class, () -> exercicioService.findById(null));
            assertThrows(BadRequestException.class, () -> exercicioService.findById(0L));
            assertThrows(BadRequestException.class, () -> exercicioService.findById(-5L));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se o exercício não existir")
        void deveLancarErroSeExercicioNaoEncontrado() {
            when(exercicioRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> exercicioService.findById(99L));
        }
    }

    @Nested
    @DisplayName("Cenários de Busca por Nome (findByNome)")
    class FindByNomeTests {

        @Test
        @DisplayName("Deve buscar exercícios correspondentes ao termo informado com sucesso")
        void deveBuscarPorNomeComSucesso() {
            when(exercicioRepository.findByNomeContainingIgnoreCase("Supino")).thenReturn(List.of(exercicio));
            when(exercicioMapper.toResponseDTO(exercicio)).thenReturn(responseDTO);

            List<ExercicioResponseDTO> resultado = exercicioService.findByNome("Supino");

            assertFalse(resultado.isEmpty());
            assertEquals(1, resultado.size());
            verify(exercicioRepository).findByNomeContainingIgnoreCase("Supino");
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o termo de busca for nulo ou em branco")
        void deveValidarNomeInvalido() {
            assertThrows(BadRequestException.class, () -> exercicioService.findByNome(null));
            assertThrows(BadRequestException.class, () -> exercicioService.findByNome("   "));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se a busca não retornar registros")
        void deveLancarErroSeNenhumResultadoPorNome() {
            when(exercicioRepository.findByNomeContainingIgnoreCase("Inexistente")).thenReturn(Collections.emptyList());

            assertThrows(ResourceNotFoundException.class, () -> exercicioService.findByNome("Inexistente"));
        }
    }

    @Nested
    @DisplayName("Cenários de Busca por Grupo Muscular (findByGrupoMuscular)")
    class FindByGrupoMuscularTests {

        @Test
        @DisplayName("Deve retornar a lista de exercícios baseada no grupo muscular")
        void deveBuscarPorGrupoMuscularComSucesso() {
            when(exercicioRepository.findByGrupoMuscularIgnoreCase("Peitoral")).thenReturn(List.of(exercicio));
            when(exercicioMapper.toResponseDTO(exercicio)).thenReturn(responseDTO);

            List<ExercicioResponseDTO> resultado = exercicioService.findByGrupoMuscular("Peitoral");

            assertFalse(resultado.isEmpty());
            assertEquals("Peitoral", resultado.get(0).getGrupoMuscular());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o grupo muscular for nulo ou em branco")
        void deveValidarGrupoMuscularInvalido() {
            assertThrows(BadRequestException.class, () -> exercicioService.findByGrupoMuscular(null));
            assertThrows(BadRequestException.class, () -> exercicioService.findByGrupoMuscular(""));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se nenhum exercício pertencer ao grupo informado")
        void deveLancarErroSeGrupoMuscularNaoEncontrado() {
            when(exercicioRepository.findByGrupoMuscularIgnoreCase("Cardio")).thenReturn(Collections.emptyList());

            assertThrows(ResourceNotFoundException.class, () -> exercicioService.findByGrupoMuscular("Cardio"));
        }
    }

    @Nested
    @DisplayName("Cenários de Cadastro (addExercicio)")
    class AddExercicioTests {

        @Test
        @DisplayName("Deve salvar um novo exercício com sucesso")
        void deveCadastrarExercicioComSucesso() {
            ExercicioCreateDTO createDTO = new ExercicioCreateDTO();
            createDTO.setNome("Supino Reto");

            when(exercicioRepository.existsByNomeIgnoreCase("Supino Reto")).thenReturn(false);
            when(exercicioMapper.toEntity(createDTO)).thenReturn(exercicio);
            when(exercicioRepository.save(exercicio)).thenReturn(exercicio);
            when(exercicioMapper.toResponseDTO(exercicio)).thenReturn(responseDTO);

            ExercicioResponseDTO resultado = exercicioService.addExercicio(createDTO);

            assertNotNull(resultado);
            assertEquals("Supino Reto", resultado.getNome());
            verify(exercicioRepository).save(exercicio);
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o nome do DTO for nulo ou em branco")
        void deveValidarNomeObrigatorio() {
            ExercicioCreateDTO dtoNomeNulo = new ExercicioCreateDTO();
            dtoNomeNulo.setNome(null);

            ExercicioCreateDTO dtoNomeVazio = new ExercicioCreateDTO();
            dtoNomeVazio.setNome("    ");

            assertThrows(BadRequestException.class, () -> exercicioService.addExercicio(dtoNomeNulo));
            assertThrows(BadRequestException.class, () -> exercicioService.addExercicio(dtoNomeVazio));
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se já existir exercício cadastrado com o mesmo nome")
        void deveLancarErroSeNomeDuplicado() {
            ExercicioCreateDTO createDTO = new ExercicioCreateDTO();
            createDTO.setNome("Supino Reto");

            when(exercicioRepository.existsByNomeIgnoreCase("Supino Reto")).thenReturn(true);

            BadRequestException exception = assertThrows(BadRequestException.class,
                    () -> exercicioService.addExercicio(createDTO));

            assertTrue(exception.getMessage().contains("Já existe um exercício cadastrado"));
            verify(exercicioRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Cenários de Atualização (updateExercicio)")
    class UpdateExercicioTests {

        @Test
        @DisplayName("Deve atualizar um exercício com sucesso")
        void deveAtualizarExercicioComSucesso() {
            ExercicioUpdateDTO updateDTO = new ExercicioUpdateDTO();

            when(exercicioRepository.findById(1L)).thenReturn(Optional.of(exercicio));
            when(exercicioRepository.save(exercicio)).thenReturn(exercicio);
            when(exercicioMapper.toResponseDTO(exercicio)).thenReturn(responseDTO);

            ExercicioResponseDTO resultado = exercicioService.updateExercicio(1L, updateDTO);

            assertNotNull(resultado);
            verify(exercicioMapper).updateEntityFromDTO(updateDTO, exercicio);
            verify(exercicioRepository).save(exercicio);
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o ID passado para atualização for inválido")
        void deveValidarIdInvalidoNaAtualizacao() {
            ExercicioUpdateDTO dto = new ExercicioUpdateDTO();
            assertThrows(BadRequestException.class, () -> exercicioService.updateExercicio(null, dto));
            assertThrows(BadRequestException.class, () -> exercicioService.updateExercicio(0L, dto));
        }
    }

    @Nested
    @DisplayName("Cenários de Exclusão (deleteExercicio)")
    class DeleteExercicioTests {

        @Test
        @DisplayName("Deve excluir um exercício com sucesso")
        void deveDeletarExercicioComSucesso() {
            when(exercicioRepository.findById(1L)).thenReturn(Optional.of(exercicio));
            when(exercicioMapper.toResponseDTO(exercicio)).thenReturn(responseDTO);

            ExercicioResponseDTO resultado = exercicioService.deleteExercicio(1L);

            assertNotNull(resultado);
            verify(exercicioRepository, times(1)).delete(exercicio);
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o ID de exclusão for inválido")
        void deveValidarIdInvalidoNaExclusao() {
            assertThrows(BadRequestException.class, () -> exercicioService.deleteExercicio(null));
            assertThrows(BadRequestException.class, () -> exercicioService.deleteExercicio(-1L));
        }
    }
}