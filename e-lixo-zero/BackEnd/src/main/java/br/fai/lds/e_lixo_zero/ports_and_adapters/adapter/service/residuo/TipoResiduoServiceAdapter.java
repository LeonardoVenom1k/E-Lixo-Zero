package br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.service.residuo;

import br.fai.lds.e_lixo_zero.domain.TipoResiduoModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.residuo.TipoResiduoDao;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.residuo.TipoResiduoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoResiduoServiceAdapter implements TipoResiduoService {

    @Autowired
    private TipoResiduoDao tipoResiduoDao;

    @Override
    public int create(final TipoResiduoModel entity) {
        if (entity == null || isInvalidString(entity.getNome())) {
            return 0;
        }
        entity.setAtivo(true);
        return tipoResiduoDao.add(entity);
    }

    @Override
    public void delete(final int id) {
        if (id <= 0) {
            return;
        }
        tipoResiduoDao.remove(id);
    }

    @Override
    public boolean update(final int id, final TipoResiduoModel entity) {
        if (id <= 0 || entity == null || isInvalidString(entity.getNome())) {
            return false;
        }
        final TipoResiduoModel stored = findById(id);
        if (stored == null) {
            return false;
        }
        tipoResiduoDao.updateInformation(id, entity);
        return true;
    }

    @Override
    public TipoResiduoModel findById(final int id) {
        if (id <= 0) {
            return null;
        }
        return tipoResiduoDao.readyById(id);
    }

    @Override
    public List<TipoResiduoModel> findAll() {
        return tipoResiduoDao.readAll();
    }

    @Override
    public TipoResiduoModel findByNome(final String nome) {
        if (nome == null || nome.isBlank()) {
            return null;
        }
        return tipoResiduoDao.readByNome(nome.trim());
    }

    private boolean isInvalidString(final String value) {
        return value == null || value.isBlank();
    }
}
