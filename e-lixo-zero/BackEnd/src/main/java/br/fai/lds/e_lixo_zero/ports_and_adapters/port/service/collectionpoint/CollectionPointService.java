package br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.collectionpoint;

import br.fai.lds.e_lixo_zero.domain.CollectionPointModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.crud.CrudService;

import java.util.List;

public interface CollectionPointService extends CrudService<CollectionPointModel> {
    List<CollectionPointModel> findByCity(final String city);

    List<CollectionPointModel> findNearby(final double lat, final double lng);
}
