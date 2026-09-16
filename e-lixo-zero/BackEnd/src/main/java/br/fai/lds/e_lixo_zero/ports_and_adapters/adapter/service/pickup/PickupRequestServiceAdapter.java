package br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.service.pickup;

import br.fai.lds.e_lixo_zero.domain.PickupRequestModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.pickup.PickupRequestDao;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.pickup.PickupRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PickupRequestServiceAdapter implements PickupRequestService {

    @Autowired
    private PickupRequestDao pickupRequestDao;

    @Override
    public int create(final PickupRequestModel entity) {
        if (entity == null || entity.getUserId() <= 0 || entity.getWasteTypeId() <= 0) {
            return 0;
        }
        if (isInvalidString(entity.getStreet()) || isInvalidString(entity.getNeighborhood()) || isInvalidString(entity.getDesiredDate())) {
            return 0;
        }
        if (isInvalidString(entity.getNumber())) {
            entity.setNumber("");
        }
        if (isInvalidString(entity.getCity())) {
            entity.setCity("Santa Rita do Sapucaí");
        }
        if (isInvalidString(entity.getState())) {
            entity.setState("MG");
        }
        if (isInvalidString(entity.getEstimatedQuantity())) {
            entity.setEstimatedQuantity("");
        }
        if (isInvalidString(entity.getStatus())) {
            entity.setStatus("PENDING");
        }
        if (entity.getNotes() == null) {
            entity.setNotes("");
        }
        return pickupRequestDao.add(entity);
    }

    @Override
    public void delete(final int id) {
        if (id <= 0) {
            return;
        }
        pickupRequestDao.remove(id);
    }

    @Override
    public boolean update(final int id, final PickupRequestModel entity) {
        if (id <= 0 || entity == null) {
            return false;
        }
        final PickupRequestModel stored = findById(id);
        if (stored == null) {
            return false;
        }
        pickupRequestDao.updateInformation(id, entity);
        return true;
    }

    @Override
    public PickupRequestModel findById(final int id) {
        if (id <= 0) {
            return null;
        }
        return pickupRequestDao.readById(id);
    }

    @Override
    public List<PickupRequestModel> findAll() {
        return pickupRequestDao.readAll();
    }

    @Override
    public List<PickupRequestModel> findByUserId(final int userId) {
        if (userId <= 0) {
            return List.of();
        }
        return pickupRequestDao.readByUserId(userId);
    }

    @Override
    public boolean updateStatus(final int id, final String status) {
        if (id <= 0 || isInvalidString(status)) {
            return false;
        }
        if (findById(id) == null) {
            return false;
        }
        pickupRequestDao.updateStatus(id, status);
        return true;
    }

    private boolean isInvalidString(final String value) {
        return value == null || value.isBlank();
    }
}
