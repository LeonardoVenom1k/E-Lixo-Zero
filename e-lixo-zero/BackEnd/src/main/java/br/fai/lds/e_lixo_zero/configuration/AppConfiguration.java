package br.fai.lds.e_lixo_zero.configuration;

import br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.dao.pickup.PickupRequestPostgresDaoAdapter;
import br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.dao.notification.NotificationPostgresDaoAdapter;
import br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.dao.collectionpoint.CollectionPointPostgresDaoAdapter;
import br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.dao.wastetype.WasteTypePostgresDaoAdapter;
import br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.dao.user.UserPostgresDaoAdapter;
import br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.dao.user.UserFakeDaoAdapter;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.pickup.PickupRequestDao;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.notification.NotificationDao;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.collectionpoint.CollectionPointDao;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.wastetype.WasteTypeDao;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.user.UserDao;
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
    public WasteTypeDao getWasteTypePostgresDao(final Connection connection) {
        return new WasteTypePostgresDaoAdapter(connection);
    }

    @Bean
    public CollectionPointDao getCollectionPointPostgresDao(final Connection connection) {
        return new CollectionPointPostgresDaoAdapter(connection);
    }

    @Bean
    public PickupRequestDao getPickupRequestPostgresDao(final Connection connection) {
        return new PickupRequestPostgresDaoAdapter(connection);
    }

    @Bean
    public NotificationDao getNotificationPostgresDao(final Connection connection) {
        return new NotificationPostgresDaoAdapter(connection);
    }
}
