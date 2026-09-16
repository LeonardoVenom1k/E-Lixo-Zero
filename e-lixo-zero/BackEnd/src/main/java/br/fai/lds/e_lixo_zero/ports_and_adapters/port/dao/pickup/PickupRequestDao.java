package br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.pickup;

import br.fai.lds.e_lixo_zero.domain.PickupRequestModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.crud.CrudDao;

import java.util.List;

public interface PickupRequestDao extends CrudDao<PickupRequestModel> {
    List<PickupRequestModel> readByUserId(final int userId);
    void updateStatus(final int id, final String status);
}
