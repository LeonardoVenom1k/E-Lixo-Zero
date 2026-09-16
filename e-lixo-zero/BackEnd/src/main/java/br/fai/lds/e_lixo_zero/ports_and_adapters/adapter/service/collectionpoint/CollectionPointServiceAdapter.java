package br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.service.collectionpoint;

import br.fai.lds.e_lixo_zero.domain.CollectionPointModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.collectionpoint.CollectionPointDao;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.collectionpoint.CollectionPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class CollectionPointServiceAdapter implements CollectionPointService {

    @Autowired
    private CollectionPointDao collectionPointDao;

    @Override
    public int create(final CollectionPointModel entity) {
        if (entity == null || isInvalidString(entity.getName())) {
            return 0;
        }
        if (isInvalidString(entity.getAddress()) && isInvalidString(entity.getStreet())) {
            return 0;
        }
        entity.setActive(true);
        if (entity.getAcceptedWastes() == null) {
            entity.setAcceptedWastes(List.of());
        }
        return collectionPointDao.add(entity);
    }

    @Override
    public void delete(final int id) {
        if (id <= 0) {
            return;
        }
        collectionPointDao.remove(id);
    }

    @Override
    public boolean update(final int id, final CollectionPointModel entity) {
        if (id <= 0 || entity == null || isInvalidString(entity.getName())) {
            return false;
        }
        final CollectionPointModel stored = findById(id);
        if (stored == null) {
            return false;
        }
        if (entity.getAcceptedWastes() == null) {
            entity.setAcceptedWastes(List.of());
        }
        collectionPointDao.updateInformation(id, entity);
        return true;
    }

    @Override
    public CollectionPointModel findById(final int id) {
        if (id <= 0) {
            return null;
        }
        return collectionPointDao.readById(id);
    }

    @Override
    public List<CollectionPointModel> findAll() {
        return collectionPointDao.readAll();
    }

    @Override
    public List<CollectionPointModel> findByCity(final String city) {
        if (isInvalidString(city)) {
            return collectionPointDao.readAll();
        }
        return collectionPointDao.readByCity(city);
    }

    @Override
    public List<CollectionPointModel> findNearby(final double lat, final double lng) {
        final List<CollectionPointModel> points = collectionPointDao.readAll();
        for (final CollectionPointModel point : points) {
            if (point.getLatitude() != 0 || point.getLongitude() != 0) {
                final double distance = distanceKm(lat, lng, point.getLatitude(), point.getLongitude());
                point.setDistanceKm(Math.round(distance * 100.0) / 100.0);
            }
        }
        points.sort(Comparator.comparing(
                CollectionPointModel::getDistanceKm,
                Comparator.nullsLast(Comparator.naturalOrder())));
        return points;
    }

    private double distanceKm(final double lat1, final double lng1, final double lat2, final double lng2) {
        final double earthRadiusKm = 6371.0;
        final double dLat = Math.toRadians(lat2 - lat1);
        final double dLng = Math.toRadians(lng2 - lng1);
        final double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return earthRadiusKm * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private boolean isInvalidString(final String value) {
        return value == null || value.isBlank();
    }
}
