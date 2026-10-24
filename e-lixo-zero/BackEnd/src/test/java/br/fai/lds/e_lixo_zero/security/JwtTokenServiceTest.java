package br.fai.lds.e_lixo_zero.security;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JwtTokenServiceTest {

    private JwtTokenService tokenService;

    @Before
    public void setUp() throws ReflectiveOperationException {
        tokenService = new JwtTokenService();
        setField("secret", "unit-test-secret-key-with-at-least-32-bytes-long");
        setField("expiration", 60_000L);
    }

    @Test
    public void generatedTokenIsValidAndContainsTheEmailAsSubject() {
        final String email = "usuario@elixozero.com";
        final String token = tokenService.generateToken(email);

        assertTrue(tokenService.isValidToken(token));
        assertEquals(email, tokenService.getSubjectFromToken(token));
    }

    @Test
    public void malformedTokenIsInvalid() {
        assertFalse(tokenService.isValidToken("token-invalido"));
    }

    @Test
    public void expiredTokenIsInvalid() throws ReflectiveOperationException {
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
