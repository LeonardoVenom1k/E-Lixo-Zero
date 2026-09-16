package br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.notification;

import br.fai.lds.e_lixo_zero.domain.NotificationModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.crud.CrudDao;

import java.util.List;

public interface NotificationDao extends CrudDao<NotificationModel> {
    List<NotificationModel> readByUserId(final int userId);
    List<NotificationModel> readUnreadByUserId(final int userId);
    void markAsRead(final int id);
}
