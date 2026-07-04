package com.academia.academia_api.services;

import com.academia.academia_api.entity.Usuarios;
import com.academia.academia_api.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Usuários (UsuariosService)")
class UsuariosServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuariosService usuariosService;

    private Usuarios usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuarios();
        usuario.setId(1L);
        usuario.setLogin("breno.dev");
    }

    @Nested
    @DisplayName("Cenários de Carregamento de Usuário (loadUserByUsername)")
    class LoadUserByUsernameTests {

        @Test
        @DisplayName("Deve retornar UserDetails com sucesso quando o login existir")
        void deveRetornarUsuarioQuandoLoginExistir() {
            when(usuarioRepository.findByLogin("breno.dev")).thenReturn(usuario);

            UserDetails resultado = usuariosService.loadUserByUsername("breno.dev");

            assertNotNull(resultado);
            assertEquals("breno.dev", resultado.getUsername());
            verify(usuarioRepository, times(1)).findByLogin("breno.dev");
        }

        @Test
        @DisplayName("Deve lançar UsernameNotFoundException se o parâmetro de username for nulo ou vazio")
        void deveLancarErroSeUsernameForInvalido() {
            assertThrows(UsernameNotFoundException.class, () -> usuariosService.loadUserByUsername(null));
            assertThrows(UsernameNotFoundException.class, () -> usuariosService.loadUserByUsername("   "));

            verify(usuarioRepository, never()).findByLogin(anyString());
        }

        @Test
        @DisplayName("Deve lançar UsernameNotFoundException se o usuário não for localizado no banco")
        void deveLancarErroSeUsuarioNaoForLocalizado() {
            when(usuarioRepository.findByLogin("usuario.inexistente")).thenReturn(null);

            UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                    () -> usuariosService.loadUserByUsername("usuario.inexistente"));

            assertTrue(exception.getMessage().contains("Usuário não encontrado com o login"));
            verify(usuarioRepository, times(1)).findByLogin("usuario.inexistente");
        }
    }
}