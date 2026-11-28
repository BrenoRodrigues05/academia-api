package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.MatriculaCreateDTO;
import com.academia.academia_api.DTOs.MatriculaResponseDTO;
import com.academia.academia_api.DTOs.PageResponseDTO;
import com.academia.academia_api.entity.*;
import com.academia.academia_api.entity.enums.MatriculaStatusEnum;
import com.academia.academia_api.entity.enums.StatusPagamentoEnum;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.infra.exceptions.ResourceNotFoundException;
import com.academia.academia_api.mappings.MatriculaMapper;
import com.academia.academia_api.repository.AlunoRepository;
import com.academia.academia_api.repository.MatriculaRepositoy;
import com.academia.academia_api.repository.PagamentoRepository;
import com.academia.academia_api.repository.PlanoRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Matrículas")
class MatriculaServiceTest {

    @Mock
    private MatriculaRepositoy matriculaRepositoy;

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private PlanoRepository planoRepository;

    @Mock
    private MatriculaMapper matriculaMapper;

    @Mock
    private PagamentoService pagamentoService;

    @InjectMocks
    private MatriculaService matriculaService;

    private MockedStatic<SecurityContextHolder> mockedSecurityContextHolder;
    private SecurityContext securityContext;
    private Authentication authentication;

    private Matricula matricula;
    private MatriculaResponseDTO responseDTO;
    private Aluno aluno;
    private Plano plano;
    private Usuarios usuarioLogado;
    private Pagamento pagamento;

    @BeforeEach
    void setUp() {
        mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class);

        securityContext = mock(SecurityContext.class);
        authentication = mock(Authentication.class);

        mockedSecurityContextHolder
                .when(SecurityContextHolder::getContext)
                .thenReturn(securityContext);

        lenient().when(securityContext.getAuthentication())
                .thenReturn(authentication);

        usuarioLogado = new Usuarios();
        usuarioLogado.setId(100L);

        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Breno Rodrigues");
        aluno.setUsuario(usuarioLogado);

        plano = new Plano();
        plano.setId(5L);

        matricula = new Matricula();
        matricula.setMatricula(10L);
        matricula.setAluno(aluno);
        matricula.setPlano(plano);
        matricula.setAtiva(true);
        matricula.setStatus(MatriculaStatusEnum.ATIVA_PAGO);

        responseDTO = new MatriculaResponseDTO();
        responseDTO.setMatricula(10L);
        responseDTO.setAtiva(true);

        pagamento = new Pagamento();
        pagamento.setId(50L);
        pagamento.setCodigoPix("PIX_TESTE");
        pagamento.setQrCodeBase64("QR_TESTE");
    }

    @AfterEach
    void tearDown() {
        mockedSecurityContextHolder.close();
    }

    private void mockUsuarioLogado(Usuarios usuario) {
        lenient().when(authentication.getPrincipal()).thenReturn(usuario);
    }

    @Nested
    @DisplayName("listarMatriculas")
    class ListarMatriculasTests {

        @Test
        @DisplayName("Deve retornar página de matrículas")
        void deveRetornarPaginaDeMatriculas() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Matricula> page = new PageImpl<>(List.of(matricula));

            when(matriculaRepositoy.findAll(pageable)).thenReturn(page);
            when(matriculaMapper.toResponseDTO(matricula))
                    .thenReturn(responseDTO);

            PageResponseDTO<MatriculaResponseDTO> resultado =
                    matriculaService.listarMatriculas(0, 10);

            assertNotNull(resultado);
            assertEquals(1, resultado.content().size());

            verify(matriculaRepositoy).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("getMeuPlano")
    class GetMeuPlanoTests {

        @Test
        @DisplayName("Deve retornar a matrícula mais recente do usuário")
        void deveRetornarMatriculaMaisRecente() {
            mockUsuarioLogado(usuarioLogado);

            when(matriculaRepositoy
                    .findFirstByAlunoUsuarioIdOrderByMatriculaDesc(100L))
                    .thenReturn(Optional.of(matricula));

            when(matriculaMapper.toResponseDTO(matricula))
                    .thenReturn(responseDTO);

            MatriculaResponseDTO resultado = matriculaService.getMeuPlano();

            assertNotNull(resultado);
            assertEquals(10L, resultado.getMatricula());

            verify(matriculaRepositoy)
                    .findFirstByAlunoUsuarioIdOrderByMatriculaDesc(100L);
        }

        @Test
        @DisplayName("Deve adicionar dados do pagamento pendente ao DTO")
        void deveAdicionarDadosPagamentoPendente() {
            mockUsuarioLogado(usuarioLogado);

            when(matriculaRepositoy
                    .findFirstByAlunoUsuarioIdOrderByMatriculaDesc(100L))
                    .thenReturn(Optional.of(matricula));

            when(matriculaMapper.toResponseDTO(matricula))
                    .thenReturn(responseDTO);

            when(pagamentoRepository
                    .findFirstByMatriculaMatriculaAndStatusOrderByDataCriacaoDesc(
                            10L, StatusPagamentoEnum.PENDENTE))
                    .thenReturn(Optional.of(pagamento));

            MatriculaResponseDTO resultado = matriculaService.getMeuPlano();

            assertNotNull(resultado);
            assertEquals(50L, resultado.getPagamentoId());
            assertEquals("PIX_TESTE", resultado.getPixCopiaECola());
            assertEquals("QR_TESTE", resultado.getQrCodeBase64());
        }

        @Test
        @DisplayName("Deve retornar matrícula sem dados PIX quando não houver pagamento pendente")
        void deveRetornarMatriculaSemPagamentoPendente() {
            mockUsuarioLogado(usuarioLogado);

            when(matriculaRepositoy
                    .findFirstByAlunoUsuarioIdOrderByMatriculaDesc(100L))
                    .thenReturn(Optional.of(matricula));

            when(matriculaMapper.toResponseDTO(matricula))
                    .thenReturn(responseDTO);

            when(pagamentoRepository
                    .findFirstByMatriculaMatriculaAndStatusOrderByDataCriacaoDesc(
                            10L, StatusPagamentoEnum.PENDENTE))
                    .thenReturn(Optional.empty());

            MatriculaResponseDTO resultado = matriculaService.getMeuPlano();

            assertNotNull(resultado);
            assertEquals(10L, resultado.getMatricula());
        }

        @Test
        @DisplayName("Deve lançar exceção quando não houver matrícula")
        void deveLancarExcecaoQuandoNaoEncontrarMatricula() {
            mockUsuarioLogado(usuarioLogado);

            when(matriculaRepositoy
                    .findFirstByAlunoUsuarioIdOrderByMatriculaDesc(100L))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> matriculaService.getMeuPlano());
        }
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTests {

        @Test
        @DisplayName("Deve retornar matrícula por ID")
        void deveRetornarMatriculaPorId() {
            when(matriculaRepositoy.findById(10L))
                    .thenReturn(Optional.of(matricula));

            when(matriculaMapper.toResponseDTO(matricula))
                    .thenReturn(responseDTO);

            MatriculaResponseDTO resultado = matriculaService.findById(10L);

            assertNotNull(resultado);
            assertEquals(10L, resultado.getMatricula());
        }

        @Test
        @DisplayName("Deve validar ID nulo ou inválido")
        void deveValidarIdInvalido() {
            assertThrows(BadRequestException.class,
                    () -> matriculaService.findById(null));

            assertThrows(BadRequestException.class,
                    () -> matriculaService.findById(0L));

            assertThrows(BadRequestException.class,
                    () -> matriculaService.findById(-1L));
        }

        @Test
        @DisplayName("Deve lançar exceção quando matrícula não existir")
        void deveLancarExcecaoQuandoMatriculaNaoExistir() {
            when(matriculaRepositoy.findById(99L))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> matriculaService.findById(99L));
        }
    }

    @Nested
    @DisplayName("criarMatricula")
    class CriarMatriculaTests {

        @Test
        @DisplayName("Deve criar matrícula no fluxo administrativo")
        void deveCriarMatriculaFluxoAdmin() {
            MatriculaCreateDTO dto = new MatriculaCreateDTO();
            dto.setAlunoId(1L);
            dto.setPlanoId(5L);

            Matricula matriculaNova = new Matricula();
            matriculaNova.setMatricula(10L);
            matriculaNova.setAluno(aluno);
            matriculaNova.setPlano(plano);
            matriculaNova.setAtiva(false);
            matriculaNova.setStatus(
                    MatriculaStatusEnum.AGUARDANDO_PRIMEIRO_PAGAMENTO);

            MatriculaResponseDTO dtoBase = new MatriculaResponseDTO();
            dtoBase.setMatricula(10L);
            dtoBase.setAtiva(false);

            when(alunoRepository.findById(1L))
                    .thenReturn(Optional.of(aluno));

            when(matriculaRepositoy.findByAlunoIdAndAtiva(1L, true))
                    .thenReturn(null);

            when(planoRepository.findById(5L))
                    .thenReturn(Optional.of(plano));

            when(matriculaMapper.toEntity(dto))
                    .thenReturn(matriculaNova);

            when(matriculaRepositoy.save(any(Matricula.class)))
                    .thenReturn(matriculaNova);

            when(pagamentoService.criarPagamentoPix(matriculaNova))
                    .thenReturn(pagamento);

            when(matriculaMapper.toResponseDTO(matriculaNova))
                    .thenReturn(dtoBase);

            MatriculaResponseDTO resultado =
                    matriculaService.criarMatricula(dto);

            assertNotNull(resultado);
            assertFalse(resultado.isAtiva());
            assertEquals(50L, resultado.getPagamentoId());
            assertEquals("PIX_TESTE", resultado.getPixCopiaECola());
            assertEquals("QR_TESTE", resultado.getQrCodeBase64());

            assertEquals(
                    MatriculaStatusEnum.AGUARDANDO_PRIMEIRO_PAGAMENTO,
                    matriculaNova.getStatus());

            verify(alunoRepository).findById(1L);
            verify(matriculaRepositoy).save(any(Matricula.class));
            verify(pagamentoService).criarPagamentoPix(matriculaNova);
        }

        @Test
        @DisplayName("Deve criar matrícula para o aluno logado")
        void deveCriarMatriculaAlunoLogado() {
            mockUsuarioLogado(usuarioLogado);

            MatriculaCreateDTO dto = new MatriculaCreateDTO();
            dto.setPlanoId(5L);

            Matricula matriculaNova = new Matricula();
            matriculaNova.setMatricula(10L);
            matriculaNova.setAluno(aluno);
            matriculaNova.setPlano(plano);
            matriculaNova.setAtiva(false);

            when(alunoRepository.findByUsuarioId(100L))
                    .thenReturn(Optional.of(aluno));

            when(matriculaRepositoy.findByAlunoIdAndAtiva(1L, true))
                    .thenReturn(null);

            when(planoRepository.findById(5L))
                    .thenReturn(Optional.of(plano));

            when(matriculaMapper.toEntity(dto))
                    .thenReturn(matriculaNova);

            when(matriculaRepositoy.save(any(Matricula.class)))
                    .thenReturn(matriculaNova);

            when(pagamentoService.criarPagamentoPix(matriculaNova))
                    .thenReturn(pagamento);

            when(matriculaMapper.toResponseDTO(matriculaNova))
                    .thenReturn(responseDTO);

            MatriculaResponseDTO resultado =
                    matriculaService.criarMatricula(dto);

            assertNotNull(resultado);
            assertEquals(50L, resultado.getPagamentoId());

            verify(alunoRepository).findByUsuarioId(100L);
            verify(alunoRepository, never()).findById(anyLong());
        }

        @Test
        @DisplayName("Deve rejeitar ID de plano inválido")
        void deveValidarPlanoIdInvalido() {
            MatriculaCreateDTO dto = new MatriculaCreateDTO();

            assertThrows(BadRequestException.class,
                    () -> matriculaService.criarMatricula(dto));

            dto.setPlanoId(0L);

            assertThrows(BadRequestException.class,
                    () -> matriculaService.criarMatricula(dto));

            verify(pagamentoService, never()).criarPagamentoPix(any());
        }

        @Test
        @DisplayName("Deve rejeitar matrícula ativa duplicada")
        void deveBloquearMatriculaDuplicada() {
            MatriculaCreateDTO dto = new MatriculaCreateDTO();
            dto.setAlunoId(1L);
            dto.setPlanoId(5L);

            when(alunoRepository.findById(1L))
                    .thenReturn(Optional.of(aluno));

            when(matriculaRepositoy.findByAlunoIdAndAtiva(1L, true))
                    .thenReturn(matricula);

            assertThrows(BadRequestException.class,
                    () -> matriculaService.criarMatricula(dto));

            verify(matriculaRepositoy, never()).save(any());
            verify(pagamentoService, never()).criarPagamentoPix(any());
        }

        @Test
        @DisplayName("Deve lançar exceção se aluno não existir")
        void deveLancarExcecaoAlunoNaoEncontrado() {
            MatriculaCreateDTO dto = new MatriculaCreateDTO();
            dto.setAlunoId(99L);
            dto.setPlanoId(5L);

            when(alunoRepository.findById(99L))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> matriculaService.criarMatricula(dto));
        }

        @Test
        @DisplayName("Deve lançar exceção se plano não existir")
        void deveLancarExcecaoPlanoNaoEncontrado() {
            MatriculaCreateDTO dto = new MatriculaCreateDTO();
            dto.setAlunoId(1L);
            dto.setPlanoId(99L);

            when(alunoRepository.findById(1L))
                    .thenReturn(Optional.of(aluno));

            when(matriculaRepositoy.findByAlunoIdAndAtiva(1L, true))
                    .thenReturn(null);

            when(planoRepository.findById(99L))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> matriculaService.criarMatricula(dto));

            verify(matriculaRepositoy, never()).save(any());
        }
    }

    @Nested
    @DisplayName("agendarTrocaPlano")
    class AgendarTrocaPlanoTests {

        private Plano novoPlano;

        @BeforeEach
        void setUp() {
            novoPlano = new Plano();
            novoPlano.setId(8L);
        }

        @Test
        @DisplayName("Deve agendar troca de plano")
        void deveAgendarTrocaPlanoComSucesso() {
            when(matriculaRepositoy.findById(10L))
                    .thenReturn(Optional.of(matricula));

            when(planoRepository.findById(8L))
                    .thenReturn(Optional.of(novoPlano));

            when(matriculaRepositoy.save(matricula))
                    .thenReturn(matricula);

            when(matriculaMapper.toResponseDTO(matricula))
                    .thenReturn(responseDTO);

            MatriculaResponseDTO resultado =
                    matriculaService.agendarTrocaPlano(10L, 8L);

            assertNotNull(resultado);
            assertEquals(8L, matricula.getPlanoAgendado().getId());

            assertEquals(5L, matricula.getPlano().getId());

            verify(matriculaRepositoy).save(matricula);
        }

        @Test
        @DisplayName("Deve rejeitar troca quando matrícula não estiver paga")
        void deveRejeitarTrocaPlanoMatriculaNaoPaga() {
            matricula.setStatus(
                    MatriculaStatusEnum.ATIVA_PENDENTE_PAGAMENTO);

            when(matriculaRepositoy.findById(10L))
                    .thenReturn(Optional.of(matricula));

            assertThrows(BadRequestException.class,
                    () -> matriculaService.agendarTrocaPlano(10L, 8L));

            verify(planoRepository, never()).findById(8L);
            verify(matriculaRepositoy, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção se matrícula não existir")
        void deveLancarExcecaoMatriculaNaoEncontrada() {
            when(matriculaRepositoy.findById(99L))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> matriculaService.agendarTrocaPlano(99L, 8L));
        }

        @Test
        @DisplayName("Deve lançar exceção se plano não existir")
        void deveLancarExcecaoPlanoNaoEncontrado() {
            when(matriculaRepositoy.findById(10L))
                    .thenReturn(Optional.of(matricula));

            when(planoRepository.findById(99L))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> matriculaService.agendarTrocaPlano(10L, 99L));

            verify(matriculaRepositoy, never()).save(any());
        }
    }

    @Nested
    @DisplayName("cancelarAssinatura")
    class CancelarAssinaturaTests {

        @Test
        @DisplayName("Deve agendar cancelamento da assinatura")
        void deveAgendarCancelamentoComSucesso() {
            when(matriculaRepositoy.findById(10L))
                    .thenReturn(Optional.of(matricula));

            when(matriculaRepositoy.save(matricula))
                    .thenReturn(matricula);

            when(matriculaMapper.toResponseDTO(matricula))
                    .thenReturn(responseDTO);

            MatriculaResponseDTO resultado =
                    matriculaService.cancelarAssinatura(10L);

            assertNotNull(resultado);

            assertEquals(
                    MatriculaStatusEnum.ATIVA_CANCELAMENTO_AGENDADO,
                    matricula.getStatus());

            assertNull(matricula.getPlanoAgendado());

            verify(matriculaRepositoy).save(matricula);
        }

        @Test
        @DisplayName("Deve rejeitar cancelamento quando matrícula não estiver paga")
        void deveRejeitarCancelamentoMatriculaNaoPaga() {
            matricula.setStatus(
                    MatriculaStatusEnum.ATIVA_PENDENTE_PAGAMENTO);

            when(matriculaRepositoy.findById(10L))
                    .thenReturn(Optional.of(matricula));

            assertThrows(BadRequestException.class,
                    () -> matriculaService.cancelarAssinatura(10L));

            verify(matriculaRepositoy, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção se matrícula não existir")
        void deveLancarExcecaoMatriculaNaoEncontrada() {
            when(matriculaRepositoy.findById(99L))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> matriculaService.cancelarAssinatura(99L));
        }
    }

    @Nested
    @DisplayName("reativar")
    class ReativarTests {

        @Test
        @DisplayName("Deve reativar matrícula inativa e gerar PIX")
        void deveReativarMatriculaComSucesso() {
            matricula.setAtiva(false);
            matricula.setStatus(MatriculaStatusEnum.INATIVA);

            when(matriculaRepositoy.findById(10L))
                    .thenReturn(Optional.of(matricula));

            when(matriculaRepositoy.save(matricula))
                    .thenReturn(matricula);

            when(pagamentoService.criarPagamentoPix(matricula))
                    .thenReturn(pagamento);

            when(matriculaMapper.toResponseDTO(matricula))
                    .thenReturn(responseDTO);

            MatriculaResponseDTO resultado =
                    matriculaService.reativar(10L);

            assertNotNull(resultado);

            assertEquals(
                    MatriculaStatusEnum.INATIVA_PENDENTE_PAGAMENTO,
                    matricula.getStatus());

            assertEquals(50L, resultado.getPagamentoId());
            assertEquals("PIX_TESTE", resultado.getPixCopiaECola());
            assertEquals("QR_TESTE", resultado.getQrCodeBase64());

            verify(matriculaRepositoy).save(matricula);
            verify(pagamentoService).criarPagamentoPix(matricula);
        }

        @Test
        @DisplayName("Deve rejeitar reativação de matrícula que não esteja inativa")
        void deveRejeitarReativacaoMatriculaNaoInativa() {
            matricula.setStatus(MatriculaStatusEnum.ATIVA_PAGO);

            when(matriculaRepositoy.findById(10L))
                    .thenReturn(Optional.of(matricula));

            assertThrows(BadRequestException.class,
                    () -> matriculaService.reativar(10L));

            verify(matriculaRepositoy, never()).save(any());
            verify(pagamentoService, never()).criarPagamentoPix(any());
        }

        @Test
        @DisplayName("Deve lançar exceção se matrícula não existir")
        void deveLancarExcecaoMatriculaNaoEncontrada() {
            when(matriculaRepositoy.findById(99L))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> matriculaService.reativar(99L));
        }
    }

    @Nested
    @DisplayName("definirDiaVencimento")
    class DefinirDiaVencimentoTests {

        @Test
        @DisplayName("Deve rejeitar dia de vencimento inválido")
        void deveRejeitarDiaVencimentoInvalido() {
            assertThrows(BadRequestException.class,
                    () -> matriculaService.definirDiaVencimento(10L, null));

            assertThrows(BadRequestException.class,
                    () -> matriculaService.definirDiaVencimento(10L, 2));

            assertThrows(BadRequestException.class,
                    () -> matriculaService.definirDiaVencimento(10L, 30));
        }

        @Test
        @DisplayName("Deve rejeitar definição de vencimento se matrícula não estiver paga")
        void deveRejeitarDefinicaoVencimentoMatriculaNaoPaga() {
            matricula.setStatus(
                    MatriculaStatusEnum.ATIVA_PENDENTE_PAGAMENTO);

            when(matriculaRepositoy.findById(10L))
                    .thenReturn(Optional.of(matricula));

            assertThrows(BadRequestException.class,
                    () -> matriculaService.definirDiaVencimento(10L, 10));

            verify(matriculaRepositoy, never()).save(any());
        }

        @Test
        @DisplayName("Deve rejeitar quando dia de vencimento já estiver definido")
        void deveRejeitarDiaVencimentoJaDefinido() {
            matricula.setStatus(MatriculaStatusEnum.ATIVA_PAGO);
            matricula.setDiaVencimento(10);

            when(matriculaRepositoy.findById(10L))
                    .thenReturn(Optional.of(matricula));

            assertThrows(BadRequestException.class,
                    () -> matriculaService.definirDiaVencimento(10L, 15));

            verify(matriculaRepositoy, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção se matrícula não existir")
        void deveLancarExcecaoMatriculaNaoEncontrada() {
            when(matriculaRepositoy.findById(99L))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> matriculaService.definirDiaVencimento(99L, 10));
        }
    }

    @Nested
    @DisplayName("alterarStatus")
    class AlterarStatusTests {

        @Test
        @DisplayName("Deve desativar matrícula")
        void deveDesativarMatricula() {
            when(matriculaRepositoy.findByMatricula(10L, false))
                    .thenReturn(matricula);

            when(matriculaRepositoy.save(matricula))
                    .thenReturn(matricula);

            responseDTO.setAtiva(false);

            when(matriculaMapper.toResponseDTO(matricula))
                    .thenReturn(responseDTO);

            MatriculaResponseDTO resultado =
                    matriculaService.alterarStatus(10L, false);

            assertNotNull(resultado);
            assertFalse(resultado.isAtiva());

            verify(matriculaRepositoy).save(matricula);
        }

        @Test
        @DisplayName("Deve ativar matrícula")
        void deveAtivarMatricula() {
            matricula.setAtiva(false);

            when(matriculaRepositoy.findByMatricula(10L, true))
                    .thenReturn(matricula);

            when(matriculaRepositoy.save(matricula))
                    .thenReturn(matricula);

            responseDTO.setAtiva(true);

            when(matriculaMapper.toResponseDTO(matricula))
                    .thenReturn(responseDTO);

            MatriculaResponseDTO resultado =
                    matriculaService.alterarStatus(10L, true);

            assertNotNull(resultado);
            assertTrue(resultado.isAtiva());

            verify(matriculaRepositoy).save(matricula);
        }

        @Test
        @DisplayName("Deve validar ID inválido")
        void deveValidarIdInvalido() {
            assertThrows(BadRequestException.class,
                    () -> matriculaService.alterarStatus(null, false));

            assertThrows(BadRequestException.class,
                    () -> matriculaService.alterarStatus(0L, true));

            assertThrows(BadRequestException.class,
                    () -> matriculaService.alterarStatus(-1L, true));

            verify(matriculaRepositoy, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando matrícula não for encontrada")
        void deveLancarExcecaoMatriculaNaoEncontrada() {
            when(matriculaRepositoy.findByMatricula(10L, false))
                    .thenReturn(null);

            assertThrows(ResourceNotFoundException.class,
                    () -> matriculaService.alterarStatus(10L, false));

            verify(matriculaRepositoy, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando status já for o solicitado")
        void deveLancarExcecaoStatusJaSelecionado() {
            when(matriculaRepositoy.findByMatricula(10L, true))
                    .thenReturn(matricula);

            assertThrows(ResourceNotFoundException.class,
                    () -> matriculaService.alterarStatus(10L, true));

            verify(matriculaRepositoy, never()).save(any());
        }
    }
}