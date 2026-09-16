package br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.user;

import br.fai.lds.e_lixo_zero.domain.UserModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.crud.CrudDao;

public interface UserDao extends CrudDao<UserModel>, ReadByEmailDao {
}
