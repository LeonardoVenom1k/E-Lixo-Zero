package br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.notification;

import br.fai.lds.e_lixo_zero.domain.NotificationModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.crud.CrudService;

import java.util.List;

public interface NotificationService extends CrudService<NotificationModel> {
    List<NotificationModel> findByUserId(final int userId);
    List<NotificationModel> findUnreadByUserId(final int userId);
    boolean markAsRead(final int id);
}
