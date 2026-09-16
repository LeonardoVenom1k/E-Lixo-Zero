package br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.wastetype;

import br.fai.lds.e_lixo_zero.domain.WasteTypeModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.crud.CrudDao;

public interface WasteTypeDao extends CrudDao<WasteTypeModel> {
    WasteTypeModel readByName(final String name);
}
