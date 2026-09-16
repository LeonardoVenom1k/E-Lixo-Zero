package br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.user;

import br.fai.lds.e_lixo_zero.domain.UserModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.crud.CrudService;

public interface UserService extends CrudService<UserModel>, FindByEmailService {
}
