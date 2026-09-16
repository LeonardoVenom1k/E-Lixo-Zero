package br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.collectionpoint;

import br.fai.lds.e_lixo_zero.domain.CollectionPointModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.crud.CrudDao;

import java.util.List;

public interface CollectionPointDao extends CrudDao<CollectionPointModel> {
    List<CollectionPointModel> readByCity(final String city);
}
