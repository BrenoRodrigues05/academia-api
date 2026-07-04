package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.RegisterAdminDTO;
import com.academia.academia_api.DTOs.RegisterAlunoDTO;
import com.academia.academia_api.DTOs.RegisterPersonalDTO;
import com.academia.academia_api.entity.Aluno;
import com.academia.academia_api.entity.Personal;
import com.academia.academia_api.entity.Usuarios;
import com.academia.academia_api.entity.enums.SexoEnum;
import com.academia.academia_api.entity.enums.UserRole;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.repository.AlunoRepository;
import com.academia.academia_api.repository.PersonalRepository;
import com.academia.academia_api.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Autenticação e Registro (AuthService)")
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private PersonalRepository personalRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Captor
    private ArgumentCaptor<Usuarios> usuarioCaptor;

    @Captor
    private ArgumentCaptor<Aluno> alunoCaptor;

    @Captor
    private ArgumentCaptor<Personal> personalCaptor;

    @Nested
    @DisplayName("Cenários de Registro de Aluno (registerAluno)")
    class RegisterAlunoTests {

        private RegisterAlunoDTO criarDtoValido() {
            return new RegisterAlunoDTO(
                    "brenorods",
                    "senha123",
                    "Breno Rodrigues",
                    "breno@email.com",
                    LocalDate.of(2005, 5, 15),
                    "8198871'6545",
                    SexoEnum.MASCULINO

            );
        }

        @Test
        @DisplayName("Deve registrar um aluno com sucesso quando os dados forem válidos")
        void deveRegistrarAlunoComSucesso() {
            RegisterAlunoDTO dto = criarDtoValido();

            when(usuarioRepository.existsByLogin(dto.login())).thenReturn(false);
            when(alunoRepository.findByEmail(dto.email())).thenReturn(null);
            when(passwordEncoder.encode(dto.senha())).thenReturn("senha_criptografada");

            when(usuarioRepository.save(any(Usuarios.class))).thenAnswer(invocation -> {
                Usuarios u = invocation.getArgument(0);
                u.setId(10L);
                return u;
            });

            assertDoesNotThrow(() -> authService.registerAluno(dto));

            verify(usuarioRepository, times(1)).save(usuarioCaptor.capture());
            verify(alunoRepository, times(1)).save(alunoCaptor.capture());

            Usuarios usuarioSalvo = usuarioCaptor.getValue();
            assertEquals("brenorods", usuarioSalvo.getLogin());
            assertEquals("senha_criptografada", usuarioSalvo.getSenha());
            assertEquals(UserRole.ALUNO, usuarioSalvo.getRole());
            assertTrue(usuarioSalvo.getAtivo());

            Aluno alunoSalvo = alunoCaptor.getValue();
            assertEquals("Breno Rodrigues", alunoSalvo.getNome());
            assertEquals("breno@email.com", alunoSalvo.getEmail());
            assertEquals(10L, alunoSalvo.getUsuario().getId());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o login já estiver em uso")
        void deveLancarErroSeLoginJaExistir() {
            RegisterAlunoDTO dto = criarDtoValido();

            when(usuarioRepository.existsByLogin(dto.login())).thenReturn(true);

            BadRequestException exception = assertThrows(BadRequestException.class,
                    () -> authService.registerAluno(dto));

            assertEquals("Login já utilizado.", exception.getMessage());
            verify(usuarioRepository, never()).save(any());
            verify(alunoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o e-mail informado já estiver cadastrado")
        void deveLancarErroSeEmailJaExistir() {
            RegisterAlunoDTO dto = criarDtoValido();

            when(usuarioRepository.existsByLogin(dto.login())).thenReturn(false);
            when(alunoRepository.findByEmail(dto.email())).thenReturn(new Aluno());

            BadRequestException exception = assertThrows(BadRequestException.class,
                    () -> authService.registerAluno(dto));

            assertEquals("O e-mail informado já está cadastrado.", exception.getMessage());
            verify(usuarioRepository, never()).save(any());
            verify(alunoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Cenários de Registro de Personal Trainer (registerPersonal)")
    class RegisterPersonalTests {

        private RegisterPersonalDTO criarDtoValido() {
            return new RegisterPersonalDTO(
                    "personal_lais",
                    "senha456",
                    "Lais Silva",
                    "dos Santos",
                    "lais@memail.com",
                    "81996754231",
                    "CREF 123456-G/PE",
                    "Dança",
                    SexoEnum.FEMININO
            );
        }

        @Test
        @DisplayName("Deve registrar um personal com sucesso quando o login estiver disponível")
        void deveRegistrarPersonalComSucesso() {
            RegisterPersonalDTO dto = criarDtoValido();

            when(usuarioRepository.existsByLogin(dto.login())).thenReturn(false);
            when(passwordEncoder.encode(dto.senha())).thenReturn("senha_criptografada_personal");
            when(usuarioRepository.save(any(Usuarios.class))).thenAnswer(invocation -> invocation.getArgument(0));

            assertDoesNotThrow(() -> authService.registerPersonal(dto));

            verify(usuarioRepository, times(1)).save(usuarioCaptor.capture());
            verify(personalRepository, times(1)).save(personalCaptor.capture());

            Usuarios usuarioSalvo = usuarioCaptor.getValue();
            assertEquals("personal_lais", usuarioSalvo.getLogin());
            assertEquals(UserRole.PERSONAL, usuarioSalvo.getRole());

            Personal personalSalvo = personalCaptor.getValue();
            assertEquals("Lais Silva", personalSalvo.getNome());
            assertEquals("CREF 123456-G/PE", personalSalvo.getCref());
            assertNotNull(personalSalvo.getUsuario());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o login de Personal já estiver em uso")
        void deveLancarErroSeLoginPersonalJaExistir() {
            RegisterPersonalDTO dto = criarDtoValido();

            when(usuarioRepository.existsByLogin(dto.login())).thenReturn(true);

            assertThrows(BadRequestException.class, () -> authService.registerPersonal(dto));
            verify(personalRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Cenários de Registro de Administrador (registerAdmin)")
    class RegisterAdminTests {

        private RegisterAdminDTO criarDtoValido() {
            return new RegisterAdminDTO(
                    "admin_master",
                    "admin@2026"
            );
        }

        @Test
        @DisplayName("Deve registrar um admin com sucesso")
        void deveRegistrarAdminComSucesso() {
            RegisterAdminDTO dto = criarDtoValido();

            when(usuarioRepository.existsByLogin(dto.login())).thenReturn(false);
            when(passwordEncoder.encode(dto.senha())).thenReturn("hash_admin");

            assertDoesNotThrow(() -> authService.registerAdmin(dto));

            verify(usuarioRepository, times(1)).save(usuarioCaptor.capture());
            verify(personalRepository, never()).save(any());
            verify(alunoRepository, never()).save(any());

            Usuarios usuarioSalvo = usuarioCaptor.getValue();
            assertEquals("admin_master", usuarioSalvo.getLogin());
            assertEquals("hash_admin", usuarioSalvo.getSenha());
            assertEquals(UserRole.ADMIN, usuarioSalvo.getRole());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o login do Admin já estiver ocupado")
        void deveLancarErroSeLoginAdminJaExistir() {
            RegisterAdminDTO dto = criarDtoValido();

            when(usuarioRepository.existsByLogin(dto.login())).thenReturn(true);

            assertThrows(BadRequestException.class, () -> authService.registerAdmin(dto));
            verify(usuarioRepository, never()).save(any());
        }
    }
}