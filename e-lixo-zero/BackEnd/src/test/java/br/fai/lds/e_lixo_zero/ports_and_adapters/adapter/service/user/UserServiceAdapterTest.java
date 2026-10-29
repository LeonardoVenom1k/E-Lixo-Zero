package br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.service.user;

import br.fai.lds.e_lixo_zero.domain.UserModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.user.UserDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceAdapterTest {

    private UserServiceAdapter userService;
    private UserDao userDao;

    @Before
    public void setUp() throws ReflectiveOperationException {
        userService = new UserServiceAdapter();
        userDao = Mockito.mock(UserDao.class);

        final Field field = UserServiceAdapter.class.getDeclaredField("userDao");
        field.setAccessible(true);
        field.set(userService, userDao);
    }

    @Test
    public void createValidUserReturnsId() {
        final UserModel user = new UserModel();
        user.setFullName("João Silva");
        user.setEmail("joao@test.com");
        user.setPassword("senha123");

        when(userDao.add(any(UserModel.class))).thenReturn(1);
        when(userDao.readByEmail("joao@test.com")).thenReturn(null);

        final int id = userService.create(user);

        assertEquals(1, id);
        verify(userDao).add(any(UserModel.class));
    }

    @Test
    public void createWithNullUserReturnsZero() {
        final int id = userService.create(null);

        assertEquals(0, id);
        verify(userDao, never()).add(any(UserModel.class));
    }

    @Test
    public void createWithNullNameReturnsZero() {
        final UserModel user = new UserModel();
        user.setEmail("joao@test.com");
        user.setPassword("senha123");

        final int id = userService.create(user);

        assertEquals(0, id);
        verify(userDao, never()).add(any(UserModel.class));
    }

    @Test
    public void createWithInvalidEmailReturnsZero() {
        final UserModel user = new UserModel();
        user.setFullName("João Silva");
        user.setEmail("invalid-email");
        user.setPassword("senha123");

        final int id = userService.create(user);

        assertEquals(0, id);
        verify(userDao, never()).add(any(UserModel.class));
    }

    @Test
    public void createWithExistingEmailReturnsZero() {
        final UserModel user = new UserModel();
        user.setFullName("João Silva");
        user.setEmail("joao@test.com");
        user.setPassword("senha123");

        final UserModel existingUser = new UserModel();
        when(userDao.readByEmail("joao@test.com")).thenReturn(existingUser);

        final int id = userService.create(user);

        assertEquals(0, id);
        verify(userDao, never()).add(any(UserModel.class));
    }

    @Test
    public void createSetsDefaultValues() {
        final UserModel user = new UserModel();
        user.setFullName("João Silva");
        user.setEmail("joao@test.com");
        user.setPassword("senha123");

        when(userDao.add(any(UserModel.class))).thenAnswer(invocation -> {
            final UserModel u = invocation.getArgument(0);
            assertEquals("Santa Rita do Sapucaí", u.getCity());
            assertEquals("MG", u.getState());
            assertEquals("CITIZEN", u.getUserType());
            assertTrue(u.isActive());
            return 1;
        });
        when(userDao.readByEmail("joao@test.com")).thenReturn(null);

        userService.create(user);
    }

    @Test
    public void findByIdWithValidIdReturnsUser() {
        final UserModel user = new UserModel();
        user.setId(1);
        user.setFullName("João Silva");

        when(userDao.readById(1)).thenReturn(user);

        final UserModel result = userService.findById(1);

        assertNotNull(result);
        assertEquals("João Silva", result.getFullName());
    }

    @Test
    public void findByIdWithInvalidIdReturnsNull() {
        final UserModel result = userService.findById(-1);

        assertNull(result);
        verify(userDao, never()).readById(anyInt());
    }

    @Test
    public void findByEmailWithValidEmailReturnsUser() {
        final UserModel user = new UserModel();
        user.setEmail("joao@test.com");

        when(userDao.readByEmail("joao@test.com")).thenReturn(user);

        final UserModel result = userService.findByEmail("joao@test.com");

        assertNotNull(result);
        assertEquals("joao@test.com", result.getEmail());
    }

    @Test
    public void findByEmailWithInvalidEmailReturnsNull() {
        final UserModel result = userService.findByEmail("invalid-email");

        assertNull(result);
        verify(userDao, never()).readByEmail(anyString());
    }

    @Test
    public void deleteWithValidIdCallsDao() {
        userService.delete(1);

        verify(userDao).remove(1);
    }

    @Test
    public void deleteWithInvalidIdDoesNotCallDao() {
        userService.delete(-1);

        verify(userDao, never()).remove(anyInt());
    }

    @Test
    public void updateWithValidDataReturnsTrue() {
        final UserModel user = new UserModel();
        user.setFullName("João Silva");

        final UserModel stored = new UserModel();
        when(userDao.readById(1)).thenReturn(stored);

        final boolean result = userService.update(1, user);

        assertTrue(result);
        verify(userDao).updateInformation(1, user);
    }

    @Test
    public void updateWithInvalidIdReturnsFalse() {
        final UserModel user = new UserModel();

        final boolean result = userService.update(-1, user);

        assertTrue(!result);
        verify(userDao, never()).updateInformation(anyInt(), any(UserModel.class));
    }

    @Test
    public void findAllReturnsUserList() {
        userService.findAll();

        verify(userDao).readAll();
    }
}
