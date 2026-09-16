package br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.pickup;

import br.fai.lds.e_lixo_zero.domain.PickupRequestModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.crud.CrudService;

import java.util.List;

public interface PickupRequestService extends CrudService<PickupRequestModel> {
    List<PickupRequestModel> findByUserId(final int userId);
    boolean updateStatus(final int id, final String status);
}
