package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.ExecucaoTreinoResponseDTO;
import com.academia.academia_api.entity.Aluno;
import com.academia.academia_api.entity.ExecucaoTreino;
import com.academia.academia_api.entity.Treino;
import com.academia.academia_api.entity.Usuarios;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.infra.exceptions.ResourceNotFoundException;
import com.academia.academia_api.mappings.ExecucaoTreinoMapper;
import com.academia.academia_api.repository.ExecucaoTreinoRepository;
import com.academia.academia_api.repository.TreinoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Execução de Treino")
class ExecucaoTreinoServiceTest {

    @Mock
    private ExecucaoTreinoRepository execucaoTreinoRepository;

    @Mock
    private TreinoRepository treinoRepository;

    @Mock
    private ExecucaoTreinoMapper mapper;

    @InjectMocks
    private ExecucaoTreinoService execucaoTreinoService;

    private Usuarios usuarioLogado;
    private Treino treinoDoUsuario;
    private Long treinoId;

    @BeforeEach
    void setUp() {
        treinoId = 1L;

        usuarioLogado = new Usuarios();
        usuarioLogado.setId(10L);

        Aluno aluno = new Aluno();
        aluno.setUsuario(usuarioLogado);

        treinoDoUsuario = new Treino();
        treinoDoUsuario.setId(treinoId);
        treinoDoUsuario.setAluno(aluno);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuarioLogado);

        SecurityContextHolder.setContext(securityContext);
    }

    @Nested
    @DisplayName("Cenários de Sucesso")
    class SucessoCenarios {

        @Test
        @DisplayName("Deve iniciar um treino com sucesso quando os dados forem válidos e pertencerem ao usuário logado")
        void deveIniciarTreinoComSucesso() {

            ExecucaoTreino execucaoSalva = new ExecucaoTreino();
            ExecucaoTreinoResponseDTO responseDTO = new ExecucaoTreinoResponseDTO();

            when(treinoRepository.findById(treinoId)).thenReturn(Optional.of(treinoDoUsuario));
            when(execucaoTreinoRepository.save(any(ExecucaoTreino.class))).thenReturn(execucaoSalva);
            when(mapper.toResponseDTO(execucaoSalva)).thenReturn(responseDTO);

            ExecucaoTreinoResponseDTO resultado = execucaoTreinoService.iniciarTreino(treinoId);

            assertNotNull(resultado);
            ArgumentCaptor<ExecucaoTreino> execucaoCaptor = ArgumentCaptor.forClass(ExecucaoTreino.class);
            verify(execucaoTreinoRepository, times(1)).save(execucaoCaptor.capture());

            ExecucaoTreino execucaoGerada = execucaoCaptor.getValue();
            assertEquals(treinoDoUsuario, execucaoGerada.getTreino());
            assertEquals(treinoDoUsuario.getAluno(), execucaoGerada.getAluno());
            assertFalse(execucaoGerada.getConcluido());
            assertNotNull(execucaoGerada.getDataExecucao());

            verify(treinoRepository, times(1)).findById(treinoId);
            verify(mapper, times(1)).toResponseDTO(execucaoSalva);
        }
    }

    @Nested
    @DisplayName("Cenários de Falha / Exceções")
    class FalhaCenarios {

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o ID do treino não existir")
        void deveLancaoExcecaoQuandoTreinoNaoExistir() {

            when(treinoRepository.findById(treinoId)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                execucaoTreinoService.iniciarTreino(treinoId);
            });

            assertEquals("Treino não encontrado.", exception.getMessage());
            verify(treinoRepository, times(1)).findById(treinoId);
            verifyNoInteractions(execucaoTreinoRepository, mapper);
        }

        @Test
        @DisplayName("Deve lançar BadRequestException quando o treino pertencer a outro aluno")
        void deveLancarExcecaoQuandoTreinoForDeOutroUsuario() {
            Usuarios outroUsuario = new Usuarios();
            outroUsuario.setId(99L);

            Aluno outroAluno = new Aluno();
            outroAluno.setUsuario(outroUsuario);

            Treino treinoDeOutro = new Treino();
            treinoDeOutro.setId(treinoId);
            treinoDeOutro.setAluno(outroAluno);

            when(treinoRepository.findById(treinoId)).thenReturn(Optional.of(treinoDeOutro));

            BadRequestException exception = assertThrows(BadRequestException.class, () -> {
                execucaoTreinoService.iniciarTreino(treinoId);
            });

            assertEquals("Você só pode executar seus próprios treinos.", exception.getMessage());
            verify(treinoRepository, times(1)).findById(treinoId);
            verifyNoInteractions(execucaoTreinoRepository, mapper);
        }
    }
}