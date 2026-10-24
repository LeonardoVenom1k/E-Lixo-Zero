package br.fai.lds.e_lixo_zero.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenServiceTest {

    private JwtTokenService tokenService;

    @BeforeEach
    void setUp() throws ReflectiveOperationException {
        tokenService = new JwtTokenService();
        setField("secret", "unit-test-secret-key-with-at-least-32-bytes-long");
        setField("expiration", 60_000L);
    }

    @Test
    void generatedTokenIsValidAndContainsTheEmailAsSubject() {
        final String email = "usuario@elixozero.com";
        final String token = tokenService.generateToken(email);

        assertTrue(tokenService.isValidToken(token));
        assertEquals(email, tokenService.getSubjectFromToken(token));
    }

    @Test
    void malformedTokenIsInvalid() {
        assertFalse(tokenService.isValidToken("token-invalido"));
    }

    @Test
    void expiredTokenIsInvalid() throws ReflectiveOperationException {
        setField("expiration", -1L);

        final String token = tokenService.generateToken("usuario@elixozero.com");

        assertFalse(tokenService.isValidToken(token));
    }

    private void setField(final String name, final Object value) throws ReflectiveOperationException {
        final Field field = JwtTokenService.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(tokenService, value);
    }
}
