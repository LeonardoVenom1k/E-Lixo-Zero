package br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.service.notificacao;

import br.fai.lds.e_lixo_zero.domain.NotificacaoModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.notificacao.NotificacaoDao;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.notificacao.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacaoServiceAdapter implements NotificacaoService {

    @Autowired
    private NotificacaoDao notificacaoDao;

    @Override
    public int create(final NotificacaoModel entity) {
        if (entity == null || entity.getUsuarioId() <= 0) {
            return 0;
        }
        if (isInvalidString(entity.getTitulo()) || isInvalidString(entity.getMensagem())) {
            return 0;
        }
        if (isInvalidString(entity.getTipoNotificacao())) {
            entity.setTipoNotificacao("INFORMATIVA");
        }
        return notificacaoDao.add(entity);
    }

    @Override
    public void delete(final int id) {
        if (id <= 0) {
            return;
        }
        notificacaoDao.remove(id);
    }

    @Override
    public boolean update(final int id, final NotificacaoModel entity) {
        if (id <= 0 || entity == null) {
            return false;
        }
        if (findById(id) == null) {
            return false;
        }
        notificacaoDao.updateInformation(id, entity);
        return true;
    }

    @Override
    public NotificacaoModel findById(final int id) {
        if (id <= 0) {
            return null;
        }
        return notificacaoDao.readyById(id);
    }

    @Override
    public List<NotificacaoModel> findAll() {
        return notificacaoDao.readAll();
    }

    @Override
    public List<NotificacaoModel> findByUsuarioId(final int usuarioId) {
        if (usuarioId <= 0) {
            return List.of();
        }
        return notificacaoDao.readByUsuarioId(usuarioId);
    }

    @Override
    public List<NotificacaoModel> findNaoLidasByUsuarioId(final int usuarioId) {
        if (usuarioId <= 0) {
            return List.of();
        }
        return notificacaoDao.readNaoLidasByUsuarioId(usuarioId);
    }

    @Override
    public boolean marcarComoLida(final int id) {
        if (id <= 0) {
            return false;
        }
        if (findById(id) == null) {
            return false;
        }
        notificacaoDao.marcarComoLida(id);
        return true;
    }

    private boolean isInvalidString(final String value) {
        return value == null || value.isBlank();
    }
}
