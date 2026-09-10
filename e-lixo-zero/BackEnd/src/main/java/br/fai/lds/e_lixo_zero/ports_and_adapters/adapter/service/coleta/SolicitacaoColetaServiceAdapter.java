package br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.service.coleta;

import br.fai.lds.e_lixo_zero.domain.SolicitacaoColetaModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.coleta.SolicitacaoColetaDao;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.coleta.SolicitacaoColetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolicitacaoColetaServiceAdapter implements SolicitacaoColetaService {

    @Autowired
    private SolicitacaoColetaDao solicitacaoColetaDao;

    @Override
    public int create(final SolicitacaoColetaModel entity) {
        if (entity == null || entity.getUsuarioId() <= 0 || entity.getTipoResiduoId() <= 0) {
            return 0;
        }
        if (isInvalidString(entity.getLogradouro()) || isInvalidString(entity.getBairro()) || isInvalidString(entity.getDataDesejada())) {
            return 0;
        }
        if (isInvalidString(entity.getNumero())) {
            entity.setNumero("");
        }
        if (isInvalidString(entity.getCidade())) {
            entity.setCidade("Santa Rita do Sapucaí");
        }
        if (isInvalidString(entity.getEstado())) {
            entity.setEstado("MG");
        }
        if (isInvalidString(entity.getQuantidadeEstimada())) {
            entity.setQuantidadeEstimada("");
        }
        if (isInvalidString(entity.getStatus())) {
            entity.setStatus("PENDENTE");
        }
        if (entity.getObservacoes() == null) {
            entity.setObservacoes("");
        }
        return solicitacaoColetaDao.add(entity);
    }

    @Override
    public void delete(final int id) {
        if (id <= 0) {
            return;
        }
        solicitacaoColetaDao.remove(id);
    }

    @Override
    public boolean update(final int id, final SolicitacaoColetaModel entity) {
        if (id <= 0 || entity == null) {
            return false;
        }
        final SolicitacaoColetaModel stored = findById(id);
        if (stored == null) {
            return false;
        }
        solicitacaoColetaDao.updateInformation(id, entity);
        return true;
    }

    @Override
    public SolicitacaoColetaModel findById(final int id) {
        if (id <= 0) {
            return null;
        }
        return solicitacaoColetaDao.readyById(id);
    }

    @Override
    public List<SolicitacaoColetaModel> findAll() {
        return solicitacaoColetaDao.readAll();
    }

    @Override
    public List<SolicitacaoColetaModel> findByUsuarioId(final int usuarioId) {
        if (usuarioId <= 0) {
            return List.of();
        }
        return solicitacaoColetaDao.readByUsuarioId(usuarioId);
    }

    @Override
    public boolean updateStatus(final int id, final String status) {
        if (id <= 0 || isInvalidString(status)) {
            return false;
        }
        if (findById(id) == null) {
            return false;
        }
        solicitacaoColetaDao.updateStatus(id, status);
        return true;
    }

    private boolean isInvalidString(final String value) {
        return value == null || value.isBlank();
    }
}
