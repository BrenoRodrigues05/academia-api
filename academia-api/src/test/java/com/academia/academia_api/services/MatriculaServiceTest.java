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
            dto.setAlunoId(1L);
            dto.setPlanoId(5L);
            dto.setAtiva(true);

            when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
            when(matriculaRepositoy.findByAlunoIdAndAtiva(1L, true)).thenReturn(null);
            when(planoRepository.findById(5L)).thenReturn(Optional.of(plano));

            when(matriculaMapper.toEntity(dto)).thenReturn(matricula);
            when(matriculaRepositoy.save(any(Matricula.class))).thenReturn(matricula);
            when(matriculaMapper.toResponseDTO(matricula)).thenReturn(responseDTO);

            MatriculaResponseDTO resultado = matriculaService.criarMatricula(dto);

            assertNotNull(resultado);
            assertTrue(resultado.isAtiva());
            verify(matriculaRepositoy, times(1)).save(any(Matricula.class));
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se IDs de aluno ou plano forem inválidos")
        void deveValidarIdsMaioresQueZero() {
            MatriculaCreateDTO dtoSemAluno = new MatriculaCreateDTO();
            dtoSemAluno.setPlanoId(5L);

            MatriculaCreateDTO dtoSemPlano = new MatriculaCreateDTO();
            dtoSemPlano.setAlunoId(1L);

            assertThrows(BadRequestException.class, () -> matriculaService.criarMatricula(dtoSemAluno));
            assertThrows(BadRequestException.class, () -> matriculaService.criarMatricula(dtoSemPlano));
        }

        @Nested
        @DisplayName("Cenários de Edição de Plano (editarPlanoMatricula)")
        class EditarPlanoMatriculaTests {

            private Plano novoPlano;

            @BeforeEach
            void setUpEditarPlano() {
                novoPlano = new Plano();
                novoPlano.setId(8L);
            }

            @Test
            @DisplayName("Deve editar o plano da matrícula com sucesso")
            void deveEditarPlanoComSucesso() {

                when(matriculaRepositoy.findById(10L)).thenReturn(Optional.of(matricula));
                when(planoRepository.findById(8L)).thenReturn(Optional.of(novoPlano));
                when(matriculaRepositoy.save(any(Matricula.class))).thenReturn(matricula);
                when(matriculaMapper.toResponseDTO(matricula)).thenReturn(responseDTO);

                MatriculaResponseDTO resultado = matriculaService.editarPlanoMatricula(10L, 8L);

                assertNotNull(resultado);
                assertEquals(8L, matricula.getPlano().getId());
                verify(matriculaRepositoy, times(1)).findById(10L);
                verify(planoRepository, times(1)).findById(8L);
                verify(matriculaRepositoy, times(1)).save(matricula);
                verify(matriculaMapper, times(1)).toResponseDTO(matricula);
            }

            @Test
            @DisplayName("Deve lançar BadRequestException se o ID da matrícula for inválido")
            void deveValidarIdMatriculaInvalido() {
                assertThrows(BadRequestException.class, () -> matriculaService.editarPlanoMatricula(null, 8L));
                assertThrows(BadRequestException.class, () -> matriculaService.editarPlanoMatricula(0L, 8L));

                verify(matriculaRepositoy, never()).findById(any());
            }

            @Test
            @DisplayName("Deve lançar ResourceNotFoundException se a matrícula não existir")
            void deveLancarErroSeMatriculaNaoExiste() {
                // Given
                when(matriculaRepositoy.findById(99L)).thenReturn(Optional.empty());

                ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                        () -> matriculaService.editarPlanoMatricula(99L, 8L));

                assertEquals("Matrícula não encontrada com o ID: 99", exception.getMessage());
                verify(planoRepository, never()).findById(any());
                verify(matriculaRepositoy, never()).save(any());
            }

            @Test
            @DisplayName("Deve lançar ResourceNotFoundException se o plano informado não existir")
            void deveLancarErroSePlanoNaoExiste() {
                // Given
                when(matriculaRepositoy.findById(10L)).thenReturn(Optional.of(matricula));
                when(planoRepository.findById(99L)).thenReturn(Optional.empty());

                ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                        () -> matriculaService.editarPlanoMatricula(10L, 99L));

                assertEquals("Plano não encontrado com o ID: 99", exception.getMessage());
                verify(matriculaRepositoy, never()).save(any());
            }
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o aluno já possuir uma matrícula ativa")
        void deveBloquearMatriculaDuplicadaAtiva() {
            MatriculaCreateDTO dto = new MatriculaCreateDTO();
            dto.setAlunoId(1L);
            dto.setPlanoId(5L);

            when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
            when(matriculaRepositoy.findByAlunoIdAndAtiva(1L, true)).thenReturn(matricula);

            BadRequestException exception = assertThrows(BadRequestException.class,
                    () -> matriculaService.criarMatricula(dto));

            assertEquals("O aluno informado já possui uma matrícula ativa.", exception.getMessage());
            verify(matriculaRepositoy, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Cenários de Alteração de Status (alterarStatus)")
    class AlterarStatusTests {

        @Test
        @DisplayName("Deve desativar matrícula ativa com sucesso")
        void deveDesativarMatriculaComSucesso() {

            when(matriculaRepositoy.findByMatricula(10L, false)).thenReturn(matricula);

            responseDTO.setAtiva(false);
            when(matriculaRepositoy.save(matricula)).thenReturn(matricula);
            when(matriculaMapper.toResponseDTO(matricula)).thenReturn(responseDTO);

            MatriculaResponseDTO resultado = matriculaService.alterarStatus(10L, false);

            assertNotNull(resultado);
            assertFalse(resultado.isAtiva());
            verify(matriculaRepositoy).save(matricula);
        }

        @Test
        @DisplayName("Deve ativar matrícula inativa com sucesso")
        void deveAtivarMatriculaComSucesso() {
            matricula.setAtiva(false);
            when(matriculaRepositoy.findByMatricula(10L, true)).thenReturn(matricula);

            responseDTO.setAtiva(true);
            when(matriculaRepositoy.save(matricula)).thenReturn(matricula);
            when(matriculaMapper.toResponseDTO(matricula)).thenReturn(responseDTO);

            MatriculaResponseDTO resultado = matriculaService.alterarStatus(10L, true);

            assertNotNull(resultado);
            assertTrue(resultado.isAtiva());
            verify(matriculaRepositoy).save(matricula);
        }

        @Test
        @DisplayName("Deve lançar BadRequestException para ID nulo ou menor/igual a zero")
        void deveValidarIdInvalidoEmAlterarStatus() {
            assertThrows(BadRequestException.class, () -> matriculaService.alterarStatus(null, false));
            assertThrows(BadRequestException.class, () -> matriculaService.alterarStatus(0L, true));
            assertThrows(BadRequestException.class, () -> matriculaService.alterarStatus(-1L, true));
            verify(matriculaRepositoy, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se a matrícula não for encontrada")
        void deveLancarErroSeMatriculaNaoExiste() {
            when(matriculaRepositoy.findByMatricula(10L, false)).thenReturn(null);

            assertThrows(ResourceNotFoundException.class, () -> matriculaService.alterarStatus(10L, false));
            verify(matriculaRepositoy, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se o status atual já for igual ao novo status")
        void deveLancarErroSeStatusJaForOProposto() {

            when(matriculaRepositoy.findByMatricula(10L, true)).thenReturn(matricula);

            assertThrows(ResourceNotFoundException.class, () -> matriculaService.alterarStatus(10L, true));
            verify(matriculaRepositoy, never()).save(any());
        }
    }
}