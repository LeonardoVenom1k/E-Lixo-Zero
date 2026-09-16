package br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.wastetype;

import br.fai.lds.e_lixo_zero.domain.WasteTypeModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.crud.CrudService;

public interface WasteTypeService extends CrudService<WasteTypeModel> {
    WasteTypeModel findByName(final String name);
}
