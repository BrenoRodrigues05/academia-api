package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.PageResponseDTO;
import com.academia.academia_api.DTOs.TreinoCreateDTO;
import com.academia.academia_api.DTOs.TreinoResponseDTO;
import com.academia.academia_api.DTOs.TreinoUpdateDTO;
import com.academia.academia_api.entity.Aluno;
import com.academia.academia_api.entity.Personal;
import com.academia.academia_api.entity.Treino;
import com.academia.academia_api.entity.Usuarios;
import com.academia.academia_api.entity.enums.UserRole;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.infra.exceptions.ForbiddenException;
import com.academia.academia_api.infra.exceptions.ResourceNotFoundException;
import com.academia.academia_api.mappings.TreinoMapper;
import com.academia.academia_api.repository.AlunoRepository;
import com.academia.academia_api.repository.PersonalRepository;
import com.academia.academia_api.repository.TreinoRepository;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - TreinoService")
class TreinoServiceTest {

    @Mock private TreinoRepository treinoRepository;
    @Mock private TreinoMapper treinoMapper;
    @Mock private PersonalRepository personalRepository;
    @Mock private AlunoRepository alunoRepository;

    @InjectMocks private TreinoService treinoService;

    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    private Usuarios usuarioAdmin;
    private Usuarios usuarioPersonal;
    private Usuarios usuarioAluno;
    private Aluno alunoMock;
    private Personal personalMock;
    private Treino treinoMock;
    private TreinoResponseDTO treinoResponseDTOMock;

    @BeforeEach
    void setUp() {
        usuarioAdmin = new Usuarios(); usuarioAdmin.setId(1L); usuarioAdmin.setRole(UserRole.ADMIN);
        usuarioPersonal = new Usuarios(); usuarioPersonal.setId(2L); usuarioPersonal.setRole(UserRole.PERSONAL);
        usuarioAluno = new Usuarios(); usuarioAluno.setId(3L); usuarioAluno.setRole(UserRole.ALUNO);

        alunoMock = new Aluno(); alunoMock.setId(10L); alunoMock.setUsuario(usuarioAluno);
        personalMock = new Personal(); personalMock.setId(20L); personalMock.setUsuario(usuarioPersonal);

        treinoMock = new Treino();
        treinoMock.setId(100L);
        treinoMock.setNome("Treino A");
        treinoMock.setAtivo(true);
        treinoMock.setAluno(alunoMock);
        treinoMock.setPersonal(personalMock);

        treinoResponseDTOMock = new TreinoResponseDTO();
        treinoResponseDTOMock.setId(100L);
        treinoResponseDTOMock.setNome("Treino A");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockContextoSeguranca(Usuarios usuario) {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuario);
    }

    @Nested
    @DisplayName("Buscas paginadas e listagens simples")
    class BuscaTests {

        @Test
        @DisplayName("findAll -> Deve retornar página de DTOs mapeados")
        void findAll_DeveRetornarPageResponseDTO() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Treino> pageTreinos = new PageImpl<>(List.of(treinoMock));

            when(treinoRepository.findAll(pageable)).thenReturn(pageTreinos);
            when(treinoMapper.toResponseDTO(treinoMock)).thenReturn(treinoResponseDTOMock);

            PageResponseDTO<TreinoResponseDTO> result = treinoService.findAll(0, 10);

            assertNotNull(result);
            assertEquals(1, result.content().size());
            assertEquals(100L, result.content().get(0).getId());
        }

        @Test
        @DisplayName("findByAtivoTrue -> Deve retornar lista de treinos ativos")
        void findByAtivoTrue_DeveRetornarLista() {
            when(treinoRepository.findByAtivoTrue()).thenReturn(List.of(treinoMock));
            when(treinoMapper.toResponseDTO(treinoMock)).thenReturn(treinoResponseDTOMock);

            List<TreinoResponseDTO> result = treinoService.findByAtivoTrue();

            assertFalse(result.isEmpty());
            verify(treinoRepository).findByAtivoTrue();
        }

        @Test
        @DisplayName("findByNome -> Deve falhar se nome for vazio")
        void findByNome_DeveLancarBadRequest_QuandoNomeInvalido() {
            assertThrows(BadRequestException.class, () -> treinoService.findByNome(" "));
            verifyNoInteractions(treinoRepository);
        }

        @Test
        @DisplayName("findByNome -> Deve falhar se nenhum registro for retornado")
        void findByNome_DeveLancarResourceNotFound_QuandoListaVazia() {
            when(treinoRepository.findByNomeContainingIgnoreCase("Inexistente")).thenReturn(Collections.emptyList());

            assertThrows(ResourceNotFoundException.class, () -> treinoService.findByNome("Inexistente"));
        }

        @Test
        @DisplayName("findByPersonal -> Deve retornar lista de treinos se o ID do personal for válido")
        void findByPersonal_DeveRetornarLista_QuandoPersonalExiste() {
            Long personalId = 20L;
            when(personalRepository.existsById(personalId)).thenReturn(true);
            when(treinoRepository.findByPersonalId(personalId)).thenReturn(List.of(treinoMock));
            when(treinoMapper.toResponseDTO(treinoMock)).thenReturn(treinoResponseDTOMock);

            List<TreinoResponseDTO> result = treinoService.findByPersonal(personalId);

            assertNotNull(result);
            verify(treinoRepository).findByPersonalId(personalId);
        }
    }

    @Nested
    @DisplayName("Regras de Busca Específica e Segurança do Aluno")
    class FindTreinoAtivoAlunoTests {

        @Test
        @DisplayName("findTreinoAtivoAluno -> Deve retornar treino com sucesso se Admin visualizar")
        void findTreinoAtivoAluno_DeveRetornarTreino_ParaAdmin() {
            mockContextoSeguranca(usuarioAdmin);
            when(alunoRepository.findById(10L)).thenReturn(Optional.of(alunoMock));
            when(treinoRepository.findByAlunoIdAndAtivoTrue(10L)).thenReturn(Optional.of(treinoMock));
            when(treinoMapper.toResponseDTO(treinoMock)).thenReturn(treinoResponseDTOMock);

            TreinoResponseDTO result = treinoService.findTreinoAtivoAluno(10L);

            assertNotNull(result);
        }

        @Test
        @DisplayName("findTreinoAtivoAluno -> Deve lançar ForbiddenException se outro aluno tentar espionar")
        void findTreinoAtivoAluno_DeveLancarForbidden_ParaOutroAluno() {
            Usuarios hackerCompleto = new Usuarios(); hackerCompleto.setId(99L); hackerCompleto.setRole(UserRole.ALUNO);
            mockContextoSeguranca(hackerCompleto);

            when(alunoRepository.findById(10L)).thenReturn(Optional.of(alunoMock));

            assertThrows(ForbiddenException.class, () -> treinoService.findTreinoAtivoAluno(10L));
            verifyNoInteractions(treinoRepository);
        }
    }

    @Nested
    @DisplayName("Regras de Criação de Treino (addTreino)")
    class AddTreinoTests {

        private TreinoCreateDTO createDTO;

        @BeforeEach
        void setupDto() {
            createDTO = new TreinoCreateDTO();
            createDTO.setAlunoId(10L);
            createDTO.setNome("Novo Treino");
        }

        @Test
        @DisplayName("addTreino -> Deve cadastrar com sucesso sob papel de PERSONAL")
        void addTreino_DeveSalvarComSucesso() {
            mockContextoSeguranca(usuarioPersonal);
            when(personalRepository.findByUsuarioId(2L)).thenReturn(Optional.of(personalMock));
            when(alunoRepository.findById(10L)).thenReturn(Optional.of(alunoMock));
            when(treinoRepository.existsByAlunoIdAndAtivoTrue(10L)).thenReturn(false);
            when(treinoMapper.toEntity(createDTO)).thenReturn(treinoMock);
            when(treinoRepository.save(treinoMock)).thenReturn(treinoMock);
            when(treinoMapper.toResponseDTO(treinoMock)).thenReturn(treinoResponseDTOMock);

            TreinoResponseDTO result = treinoService.addTreino(createDTO);

            assertNotNull(result);
            verify(treinoRepository).save(treinoMock);
        }

        @Test
        @DisplayName("addTreino -> Deve bloquear criação caso o aluno já possua um treino ativo")
        void addTreino_DeveFalhar_SeAlunoJaPossuiTreinoAtivo() {
            mockContextoSeguranca(usuarioPersonal);
            when(personalRepository.findByUsuarioId(2L)).thenReturn(Optional.of(personalMock));
            when(alunoRepository.findById(10L)).thenReturn(Optional.of(alunoMock));
            when(treinoRepository.existsByAlunoIdAndAtivoTrue(10L)).thenReturn(true);

            assertThrows(BadRequestException.class, () -> treinoService.addTreino(createDTO));
            verify(treinoRepository, never()).save(any());
        }

        @Test
        @DisplayName("addTreino -> Deve barrar se o usuário logado for da role ALUNO")
        void addTreino_DeveLancarForbidden_ParaRoleAluno() {
            mockContextoSeguranca(usuarioAluno);

            assertThrows(ForbiddenException.class, () -> treinoService.addTreino(createDTO));
        }
    }

    @Nested
    @DisplayName("Regras de Edição, Status e Exclusão (Escrita Segura)")
    class MutacaoTreinoTests {

        @Test
        @DisplayName("updateTreino -> Deve permitir atualização se o usuário for o Personal dono do treino")
        void updateTreino_DeveAtualizar_QuandoForDono() {
            mockContextoSeguranca(usuarioPersonal); // ID 2
            TreinoUpdateDTO updateDTO = new TreinoUpdateDTO();

            when(treinoRepository.findById(100L)).thenReturn(Optional.of(treinoMock));
            when(treinoRepository.save(treinoMock)).thenReturn(treinoMock);
            when(treinoMapper.toResponseDTO(treinoMock)).thenReturn(treinoResponseDTOMock);

            TreinoResponseDTO result = treinoService.updateTreino(100L, updateDTO);

            assertNotNull(result);
            verify(treinoMapper).updateEntityFromDTO(updateDTO, treinoMock);
            verify(treinoRepository).save(treinoMock);
        }

        @Test
        @DisplayName("updateTreino -> Deve bloquear modificação se outro professor tentar alterar o treino")
        void updateTreino_DeveLancarForbidden_QuandoNaoForDono() {
            Usuarios outroProf = new Usuarios(); outroProf.setId(88L); outroProf.setRole(UserRole.PERSONAL);
            mockContextoSeguranca(outroProf);

            when(treinoRepository.findById(100L)).thenReturn(Optional.of(treinoMock));

            assertThrows(ForbiddenException.class, () -> treinoService.updateTreino(100L, new TreinoUpdateDTO()));
            verify(treinoRepository, never()).save(any());
        }

        @Test
        @DisplayName("alterarStatus -> Deve falhar ao mudar status para o mesmo valor atual")
        void alterarStatus_DeveLancarBadRequest_SeStatusIdentico() {
            when(treinoRepository.findById(100L)).thenReturn(Optional.of(treinoMock));

            assertThrows(BadRequestException.class, () -> treinoService.alterarStatus(100L, true));
        }

        @Test
        @DisplayName("deleteTreino -> Deve deletar registro se requisitado por um SUPER_ADMIN")
        void deleteTreino_DeveExecutarExclusao_ParaSuperAdmin() {
            Usuarios superAdmin = new Usuarios(); superAdmin.setId(9L); superAdmin.setRole(UserRole.SUPER_ADMIN);
            mockContextoSeguranca(superAdmin);

            when(treinoRepository.findById(100L)).thenReturn(Optional.of(treinoMock));
            when(treinoMapper.toResponseDTO(treinoMock)).thenReturn(treinoResponseDTOMock);

            TreinoResponseDTO result = treinoService.deleteTreino(100L);

            assertNotNull(result);
            verify(treinoRepository).delete(treinoMock);
        }
    }
}