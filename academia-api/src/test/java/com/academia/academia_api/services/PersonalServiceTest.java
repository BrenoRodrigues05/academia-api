package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.PageResponseDTO;
import com.academia.academia_api.DTOs.PersonalCreateDTO;
import com.academia.academia_api.DTOs.PersonalResponseDTO;
import com.academia.academia_api.DTOs.PersonalUpdateDTO;
import com.academia.academia_api.entity.Personal;
import com.academia.academia_api.entity.Treino;
import com.academia.academia_api.entity.Usuarios;
import com.academia.academia_api.entity.enums.UserRole;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.infra.exceptions.ResourceNotFoundException;
import com.academia.academia_api.mappings.PersonalMapper;
import com.academia.academia_api.repository.PersonalRepository;
import com.academia.academia_api.repository.TreinoRepository;
import com.academia.academia_api.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Personal Trainers (PersonalService)")
class PersonalServiceTest {

    @Mock
    private PersonalRepository personalRepository;

    @Mock
    private PersonalMapper personalMapper;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TreinoRepository treinoRepository;

    @InjectMocks
    private PersonalService personalService;

    private Personal personal;
    private PersonalResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        personal = new Personal();
        personal.setId(1L);
        personal.setNome("Lais Silva");
        personal.setEmail("lais@email.com");
        personal.setCref("CREF 123456-G/PE");
        personal.setAtivo(true);

        Usuarios usuarioMock = new Usuarios();
        usuarioMock.setId(10L);
        usuarioMock.setLogin("lais@email.com");
        usuarioMock.setRole(UserRole.PERSONAL);
        personal.setUsuario(usuarioMock);

        responseDTO = new PersonalResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("Lais Silva");
        responseDTO.setEmail("lais@email.com");
        responseDTO.setCref("CREF 123456-G/PE");
        responseDTO.setAtivo(true);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
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
    @DisplayName("Cenários de Listagem e Paginação (findAll)")
    class FindAllTests {

        @Test
        @DisplayName("Deve retornar uma página de personals com sucesso")
        void deveRetornarPaginaDePersonals() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("nome").ascending());
            Page<Personal> page = new PageImpl<>(List.of(personal));

            when(personalRepository.findAll(pageable)).thenReturn(page);
            when(personalMapper.toResponseDTO(personal)).thenReturn(responseDTO);

            PageResponseDTO<PersonalResponseDTO> resultado = personalService.findAll(0, 10);

            assertNotNull(resultado);
            assertEquals(1, resultado.content().size());
            verify(personalRepository, times(1)).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("Cenários de Contagem Total de Personais (count)")
    class CountPersonaisTests {

        @Test
        @DisplayName("Deve retornar o total de personais cadastrados com sucesso")
        void deveRetornarTotalDePersonais() {
            long totalEsperado = 12L;
            when(personalRepository.count()).thenReturn(totalEsperado);

            long resultado = personalService.countPersonal();

            assertEquals(totalEsperado, resultado);
            verify(personalRepository, times(1)).count();
        }

        @Test
        @DisplayName("Deve retornar zero quando não houver personais cadastrados")
        void deveRetornarZeroQuandoNaoHouverPersonais() {
            when(personalRepository.count()).thenReturn(0L);

            long resultado = personalService.countPersonal();

            assertEquals(0L, resultado);
            verify(personalRepository, times(1)).count();
        }
    }

    @Nested
    @DisplayName("Cenários de Meu Perfil (getMeuPerfil)")
    class GetMeuPerfilTests {

        @Test
        @DisplayName("Deve retornar o perfil do personal logado com sucesso")
        void deveRetornarPerfilDoPersonalLogado() {
            mockUsuarioLogado(10L, UserRole.PERSONAL);

            when(personalRepository.findByUsuarioId(10L)).thenReturn(Optional.of(personal));
            when(personalMapper.toResponseDTO(personal)).thenReturn(responseDTO);

            PersonalResponseDTO resultado = personalService.getMeuPerfil();

            assertNotNull(resultado);
            assertEquals("Lais Silva", resultado.getNome());
            verify(personalRepository).findByUsuarioId(10L);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se o perfil não for encontrado para o usuário logado")
        void deveLancarErroQuandoPerfilNaoEncontrado() {
            mockUsuarioLogado(99L, UserRole.PERSONAL);

            when(personalRepository.findByUsuarioId(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> personalService.getMeuPerfil());
        }
    }

    @Nested
    @DisplayName("Cenários de Busca por Atributos Únicos (ById, Cref, Email, Nome)")
    class QueryTests {

        @Test
        @DisplayName("Deve buscar personal por ID com sucesso")
        void deveBuscarPorId() {
            when(personalRepository.findById(1L)).thenReturn(Optional.of(personal));
            when(personalMapper.toResponseDTO(personal)).thenReturn(responseDTO);

            PersonalResponseDTO resultado = personalService.findById(1L);

            assertNotNull(resultado);
            assertEquals("Lais Silva", resultado.getNome());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o ID for nulo ou menor/igual a zero")
        void deveValidarIdInvalido() {
            assertThrows(BadRequestException.class, () -> personalService.findById(null));
            assertThrows(BadRequestException.class, () -> personalService.findById(0L));
            assertThrows(BadRequestException.class, () -> personalService.findById(-1L));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se o ID não for encontrado")
        void deveLancarErroSeIdNaoEncontrado() {
            when(personalRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> personalService.findById(99L));
        }

        @Test
        @DisplayName("Deve buscar por CREF com sucesso")
        void deveBuscarPorCref() {
            when(personalRepository.findByCref("CREF 123456-G/PE")).thenReturn(Optional.of(personal));
            when(personalMapper.toResponseDTO(personal)).thenReturn(responseDTO);

            PersonalResponseDTO resultado = personalService.findByCref("CREF 123456-G/PE");

            assertNotNull(resultado);
            assertEquals("CREF 123456-G/PE", resultado.getCref());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se CREF for nulo ou vazio")
        void deveValidarCrefInvalido() {
            assertThrows(BadRequestException.class, () -> personalService.findByCref(null));
            assertThrows(BadRequestException.class, () -> personalService.findByCref("   "));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se o CREF não for encontrado")
        void deveLancarErroSeCrefNaoEncontrado() {
            when(personalRepository.findByCref("CREF 000000-G/PE")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> personalService.findByCref("CREF 000000-G/PE"));
        }

        @Test
        @DisplayName("Deve buscar por e-mail com sucesso")
        void deveBuscarPorEmail() {
            when(personalRepository.findByEmailContainingIgnoreCase("lais@email.com")).thenReturn(Optional.of(personal));
            when(personalMapper.toResponseDTO(personal)).thenReturn(responseDTO);

            PersonalResponseDTO resultado = personalService.findByEmail("lais@email.com");

            assertNotNull(resultado);
            assertEquals("lais@email.com", resultado.getEmail());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se e-mail for nulo ou vazio")
        void deveValidarEmailInvalido() {
            assertThrows(BadRequestException.class, () -> personalService.findByEmail(null));
            assertThrows(BadRequestException.class, () -> personalService.findByEmail(""));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se o e-mail não for encontrado")
        void deveLancarErroSeEmailNaoEncontrado() {
            when(personalRepository.findByEmailContainingIgnoreCase("inexistente@email.com")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> personalService.findByEmail("inexistente@email.com"));
        }

        @Test
        @DisplayName("Deve buscar uma lista de personals pelo nome")
        void deveBuscarPorNome() {
            when(personalRepository.findByNomeContainingIgnoreCase("Lais Silva")).thenReturn(List.of(personal));
            when(personalMapper.toResponseDTO(personal)).thenReturn(responseDTO);

            List<PersonalResponseDTO> resultado = personalService.findByNome("Lais Silva");

            assertFalse(resultado.isEmpty());
            assertEquals(1, resultado.size());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se nome for nulo ou vazio")
        void deveValidarNomeInvalido() {
            assertThrows(BadRequestException.class, () -> personalService.findByNome(null));
            assertThrows(BadRequestException.class, () -> personalService.findByNome("   "));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se a busca por nome retornar lista vazia")
        void deveLancarErroSeNomeNaoEncontrado() {
            when(personalRepository.findByNomeContainingIgnoreCase("Desconhecido")).thenReturn(Collections.emptyList());

            assertThrows(ResourceNotFoundException.class, () -> personalService.findByNome("Desconhecido"));
        }
    }

    @Nested
    @DisplayName("Cenários de Filtragem por Status (findByAtivo)")
    class FilterStatusTests {

        @Test
        @DisplayName("Deve retornar apenas os personals ativos")
        void deveBuscarAtivos() {
            when(personalRepository.findByAtivoTrue()).thenReturn(List.of(personal));
            when(personalMapper.toResponseDTO(personal)).thenReturn(responseDTO);

            List<PersonalResponseDTO> resultado = personalService.findByAtivoTrue();

            assertFalse(resultado.isEmpty());
            assertTrue(resultado.get(0).getAtivo());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se não houver personais ativos")
        void deveLancarErroSeNaoHouverAtivos() {
            when(personalRepository.findByAtivoTrue()).thenReturn(Collections.emptyList());

            assertThrows(ResourceNotFoundException.class, () -> personalService.findByAtivoTrue());
        }

        @Test
        @DisplayName("Deve retornar apenas os personals inativos")
        void deveBuscarInativos() {
            personal.setAtivo(false);
            responseDTO.setAtivo(false);

            when(personalRepository.findByAtivoFalse()).thenReturn(List.of(personal));
            when(personalMapper.toResponseDTO(personal)).thenReturn(responseDTO);

            List<PersonalResponseDTO> resultado = personalService.findByAtivoFalse();

            assertFalse(resultado.isEmpty());
            assertFalse(resultado.get(0).getAtivo());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se não houver personais inativos")
        void deveLancarErroSeNaoHouverInativos() {
            when(personalRepository.findByAtivoFalse()).thenReturn(Collections.emptyList());

            assertThrows(ResourceNotFoundException.class, () -> personalService.findByAtivoFalse());
        }
    }

    @Nested
    @DisplayName("Cenários de Cadastro (addPersonal)")
    class AddPersonalTests {

        @Test
        @DisplayName("Deve cadastrar um personal com sucesso e criar seu usuário associado")
        void deveCadastrarComSucesso() {
            PersonalCreateDTO createDTO = new PersonalCreateDTO();
            createDTO.setEmail("lais@email.com");
            createDTO.setCref("CREF 123456-G/PE");

            when(personalRepository.findByEmailContainingIgnoreCase("lais@email.com")).thenReturn(Optional.empty());
            when(personalRepository.findByCref("CREF 123456-G/PE")).thenReturn(Optional.empty());

            when(passwordEncoder.encode("Mudar@123")).thenReturn("senhaCriptografada");

            Usuarios usuarioMockado = new Usuarios();
            usuarioMockado.setId(1L);
            usuarioMockado.setLogin("lais@email.com");
            when(usuarioRepository.save(any(Usuarios.class))).thenReturn(usuarioMockado);

            when(personalMapper.toEntity(createDTO)).thenReturn(personal);
            when(personalRepository.save(personal)).thenReturn(personal);
            when(personalMapper.toResponseDTO(personal)).thenReturn(responseDTO);

            PersonalResponseDTO resultado = personalService.addPersonal(createDTO);

            assertNotNull(resultado);
            verify(passwordEncoder, times(1)).encode("Mudar@123");
            verify(usuarioRepository, times(1)).save(any(Usuarios.class));
            verify(personalRepository, times(1)).save(personal);
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o e-mail já estiver cadastrado")
        void deveValidarEmailDuplicado() {
            PersonalCreateDTO createDTO = new PersonalCreateDTO();
            createDTO.setEmail("lais@email.com");

            when(personalRepository.findByEmailContainingIgnoreCase("lais@email.com")).thenReturn(Optional.of(personal));

            assertThrows(BadRequestException.class, () -> personalService.addPersonal(createDTO));
            verify(personalRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o CREF já estiver cadastrado")
        void deveValidarCrefDuplicado() {
            PersonalCreateDTO createDTO = new PersonalCreateDTO();
            createDTO.setEmail("novo@email.com");
            createDTO.setCref("CREF 123456-G/PE");

            when(personalRepository.findByEmailContainingIgnoreCase("novo@email.com")).thenReturn(Optional.empty());
            when(personalRepository.findByCref("CREF 123456-G/PE")).thenReturn(Optional.of(personal));

            assertThrows(BadRequestException.class, () -> personalService.addPersonal(createDTO));
            verify(personalRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Cenários de Atualização e Status (updatePersonal / atualizarAtivoPersonal)")
    class UpdateTests {

        @Test
        @DisplayName("Deve atualizar dados cadastrais com sucesso")
        void deveAtualizarDados() {
            PersonalUpdateDTO updateDTO = new PersonalUpdateDTO();

            when(personalRepository.findById(1L)).thenReturn(Optional.of(personal));
            when(personalRepository.save(personal)).thenReturn(personal);
            when(personalMapper.toResponseDTO(personal)).thenReturn(responseDTO);

            PersonalResponseDTO resultado = personalService.updatePersonal(1L, updateDTO);

            assertNotNull(resultado);
            verify(personalMapper).updateEntityFromDTO(updateDTO, personal);
            verify(personalRepository).save(personal);
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o ID para atualização for inválido")
        void deveValidarIdInvalidoUpdate() {
            PersonalUpdateDTO updateDTO = new PersonalUpdateDTO();

            assertThrows(BadRequestException.class, () -> personalService.updatePersonal(null, updateDTO));
            assertThrows(BadRequestException.class, () -> personalService.updatePersonal(0L, updateDTO));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao tentar atualizar personal não existente")
        void deveLancarErroSePersonalInexistenteUpdate() {
            PersonalUpdateDTO updateDTO = new PersonalUpdateDTO();
            when(personalRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> personalService.updatePersonal(99L, updateDTO));
        }

        @Test
        @DisplayName("Deve alterar status de ativo/inativo com sucesso")
        void deveAlterarStatusAtivo() {
            personal.setAtivo(true);

            when(personalRepository.findById(1L)).thenReturn(Optional.of(personal));
            when(personalRepository.save(personal)).thenReturn(personal);
            when(personalMapper.toResponseDTO(personal)).thenReturn(responseDTO);

            PersonalResponseDTO resultado = personalService.atualizarAtivoPersonal(1L, false);

            assertNotNull(resultado);
            verify(personalRepository).save(personal);
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se tentar alterar o status para o valor atual")
        void deveImpedirAlteracaoParaMesmoStatus() {
            personal.setAtivo(true);
            when(personalRepository.findById(1L)).thenReturn(Optional.of(personal));

            assertThrows(BadRequestException.class, () -> personalService.atualizarAtivoPersonal(1L, true));
            verify(personalRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o ID para alteração de status for inválido")
        void deveValidarIdInvalidoStatus() {
            assertThrows(BadRequestException.class, () -> personalService.atualizarAtivoPersonal(null, true));
            assertThrows(BadRequestException.class, () -> personalService.atualizarAtivoPersonal(0L, true));
        }
    }

    @Nested
    @DisplayName("Cenários de Deleção (deletePersonal)")
    class DeleteTests {

        @Test
        @DisplayName("Deve deletar personal com sucesso se ele estiver inativo e não possuir treinos")
        void deveDeletarSeInativoESemTreinos() {
            personal.setAtivo(false);

            when(personalRepository.findById(1L)).thenReturn(Optional.of(personal));
            when(treinoRepository.findByPersonalId(1L)).thenReturn(List.of());
            when(personalMapper.toResponseDTO(personal)).thenReturn(responseDTO);

            PersonalResponseDTO resultado = personalService.deletePersonal(1L);

            assertNotNull(resultado);
            verify(personalRepository, times(1)).delete(personal);
            verify(treinoRepository, times(1)).findByPersonalId(1L);
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o ID de exclusão for inválido")
        void deveValidarIdInvalidoDelete() {
            assertThrows(BadRequestException.class, () -> personalService.deletePersonal(null));
            assertThrows(BadRequestException.class, () -> personalService.deletePersonal(0L));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se o personal a ser deletado não existir")
        void deveLancarErroSePersonalInexistenteDelete() {
            when(personalRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> personalService.deletePersonal(99L));
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se tentar deletar um personal ativo")
        void deveImpedirDelecaoDePersonalAtivo() {
            personal.setAtivo(true);

            when(personalRepository.findById(1L)).thenReturn(Optional.of(personal));

            BadRequestException exception = assertThrows(BadRequestException.class,
                    () -> personalService.deletePersonal(1L));

            assertTrue(exception.getMessage().contains("O personal está ativo"));
            verify(treinoRepository, never()).findByPersonalId(any());
            verify(personalRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o personal inativo possuir treinos vinculados")
        void deveImpedirDelecaoSePossuirTreinos() {
            personal.setAtivo(false);

            when(personalRepository.findById(1L)).thenReturn(Optional.of(personal));
            when(treinoRepository.findByPersonalId(1L)).thenReturn(List.of(new Treino()));

            BadRequestException exception = assertThrows(BadRequestException.class,
                    () -> personalService.deletePersonal(1L));

            assertTrue(exception.getMessage().contains("existem treinos associados"));
            verify(personalRepository, never()).delete(any());
        }
    }
}