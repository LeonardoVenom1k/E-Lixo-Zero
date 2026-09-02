package br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.usuario;

import br.fai.lds.e_lixo_zero.domain.UserModel;

public interface ReadByEmailDao {

    UserModel readByEmail(final String email);
}
