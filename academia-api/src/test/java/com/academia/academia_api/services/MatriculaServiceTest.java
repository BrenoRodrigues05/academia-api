package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.MatriculaCreateDTO;
import com.academia.academia_api.DTOs.MatriculaResponseDTO;
import com.academia.academia_api.DTOs.PageResponseDTO;
import com.academia.academia_api.entity.Aluno;
import com.academia.academia_api.entity.Matricula;
import com.academia.academia_api.entity.Plano;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.infra.exceptions.ResourceNotFoundException;
import com.academia.academia_api.mappings.MatriculaMapper;
import com.academia.academia_api.repository.AlunoRepository;
import com.academia.academia_api.repository.MatriculaRepositoy;
import com.academia.academia_api.repository.PlanoRepository;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Matrículas (MatriculaService)")
class MatriculaServiceTest {

    @Mock
    private MatriculaRepositoy matriculaRepositoy;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private PlanoRepository planoRepository;

    @Mock
    private MatriculaMapper matriculaMapper;

    @InjectMocks
    private MatriculaService matriculaService;

    private Matricula matricula;
    private MatriculaResponseDTO responseDTO;
    private Aluno aluno;
    private Plano plano;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Breno Rodrigues");

        plano = new Plano();
        plano.setId(5L);

        matricula = new Matricula();
        matricula.setMatricula(10L);
        matricula.setAluno(aluno);
        matricula.setPlano(plano);
        matricula.setAtiva(true);

        responseDTO = new MatriculaResponseDTO();
        responseDTO.setMatricula(10L);
        responseDTO.setAtiva(true);
    }

    @Nested
    @DisplayName("Cenários de Listagem (listarMatriculas)")
    class ListarMatriculasTests {

        @Test
        @DisplayName("Deve retornar página de matrículas com sucesso")
        void deveRetornarPaginaDeMatriculas() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Matricula> page = new PageImpl<>(List.of(matricula));

            when(matriculaRepositoy.findAll(pageable)).thenReturn(page);
            when(matriculaMapper.toResponseDTO(matricula)).thenReturn(responseDTO);

            PageResponseDTO<MatriculaResponseDTO> resultado = matriculaService.listarMatriculas(0, 10);

            assertNotNull(resultado);
            assertEquals(1, resultado.content().size());
            verify(matriculaRepositoy, times(1)).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("Cenários de Busca por ID (findById)")
    class FindByIdTests {

        @Test
        @DisplayName("Deve retornar uma matrícula se o ID existir")
        void deveRetornarMatriculaPorId() {
            when(matriculaRepositoy.findById(10L)).thenReturn(Optional.of(matricula));
            when(matriculaMapper.toResponseDTO(matricula)).thenReturn(responseDTO);

            MatriculaResponseDTO resultado = matriculaService.findById(10L);

            assertNotNull(resultado);
            assertEquals(10L, resultado.getMatricula());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o ID for inválido")
        void deveValidarIdInvalido() {
            assertThrows(BadRequestException.class, () -> matriculaService.findById(null));
            assertThrows(BadRequestException.class, () -> matriculaService.findById(0L));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se a matrícula não existir")
        void deveLancarErroSeMatriculaNaoExiste() {
            when(matriculaRepositoy.findById(99L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> matriculaService.findById(99L));
        }
    }

    @Nested
    @DisplayName("Cenários de Criação (criarMatricula)")
    class CriarMatriculaTests {

        @Test
        @DisplayName("Deve criar matrícula com sucesso quando os dados forem válidos")
        void deveCriarMatriculaComSucesso() {
            MatriculaCreateDTO dto = new MatriculaCreateDTO();

            when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
            when(matriculaRepositoy.findByAlunoIdAndAtiva(1L, true)).thenReturn(null);
            when(planoRepository.findById(5L)).thenReturn(Optional.of(plano));

            when(matriculaMapper.toEntity(dto)).thenReturn(matricula);
            when(matriculaRepositoy.save(any(Matricula.class))).thenReturn(matricula);
            when(matriculaMapper.toResponseDTO(matricula)).thenReturn(responseDTO);

            MatriculaResponseDTO resultado = matriculaService.criarMatricula(dto, 1L, 5L);

            assertNotNull(resultado);
            assertTrue(resultado.isAtiva());
            verify(matriculaRepositoy, times(1)).save(any(Matricula.class));
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se IDs de aluno ou plano forem inválidos")
        void deveValidarIdsMaioresQueZero() {
            MatriculaCreateDTO dto = new MatriculaCreateDTO();
            assertThrows(BadRequestException.class, () -> matriculaService.criarMatricula(dto, null, 5L));
            assertThrows(BadRequestException.class, () -> matriculaService.criarMatricula(dto, 1L, 0L));
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o aluno já possuir uma matrícula ativa")
        void deveBloquearMatriculaDuplicadaAtiva() {
            MatriculaCreateDTO dto = new MatriculaCreateDTO();

            when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
            when(matriculaRepositoy.findByAlunoIdAndAtiva(1L, true)).thenReturn(matricula);

            BadRequestException exception = assertThrows(BadRequestException.class,
                    () -> matriculaService.criarMatricula(dto, 1L, 5L));

            assertEquals("O aluno informado já possui uma matrícula ativa.", exception.getMessage());
            verify(matriculaRepositoy, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Cenários de Desativação (desativarMatricula)")
    class DesativarMatriculaTests {

        @Test
        @DisplayName("Deve desativar uma matrícula ativa com sucesso")
        void deveDesativarMatriculaComSucesso() {
            when(matriculaRepositoy.findByMatriculaAndAtiva(10L, true)).thenReturn(matricula);

            matricula.setAtiva(false);
            responseDTO.setAtiva(false);

            when(matriculaRepositoy.save(matricula)).thenReturn(matricula);
            when(matriculaMapper.toResponseDTO(matricula)).thenReturn(responseDTO);

            MatriculaResponseDTO resultado = matriculaService.desativarMatricula(10L);

            assertNotNull(resultado);
            assertFalse(resultado.isAtiva());
            verify(matriculaRepositoy).save(matricula);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se a matrícula não existir ou já for inativa")
        void deveLancarErroSeInexistenteOuInativa() {
            when(matriculaRepositoy.findByMatriculaAndAtiva(10L, true)).thenReturn(null);

            assertThrows(ResourceNotFoundException.class, () -> matriculaService.desativarMatricula(10L));
            verify(matriculaRepositoy, never()).save(any());
        }
    }
}