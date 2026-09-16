package br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.service.notification;

import br.fai.lds.e_lixo_zero.domain.NotificationModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.notification.NotificationDao;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceAdapter implements NotificationService {

    @Autowired
    private NotificationDao notificationDao;

    @Override
    public int create(final NotificationModel entity) {
        if (entity == null || entity.getUserId() <= 0) {
            return 0;
        }
        if (isInvalidString(entity.getTitle()) || isInvalidString(entity.getMessage())) {
            return 0;
        }
        if (isInvalidString(entity.getNotificationType())) {
            entity.setNotificationType("INFO");
        }
        return notificationDao.add(entity);
    }

    @Override
    public void delete(final int id) {
        if (id <= 0) {
            return;
        }
        notificationDao.remove(id);
    }

    @Override
    public boolean update(final int id, final NotificationModel entity) {
        if (id <= 0 || entity == null) {
            return false;
        }
        if (findById(id) == null) {
            return false;
        }
        notificationDao.updateInformation(id, entity);
        return true;
    }

    @Override
    public NotificationModel findById(final int id) {
        if (id <= 0) {
            return null;
        }
        return notificationDao.readById(id);
    }

    @Override
    public List<NotificationModel> findAll() {
        return notificationDao.readAll();
    }

    @Override
    public List<NotificationModel> findByUserId(final int userId) {
        if (userId <= 0) {
            return List.of();
        }
        return notificationDao.readByUserId(userId);
    }

    @Override
    public List<NotificationModel> findUnreadByUserId(final int userId) {
        if (userId <= 0) {
            return List.of();
        }
        return notificationDao.readUnreadByUserId(userId);
    }

    @Override
    public boolean markAsRead(final int id) {
        if (id <= 0) {
            return false;
        }
        if (findById(id) == null) {
            return false;
        }
        notificationDao.markAsRead(id);
        return true;
    }

    private boolean isInvalidString(final String value) {
        return value == null || value.isBlank();
    }
}
