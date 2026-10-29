package br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.service.pickup;

import br.fai.lds.e_lixo_zero.domain.PickupRequestModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.pickup.PickupRequestDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PickupRequestServiceAdapterTest {

    private PickupRequestServiceAdapter pickupService;
    private PickupRequestDao pickupDao;

    @Before
    public void setUp() throws ReflectiveOperationException {
        pickupService = new PickupRequestServiceAdapter();
        pickupDao = Mockito.mock(PickupRequestDao.class);

        final Field field = PickupRequestServiceAdapter.class.getDeclaredField("pickupRequestDao");
        field.setAccessible(true);
        field.set(pickupService, pickupDao);
    }

    @Test
    public void createValidPickupReturnsId() {
        final PickupRequestModel pickup = new PickupRequestModel();
        pickup.setUserId(1);
        pickup.setWasteTypeId(1);
        pickup.setStreet("Rua Teste");
        pickup.setNeighborhood("Centro");
        pickup.setDesiredDate("2026-12-31");

        when(pickupDao.add(any(PickupRequestModel.class))).thenReturn(1);

        final int id = pickupService.create(pickup);

        assertEquals(1, id);
        verify(pickupDao).add(any(PickupRequestModel.class));
    }

    @Test
    public void createWithNullPickupReturnsZero() {
        final int id = pickupService.create(null);

        assertEquals(0, id);
        verify(pickupDao, never()).add(any(PickupRequestModel.class));
    }

    @Test
    public void createWithInvalidUserIdReturnsZero() {
        final PickupRequestModel pickup = new PickupRequestModel();
        pickup.setUserId(0);
        pickup.setWasteTypeId(1);
        pickup.setStreet("Rua Teste");
        pickup.setNeighborhood("Centro");
        pickup.setDesiredDate("2026-12-31");

        final int id = pickupService.create(pickup);

        assertEquals(0, id);
        verify(pickupDao, never()).add(any(PickupRequestModel.class));
    }

    @Test
    public void createWithInvalidWasteTypeIdReturnsZero() {
        final PickupRequestModel pickup = new PickupRequestModel();
        pickup.setUserId(1);
        pickup.setWasteTypeId(0);
        pickup.setStreet("Rua Teste");
        pickup.setNeighborhood("Centro");
        pickup.setDesiredDate("2026-12-31");

        final int id = pickupService.create(pickup);

        assertEquals(0, id);
        verify(pickupDao, never()).add(any(PickupRequestModel.class));
    }

    @Test
    public void createWithMissingStreetReturnsZero() {
        final PickupRequestModel pickup = new PickupRequestModel();
        pickup.setUserId(1);
        pickup.setWasteTypeId(1);
        pickup.setNeighborhood("Centro");
        pickup.setDesiredDate("2026-12-31");

        final int id = pickupService.create(pickup);

        assertEquals(0, id);
        verify(pickupDao, never()).add(any(PickupRequestModel.class));
    }

    @Test
    public void createSetsDefaultValues() {
        final PickupRequestModel pickup = new PickupRequestModel();
        pickup.setUserId(1);
        pickup.setWasteTypeId(1);
        pickup.setStreet("Rua Teste");
        pickup.setNeighborhood("Centro");
        pickup.setDesiredDate("2026-12-31");

        when(pickupDao.add(any(PickupRequestModel.class))).thenAnswer(invocation -> {
            final PickupRequestModel p = invocation.getArgument(0);
            assertEquals("Santa Rita do Sapucaí", p.getCity());
            assertEquals("MG", p.getState());
            assertEquals("PENDING", p.getStatus());
            return 1;
        });

        pickupService.create(pickup);
    }

    @Test
    public void findByIdWithValidIdReturnsPickup() {
        final PickupRequestModel pickup = new PickupRequestModel();
        pickup.setId(1);
        pickup.setStreet("Rua Teste");

        when(pickupDao.readById(1)).thenReturn(pickup);

        final PickupRequestModel result = pickupService.findById(1);

        assertNotNull(result);
        assertEquals("Rua Teste", result.getStreet());
    }

    @Test
    public void findByIdWithInvalidIdReturnsNull() {
        final PickupRequestModel result = pickupService.findById(-1);

        assertNull(result);
        verify(pickupDao, never()).readById(anyInt());
    }

    @Test
    public void findByUserIdWithValidIdReturnsList() {
        final PickupRequestModel pickup = new PickupRequestModel();
        pickup.setUserId(1);

        when(pickupDao.readByUserId(1)).thenReturn(List.of(pickup));

        final List<PickupRequestModel> result = pickupService.findByUserId(1);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void findByUserIdWithInvalidIdReturnsEmptyList() {
        final List<PickupRequestModel> result = pickupService.findByUserId(-1);

        assertTrue(result.isEmpty());
        verify(pickupDao, never()).readByUserId(anyInt());
    }

    @Test
    public void findForCollectorWithValidIdReturnsList() {
        final PickupRequestModel pickup = new PickupRequestModel();
        pickup.setCollectorId(1);

        when(pickupDao.readForCollector(1)).thenReturn(List.of(pickup));

        final List<PickupRequestModel> result = pickupService.findForCollector(1);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void findForCollectorWithInvalidIdReturnsEmptyList() {
        final List<PickupRequestModel> result = pickupService.findForCollector(-1);

        assertTrue(result.isEmpty());
        verify(pickupDao, never()).readForCollector(anyInt());
    }

    @Test
    public void updateStatusWithValidDataReturnsTrue() {
        final PickupRequestModel stored = new PickupRequestModel();
        stored.setId(1);
        stored.setStatus("PENDING");

        when(pickupDao.readById(1)).thenReturn(stored);

        final boolean result = pickupService.updateStatus(1, "Scheduled");

        assertTrue(result);
        verify(pickupDao).updateStatus(1, "Scheduled");
    }

    @Test
    public void updateStatusWithFinalStatusReturnsFalse() {
        final PickupRequestModel stored = new PickupRequestModel();
        stored.setId(1);
        stored.setStatus("Completed");

        when(pickupDao.readById(1)).thenReturn(stored);

        final boolean result = pickupService.updateStatus(1, "Scheduled");

        assertTrue(!result);
        verify(pickupDao, never()).updateStatus(anyInt(), anyString());
    }

    @Test
    public void updateStatusByCollectorWithValidDataReturnsTrue() {
        final PickupRequestModel stored = new PickupRequestModel();
        stored.setId(1);
        stored.setStatus("PENDING");
        stored.setCollectorId(0);

        when(pickupDao.readById(1)).thenReturn(stored);

        final boolean result = pickupService.updateStatusByCollector(1, "In Progress", 1);

        assertTrue(result);
        verify(pickupDao).updateStatus(1, "In Progress");
        verify(pickupDao).assignCollector(1, 1);
    }

    @Test
    public void updateStatusByCollectorWithDifferentCollectorReturnsFalse() {
        final PickupRequestModel stored = new PickupRequestModel();
        stored.setId(1);
        stored.setStatus("PENDING");
        stored.setCollectorId(2);

        when(pickupDao.readById(1)).thenReturn(stored);

        final boolean result = pickupService.updateStatusByCollector(1, "In Progress", 1);

        assertTrue(!result);
        verify(pickupDao, never()).updateStatus(anyInt(), anyString());
    }

    @Test
    public void deleteWithValidIdCallsDao() {
        pickupService.delete(1);

        verify(pickupDao).remove(1);
    }

    @Test
    public void deleteWithInvalidIdDoesNotCallDao() {
        pickupService.delete(-1);

        verify(pickupDao, never()).remove(anyInt());
    }

    @Test
    public void findAllReturnsPickupList() {
        pickupService.findAll();

        verify(pickupDao).readAll();
    }
}
