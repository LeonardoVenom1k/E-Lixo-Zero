package br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.service.ponto;

import br.fai.lds.e_lixo_zero.domain.PontoColetaModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.ponto.PontoColetaDao;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.ponto.PontoColetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PontoColetaServiceAdapter implements PontoColetaService {

    @Autowired
    private PontoColetaDao pontoColetaDao;

    @Override
    public int create(final PontoColetaModel entity) {
        if (entity == null || isInvalidString(entity.getNome())) {
            return 0;
        }
        if (isInvalidString(entity.getEndereco()) && isInvalidString(entity.getLogradouro())) {
            return 0;
        }
        entity.setAtivo(true);
        if (entity.getResiduos() == null) {
            entity.setResiduos(List.of());
        }
        return pontoColetaDao.add(entity);
    }

    @Override
    public void delete(final int id) {
        if (id <= 0) {
            return;
        }
        pontoColetaDao.remove(id);
    }

    @Override
    public boolean update(final int id, final PontoColetaModel entity) {
        if (id <= 0 || entity == null || isInvalidString(entity.getNome())) {
            return false;
        }
        final PontoColetaModel stored = findById(id);
        if (stored == null) {
            return false;
        }
        if (entity.getResiduos() == null) {
            entity.setResiduos(List.of());
        }
        pontoColetaDao.updateInformation(id, entity);
        return true;
    }

    @Override
    public PontoColetaModel findById(final int id) {
        if (id <= 0) {
            return null;
        }
        return pontoColetaDao.readyById(id);
    }

    @Override
    public List<PontoColetaModel> findAll() {
        return pontoColetaDao.readAll();
    }

    @Override
    public List<PontoColetaModel> findByCidade(final String cidade) {
        if (isInvalidString(cidade)) {
            return pontoColetaDao.readAll();
        }
        return pontoColetaDao.readByCidade(cidade);
    }

    private boolean isInvalidString(final String value) {
        return value == null || value.isBlank();
    }
}
