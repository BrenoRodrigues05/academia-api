package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.AlunoCreateDTO;
import com.academia.academia_api.DTOs.AlunoResponseDTO;
import com.academia.academia_api.DTOs.AlunoUpdateDTO;
import com.academia.academia_api.DTOs.PageResponseDTO;
import com.academia.academia_api.entity.Aluno;
import com.academia.academia_api.entity.Usuarios;
import com.academia.academia_api.entity.enums.SexoEnum;
import com.academia.academia_api.entity.enums.UserRole;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.infra.exceptions.ForbiddenException;
import com.academia.academia_api.infra.exceptions.ResourceNotFoundException;
import com.academia.academia_api.mappings.AlunoMapper;
import com.academia.academia_api.repository.AlunoRepository;
import com.academia.academia_api.repository.UsuarioRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Alunos")
class AlunoServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private AlunoMapper alunoMapper;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AlunoService alunoService;

    private Aluno aluno;
    private AlunoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("João Paulo");
        aluno.setEmail("joao@email.com");
        aluno.setSexo(SexoEnum.MASCULINO);
        aluno.setDataNascimento(LocalDate.of(1999, 12, 5));

        Usuarios usuarioMock = new Usuarios();
        usuarioMock.setId(10L);
        usuarioMock.setLogin("joao@email.com");
        aluno.setUsuario(usuarioMock);

        responseDTO = new AlunoResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("João Paulo");
        responseDTO.setEmail("joao@email.com");
    }

    private void mockUsuarioLogado(Long id, UserRole role) {
        Usuarios usuarioLogado = new Usuarios();
        usuarioLogado.setId(id);
        usuarioLogado.setRole(role);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(usuarioLogado);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Nested
    @DisplayName("Cenários de Busca Geral (findAll)")
    class FindAllTests {

        @Test
        @DisplayName("Deve retornar uma página de alunos com sucesso")
        void deveRetornarPaginaDeAlunos() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Aluno> page = new PageImpl<>(List.of(aluno));

            when(alunoRepository.findAll(pageable)).thenReturn(page);
            when(alunoMapper.toResponseDTO(aluno)).thenReturn(responseDTO);

            PageResponseDTO<AlunoResponseDTO> resultado = alunoService.findAll(0, 10);

            assertNotNull(resultado);
            assertEquals(1, resultado.content().size());
            assertEquals(0, resultado.page());
            assertEquals(1, resultado.totalElements());
            verify(alunoRepository, times(1)).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("Cenários de Busca por ID")
    class FindByIdTests {

        @Test
        @DisplayName("Deve retornar um aluno se o ID existir")
        void deveRetornarAlunoPorId() {
            when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
            when(alunoMapper.toResponseDTO(aluno)).thenReturn(responseDTO);

            AlunoResponseDTO resultado = alunoService.findById(1L);

            assertNotNull(resultado);
            assertEquals("João Paulo", resultado.getNome());
            verify(alunoRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o ID fornecido for nulo")
        void deveLancarErroSeIdForNulo() {
            assertThrows(BadRequestException.class, () -> alunoService.findById(null));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se o aluno não for encontrado")
        void deveLancarErroSeAlunoNaoExiste() {
            when(alunoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> alunoService.findById(99L));
        }
    }

    @Nested
    @DisplayName("Cenários de Cadastro (addAluno)")
    class AddAlunoTests {

        @Test
        @DisplayName("Deve salvar um novo aluno com sucesso")
        void deveCadastrarAlunoComSucesso() {
            AlunoCreateDTO createDTO = new AlunoCreateDTO();
            createDTO.setEmail("joao@email.com");

            Usuarios usuarioSalvo = new Usuarios();
            usuarioSalvo.setId(10L);
            usuarioSalvo.setLogin("joao@email.com");

            when(passwordEncoder.encode(anyString())).thenReturn("senhaCriptografadaTexto");
            when(usuarioRepository.save(any(Usuarios.class))).thenReturn(usuarioSalvo);

            when(alunoMapper.toEntity(createDTO)).thenReturn(aluno);
            when(alunoRepository.save(aluno)).thenReturn(aluno);
            when(alunoMapper.toResponseDTO(aluno)).thenReturn(responseDTO);

            AlunoResponseDTO resultado = alunoService.addAluno(createDTO);

            assertNotNull(resultado);
            assertEquals("João Paulo", resultado.getNome());

            verify(passwordEncoder, times(1)).encode("Aluno@123");
            verify(usuarioRepository, times(1)).save(any(Usuarios.class));
            verify(alunoRepository, times(1)).save(aluno);
        }
    }

    @Nested
    @DisplayName("Cenários de Atualização (updateAluno) e Validação de Permissões (RBAC)")
    class UpdateAlunoTests {

        private AlunoUpdateDTO updateDTO;

        @BeforeEach
        void setupUpdate() {
            updateDTO = new AlunoUpdateDTO();
        }

        @Test
        @DisplayName("Deve permitir atualização se o usuário logado for ADMIN ou SUPER_ADMIN")
        void deveAtualizarComoAdmin() {
            mockUsuarioLogado(5L, UserRole.ADMIN);

            when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
            when(alunoRepository.save(aluno)).thenReturn(aluno);
            when(alunoMapper.toResponseDTO(aluno)).thenReturn(responseDTO);

            AlunoResponseDTO resultado = alunoService.updateAluno(1L, updateDTO);

            assertNotNull(resultado);
            verify(alunoMapper).updateEntityFromDTO(updateDTO, aluno);
            verify(alunoRepository).save(aluno);
        }

        @Test
        @DisplayName("Deve permitir atualização se o usuário logado for o próprio ALUNO dono do perfil")
        void deveAtualizarSendoOProprioAluno() {
            mockUsuarioLogado(1L, UserRole.ALUNO);

            when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
            when(alunoRepository.save(aluno)).thenReturn(aluno);
            when(alunoMapper.toResponseDTO(aluno)).thenReturn(responseDTO);

            AlunoResponseDTO resultado = alunoService.updateAluno(1L, updateDTO);

            assertNotNull(resultado);
            assertDoesNotThrow(() -> {});
        }

        @Test
        @DisplayName("Deve lançar ForbiddenException se um ALUNO tentar atualizar os dados de outro ALUNO")
        void deveBloquearAlunoAtualizandoOutro() {
            mockUsuarioLogado(2L, UserRole.ALUNO);

            when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));

            assertThrows(ForbiddenException.class, () -> alunoService.updateAluno(1L, updateDTO));
            verify(alunoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o ID para atualização for nulo")
        void deveLancarErroSeIdUpdateForNulo() {
            assertThrows(BadRequestException.class, () -> alunoService.updateAluno(null, updateDTO));
        }
    }

    @Nested
    @DisplayName("Cenários de Exclusão (deleteAluno)")
    class DeleteAlunoTests {

        @Test
        @DisplayName("Deve carregar o aluno para exclusão com sucesso")
        void deveRetornarAlunoDeletado() {
            when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
            when(alunoMapper.toResponseDTO(aluno)).thenReturn(responseDTO);

            AlunoResponseDTO resultado = alunoService.deleteAluno(1L);

            assertNotNull(resultado);
            verify(alunoRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o ID de exclusão for nulo")
        void deveLancarErroSeIdDelecaoForNulo() {
            assertThrows(BadRequestException.class, () -> alunoService.deleteAluno(null));
        }
    }

    @Nested
    @DisplayName("Cenários de Buscas Customizadas (Nome, Email, Sexo, Idade)")
    class CustomQueriesTests {

        @Test
        @DisplayName("Deve buscar alunos por nome com sucesso")
        void deveBuscarPorNome() {
            when(alunoRepository.findByNome("João")).thenReturn(List.of(aluno));
            when(alunoMapper.toResponseDTO(aluno)).thenReturn(responseDTO);

            List<AlunoResponseDTO> resultado = alunoService.findByNome("João");

            assertFalse(resultado.isEmpty());
            assertEquals(1, resultado.size());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o nome buscado for nulo ou vazio")
        void deveLancarErroSeNomeInvalido() {
            assertThrows(BadRequestException.class, () -> alunoService.findByNome(""));
            assertThrows(BadRequestException.class, () -> alunoService.findByNome(null));
        }

        @Test
        @DisplayName("Deve buscar por e-mail com sucesso")
        void deveBuscarPorEmail() {
            when(alunoRepository.findByEmail("joao@email.com")).thenReturn(aluno);
            when(alunoMapper.toResponseDTO(aluno)).thenReturn(responseDTO);

            AlunoResponseDTO resultado = alunoService.findByEmail("joao@email.com");

            assertNotNull(resultado);
            assertEquals("joao@email.com", resultado.getEmail());
        }

        @Test
        @DisplayName("Deve buscar por sexo e retornar estrutura paginada")
        void deveBuscarPorSexoPaginado() {
            Pageable pageable = PageRequest.of(0, 5);
            Page<Aluno> page = new PageImpl<>(List.of(aluno));

            when(alunoRepository.findBySexo(SexoEnum.MASCULINO, pageable)).thenReturn(page);
            when(alunoMapper.toResponseDTO(aluno)).thenReturn(responseDTO);

            PageResponseDTO<AlunoResponseDTO> resultado = alunoService.findBySexo(SexoEnum.MASCULINO, 0, 5);

            assertNotNull(resultado);
            assertEquals(1, resultado.content().size());
        }

        @Test
        @DisplayName("Deve buscar por idade com sucesso")
        void deveBuscarPorIdade() {
            when(alunoRepository.findByIdadeCustom(21)).thenReturn(List.of(aluno));
            when(alunoMapper.toResponseDTO(aluno)).thenReturn(responseDTO);

            List<AlunoResponseDTO> resultado = alunoService.findByIdade(21);

            assertFalse(resultado.isEmpty());
            verify(alunoRepository).findByIdadeCustom(21);
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se a idade for menor ou igual a 0 ou maior que 99")
        void deveValidarIdadeInvalida() {
            assertThrows(BadRequestException.class, () -> alunoService.findByIdade(0));
            assertThrows(BadRequestException.class, () -> alunoService.findByIdade(105));
        }
    }
}