package br.fai.lds.e_lixo_zero.configuration;

import br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.dao.coleta.SolicitacaoColetaPostgresDaoAdapter;
import br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.dao.notificacao.NotificacaoPostgresDaoAdapter;
import br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.dao.ponto.PontoColetaPostgresDaoAdapter;
import br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.dao.residuo.TipoResiduoPostgresDaoAdapter;
import br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.dao.configuration.UserPostgresDaoAdapter;
import br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.dao.usuario.UserFakeDaoAdapter;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.coleta.SolicitacaoColetaDao;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.notificacao.NotificacaoDao;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.ponto.PontoColetaDao;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.residuo.TipoResiduoDao;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.usuario.UserDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
public class AppConfiguration {

    @Bean
    public Connection connection(final DataSource dataSource) throws SQLException {
        return dataSource.getConnection();
    }

    public UserDao getUserFakeDao() {
        return new UserFakeDaoAdapter();
    }

    @Bean
    public UserDao getUserPostgresDao(final Connection connection) {
        return new UserPostgresDaoAdapter(connection);
    }

    @Bean
    public TipoResiduoDao getTipoResiduoPostgresDao(final Connection connection) {
        return new TipoResiduoPostgresDaoAdapter(connection);
    }

    @Bean
    public PontoColetaDao getPontoColetaPostgresDao(final Connection connection) {
        return new PontoColetaPostgresDaoAdapter(connection);
    }

    @Bean
    public SolicitacaoColetaDao getSolicitacaoColetaPostgresDao(final Connection connection) {
        return new SolicitacaoColetaPostgresDaoAdapter(connection);
    }

    @Bean
    public NotificacaoDao getNotificacaoPostgresDao(final Connection connection) {
        return new NotificacaoPostgresDaoAdapter(connection);
    }
}
