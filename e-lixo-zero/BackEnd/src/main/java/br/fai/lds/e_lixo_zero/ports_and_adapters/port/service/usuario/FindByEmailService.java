package br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.usuario;

import br.fai.lds.e_lixo_zero.domain.UserModel;

public interface FindByEmailService {

    UserModel findByEmail(final String email);
}
