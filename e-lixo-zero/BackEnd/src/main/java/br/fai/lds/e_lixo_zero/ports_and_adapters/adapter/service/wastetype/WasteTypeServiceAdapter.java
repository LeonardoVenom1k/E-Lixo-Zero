package br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.service.wastetype;

import br.fai.lds.e_lixo_zero.domain.WasteTypeModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.wastetype.WasteTypeDao;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.wastetype.WasteTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WasteTypeServiceAdapter implements WasteTypeService {

    @Autowired
    private WasteTypeDao wasteTypeDao;

    @Override
    public int create(final WasteTypeModel entity) {
        if (entity == null || isInvalidString(entity.getName())) {
            return 0;
        }
        entity.setActive(true);
        return wasteTypeDao.add(entity);
    }

    @Override
    public void delete(final int id) {
        if (id <= 0) {
            return;
        }
        wasteTypeDao.remove(id);
    }

    @Override
    public boolean update(final int id, final WasteTypeModel entity) {
        if (id <= 0 || entity == null || isInvalidString(entity.getName())) {
            return false;
        }
        final WasteTypeModel stored = findById(id);
        if (stored == null) {
            return false;
        }
        wasteTypeDao.updateInformation(id, entity);
        return true;
    }

    @Override
    public WasteTypeModel findById(final int id) {
        if (id <= 0) {
            return null;
        }
        return wasteTypeDao.readById(id);
    }

    @Override
    public List<WasteTypeModel> findAll() {
        return wasteTypeDao.readAll();
    }

    @Override
    public WasteTypeModel findByName(final String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        return wasteTypeDao.readByName(name.trim());
    }

    private boolean isInvalidString(final String value) {
        return value == null || value.isBlank();
    }
}
