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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TreinoServiceTest {

    @Mock
    private TreinoRepository treinoRepository;
    @Mock
    private TreinoMapper treinoMapper;
    @Mock
    private PersonalRepository personalRepository;
    @Mock
    private AlunoRepository alunoRepository;

    @InjectMocks
    private TreinoService treinoService;

    private MockedStatic<SecurityContextHolder> mockedSecurityContextHolder;
    private SecurityContext securityContext;
    private Authentication authentication;

    private Usuarios usuarioLogado;
    private Aluno alunoExemplo;
    private Personal personalExemplo;
    private Treino treinoExemplo;

    @BeforeEach
    void setUp() {
        mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class);
        securityContext = mock(SecurityContext.class);
        authentication = mock(Authentication.class);
        mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        usuarioLogado = new Usuarios();
        usuarioLogado.setId(10L);
        usuarioLogado.setRole(UserRole.ADMIN);

        alunoExemplo = new Aluno();
        alunoExemplo.setId(1L);

        personalExemplo = new Personal();
        personalExemplo.setId(2L);
        personalExemplo.setUsuario(usuarioLogado);

        treinoExemplo = new Treino();
        treinoExemplo.setId(5L);
        treinoExemplo.setNome("Treino de Hipertrofia ABC");
        treinoExemplo.setAluno(alunoExemplo);
        treinoExemplo.setPersonal(personalExemplo);
        treinoExemplo.setAtivo(true);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityContextHolder.close();
    }
    private void mockUsuarioLogado(Usuarios usuario) {
        lenient().when(authentication.getPrincipal()).thenReturn(usuario);
    }

    @Test
    @DisplayName("Deve retornar uma pagina de treinos convertida em DTO com sucesso")
    void findAll_DeveRetornarPageResponseDTOSucesso() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Treino> pageTreinos = new PageImpl<>(List.of(treinoExemplo), pageable, 1);
        TreinoResponseDTO responseDTO = new TreinoResponseDTO();

        when(treinoRepository.findAll(pageable)).thenReturn(pageTreinos);
        when(treinoMapper.toResponseDTO(treinoExemplo)).thenReturn(responseDTO);

        PageResponseDTO<TreinoResponseDTO> resultado = treinoService.findAll(0, 10);

        assertNotNull(resultado);
        assertEquals(1, resultado.totalElements());
        verify(treinoRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve buscar treino por ID com sucesso se tiver permissao")
    void findById_DeveRetornarTreinoResponseDTOSucesso() {
        mockUsuarioLogado(usuarioLogado);
        when(treinoRepository.findById(5L)).thenReturn(Optional.of(treinoExemplo));
        when(treinoMapper.toResponseDTO(treinoExemplo)).thenReturn(new TreinoResponseDTO());

        TreinoResponseDTO resultado = treinoService.findById(5L);

        assertNotNull(resultado);
        verify(treinoRepository, times(1)).findById(5L);
    }

    @Test
    @DisplayName("Deve lancar BadRequestException quando o ID informado for invalido")
    void findById_DeveLancarBadRequestExceptionQuandoIdInvalido() {
        assertThrows(BadRequestException.class, () -> treinoService.findById(0L));
        assertThrows(BadRequestException.class, () -> treinoService.findById(null));
    }

    @Test
    @DisplayName("Deve lancar ResourceNotFoundException quando o treino nao existir")
    void findById_DeveLancarResourceNotFoundExceptionQuandoNaoEncontrado() {
        when(treinoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> treinoService.findById(99L));
    }

    @Test
    @DisplayName("Deve lancar ForbiddenException quando o Aluno tentar ver treino de outra pessoa")
    void findById_DeveLancarForbiddenExceptionQuandoAlunoNaoForDono() {
        Usuarios usuarioAlunoOutro = new Usuarios();
        usuarioAlunoOutro.setId(50L);
        usuarioAlunoOutro.setRole(UserRole.ALUNO);
        mockUsuarioLogado(usuarioAlunoOutro);

        when(treinoRepository.findById(5L)).thenReturn(Optional.of(treinoExemplo));

        assertThrows(ForbiddenException.class, () -> treinoService.findById(5L));
    }

    @Test
    @DisplayName("Deve buscar treinos por nome com sucesso")
    void findByNome_DeveRetornarListaSucesso() {
        when(treinoRepository.findByNomeContainingIgnoreCase("Hipertrofia")).thenReturn(List.of(treinoExemplo));
        when(treinoMapper.toResponseDTO(treinoExemplo)).thenReturn(new TreinoResponseDTO());

        List<TreinoResponseDTO> resultado = treinoService.findByNome("Hipertrofia");

        assertFalse(resultado.isEmpty());
        verify(treinoRepository, times(1)).findByNomeContainingIgnoreCase("Hipertrofia");
    }

    @Test
    @DisplayName("Deve lancar BadRequestException quando o termo de busca for vazio ou nulo")
    void findByNome_DeveLancarBadRequestException() {
        assertThrows(BadRequestException.class, () -> treinoService.findByNome(""));
        assertThrows(BadRequestException.class, () -> treinoService.findByNome("   "));
    }

    @Test
    @DisplayName("Deve salvar novo treino com sucesso quando disparado por um Personal")
    void addTreino_DeveSalvarComSucessoQuandoProfessorLogado() {
        usuarioLogado.setRole(UserRole.PERSONAL);
        mockUsuarioLogado(usuarioLogado);

        TreinoCreateDTO dto = new TreinoCreateDTO();
        dto.setAlunoId(1L);

        when(personalRepository.findByUsuarioId(usuarioLogado.getId())).thenReturn(Optional.of(personalExemplo));
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(alunoExemplo));
        when(treinoRepository.existsByAlunoIdAndAtivoTrue(1L)).thenReturn(false);
        when(treinoMapper.toEntity(dto)).thenReturn(treinoExemplo);
        when(treinoRepository.save(any(Treino.class))).thenReturn(treinoExemplo);
        when(treinoMapper.toResponseDTO(treinoExemplo)).thenReturn(new TreinoResponseDTO());

        TreinoResponseDTO resultado = treinoService.addTreino(dto);

        assertNotNull(resultado);
        verify(treinoRepository, times(1)).save(any(Treino.class));
    }

    @Test
    @DisplayName("Deve lancar ForbiddenException ao tentar cadastrar treino sem perfil elegivel")
    void addTreino_DeveLancarForbiddenExceptionParaPerfilInvalido() {
        usuarioLogado.setRole(UserRole.ALUNO);
        mockUsuarioLogado(usuarioLogado);

        TreinoCreateDTO dto = new TreinoCreateDTO();

        assertThrows(ForbiddenException.class, () -> treinoService.addTreino(dto));
    }

    @Test
    @DisplayName("Deve lancar BadRequestException se o aluno selecionado ja possuir treino ativo")
    void addTreino_DeveLancarBadRequestExceptionSeAlunoJaPossuiTreinoAtivo() {
        mockUsuarioLogado(usuarioLogado);
        TreinoCreateDTO dto = new TreinoCreateDTO();
        dto.setAlunoId(1L);

        when(personalRepository.findByUsuarioId(usuarioLogado.getId())).thenReturn(Optional.of(personalExemplo));
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(alunoExemplo));
        when(treinoRepository.existsByAlunoIdAndAtivoTrue(1L)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> treinoService.addTreino(dto));
    }

    @Test
    @DisplayName("Deve atualizar treino com sucesso se for o Personal criador do treino")
    void updateTreino_DeveAtualizarComSucessoSeForDono() {
        usuarioLogado.setRole(UserRole.PERSONAL);
        mockUsuarioLogado(usuarioLogado);

        TreinoUpdateDTO updateDTO = new TreinoUpdateDTO();

        when(treinoRepository.findById(5L)).thenReturn(Optional.of(treinoExemplo));
        when(treinoRepository.save(treinoExemplo)).thenReturn(treinoExemplo);
        when(treinoMapper.toResponseDTO(treinoExemplo)).thenReturn(new TreinoResponseDTO());

        TreinoResponseDTO resultado = treinoService.updateTreino(5L, updateDTO);

        assertNotNull(resultado);
        verify(treinoMapper, times(1)).updateEntityFromDTO(updateDTO, treinoExemplo);
        verify(treinoRepository, times(1)).save(treinoExemplo);
    }

    @Test
    @DisplayName("Deve barrar atualizacao com ForbiddenException se o Personal tentar mexer no treino de outro colega")
    void updateTreino_DeveLancarForbiddenExceptionSeNaoForDono() {
        Usuarios outroPersonalLogado = new Usuarios();
        outroPersonalLogado.setId(99L);
        outroPersonalLogado.setRole(UserRole.PERSONAL);
        mockUsuarioLogado(outroPersonalLogado);

        when(treinoRepository.findById(5L)).thenReturn(Optional.of(treinoExemplo));

        assertThrows(ForbiddenException.class, () -> treinoService.updateTreino(5L, new TreinoUpdateDTO()));
    }

    @Test
    @DisplayName("Deve desativar treino setando a data de fim para a data atual")
    void alterarStatus_DeveDesativarTreinoComSucesso() {
        mockUsuarioLogado(usuarioLogado);
        treinoExemplo.setAtivo(true);

        when(treinoRepository.findById(5L)).thenReturn(Optional.of(treinoExemplo));
        when(treinoMapper.toResponseDTO(treinoExemplo)).thenReturn(new TreinoResponseDTO());

        treinoService.alterarStatus(5L, false);

        assertFalse(treinoExemplo.getAtivo());
        assertEquals(LocalDate.now(), treinoExemplo.getDataFim());
        verify(treinoRepository, times(1)).save(treinoExemplo);
    }

    @Test
    @DisplayName("Deve lancar BadRequestException ao tentar mudar status para o mesmo estado que ja se encontra")
    void alterarStatus_DeveLancarBadRequestExceptionSeStatusForIgual() {
        mockUsuarioLogado(usuarioLogado);
        treinoExemplo.setAtivo(true);

        when(treinoRepository.findById(5L)).thenReturn(Optional.of(treinoExemplo));

        assertThrows(BadRequestException.class, () -> treinoService.alterarStatus(5L, true));
    }

    @Test
    @DisplayName("Deve excluir treino com sucesso se o usuario for um Administrador")
    void deleteTreino_DeveExcluirComSucesso() {
        mockUsuarioLogado(usuarioLogado);

        when(treinoRepository.findById(5L)).thenReturn(Optional.of(treinoExemplo));

        treinoService.deleteTreino(5L);

        verify(treinoRepository, times(1)).delete(treinoExemplo);
    }
}