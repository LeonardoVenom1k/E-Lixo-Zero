package br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.usuario;

import br.fai.lds.e_lixo_zero.domain.UserModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.crud.CrudService;

public interface UsuarioService extends CrudService<UserModel>, FindByEmailService {
}
