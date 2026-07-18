package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.PageResponseDTO;
import com.academia.academia_api.DTOs.PersonalCreateDTO;
import com.academia.academia_api.DTOs.PersonalResponseDTO;
import com.academia.academia_api.DTOs.PersonalUpdateDTO;
import com.academia.academia_api.entity.Personal;
import com.academia.academia_api.entity.Usuarios;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.mappings.PersonalMapper;
import com.academia.academia_api.repository.PersonalRepository;
import com.academia.academia_api.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

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

        responseDTO = new PersonalResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("Lais Silva");
        responseDTO.setEmail("lais@email.com");
        responseDTO.setCref("CREF 123456-G/PE");
        responseDTO.setAtivo(true);
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
        @DisplayName("Deve lançar BadRequestException se o ID for menor ou igual a zero")
        void deveValidarIdInvalido() {
            assertThrows(BadRequestException.class, () -> personalService.findById(0L));
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
        @DisplayName("Deve buscar por e-mail com sucesso")
        void deveBuscarPorEmail() {
            when(personalRepository.findByEmailContainingIgnoreCase("lais@email.com")).thenReturn(Optional.of(personal));
            when(personalMapper.toResponseDTO(personal)).thenReturn(responseDTO);

            PersonalResponseDTO resultado = personalService.findByEmail("lais@email.com");

            assertNotNull(resultado);
            assertEquals("lais@email.com", resultado.getEmail());
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
            assertTrue(resultado.getFirst().getAtivo());
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
            assertFalse(resultado.getFirst().getAtivo());
        }
    }

    @Nested
    @DisplayName("Cenários de Cadastro (addPersonal)")
    class AddPersonalTests {

        @Test
        @DisplayName("Deve cadastrar um personal com sucesso e criar seu usuário associado")
        void deveCadastrarComSucesso() {
            // Arrange
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
        }

        @Test
        @DisplayName("Deve alterar status de ativo/inativo com sucesso")
        void deveAlterarStatusAtivo() {
            when(personalRepository.findById(1L)).thenReturn(Optional.of(personal));

            personal.setAtivo(true);

            when(personalRepository.save(personal)).thenReturn(personal);
            when(personalMapper.toResponseDTO(personal)).thenReturn(responseDTO);

            PersonalResponseDTO resultado = personalService.atualizarAtivoPersonal(1L, false);

            assertNotNull(resultado);
            verify(personalRepository).save(personal);
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se tentar alterar o status para o valor atual")
        void deveImpedirAlteracaoParaMesmoStatus() {
            when(personalRepository.findById(1L)).thenReturn(Optional.of(personal));
            personal.setAtivo(true);

            assertThrows(BadRequestException.class, () -> personalService.atualizarAtivoPersonal(1L, true));
            verify(personalRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Cenários de Deleção (deletePersonal)")
    class DeleteTests {

        @Test
        @DisplayName("Deve deletar personal com sucesso se ele estiver inativo")
        void deveDeletarSeInativo() {
            personal.setAtivo(false);

            when(personalRepository.findById(1L)).thenReturn(Optional.of(personal));
            when(personalMapper.toResponseDTO(personal)).thenReturn(responseDTO);

            PersonalResponseDTO resultado = personalService.deletePersonal(1L);

            assertNotNull(resultado);
            verify(personalRepository, times(1)).delete(personal);
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se tentar deletar um personal ativo")
        void deveImpedirDelecaoDePersonalAtivo() {
            personal.setAtivo(true);

            when(personalRepository.findById(1L)).thenReturn(Optional.of(personal));

            BadRequestException exception = assertThrows(BadRequestException.class,
                    () -> personalService.deletePersonal(1L));

            assertTrue(exception.getMessage().contains("O personal está ativo"));
            verify(personalRepository, never()).delete(any());
        }
    }
}