package br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.dao.notificacao;

import br.fai.lds.e_lixo_zero.domain.NotificacaoModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.notificacao.NotificacaoDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotificacaoPostgresDaoAdapter implements NotificacaoDao {

    private final Connection connection;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public NotificacaoPostgresDaoAdapter(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public int add(final NotificacaoModel entity) {
        final String sql = "INSERT INTO notificacoes (id_usuario, titulo, mensagem, tipo_notificacao, lida, data_envio) VALUES (?, ?, ?, ?, ?, current_timestamp)";
        try {
            connection.setAutoCommit(false);
            final PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id_notificacao"});
            preparedStatement.setInt(1, entity.getUsuarioId());
            preparedStatement.setString(2, entity.getTitulo());
            preparedStatement.setString(3, entity.getMensagem());
            preparedStatement.setString(4, entity.getTipoNotificacao());
            preparedStatement.setBoolean(5, entity.isLida());
            preparedStatement.executeUpdate();

            final ResultSet resultSet = preparedStatement.getGeneratedKeys();
            int id = 0;
            if (resultSet.next()) {
                id = resultSet.getInt(1);
            }
            resultSet.close();
            preparedStatement.close();
            connection.commit();
            return id;
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(final int id) {
        final String sql = "DELETE FROM notificacoes WHERE id_notificacao = ?";
        try {
            connection.setAutoCommit(false);
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public NotificacaoModel readyById(final int id) {
        final String sql = "SELECT * FROM notificacoes WHERE id_notificacao = ?";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            NotificacaoModel notificacao = null;
            if (resultSet.next()) {
                notificacao = mapNotificacao(resultSet);
            }
            resultSet.close();
            preparedStatement.close();
            return notificacao;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<NotificacaoModel> readAll() {
        final String sql = "SELECT * FROM notificacoes";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<NotificacaoModel> notificacoes = new ArrayList<>();
            while (resultSet.next()) {
                notificacoes.add(mapNotificacao(resultSet));
            }
            resultSet.close();
            preparedStatement.close();
            return notificacoes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<NotificacaoModel> readByUsuarioId(final int usuarioId) {
        final String sql = "SELECT * FROM notificacoes WHERE id_usuario = ? ORDER BY data_envio DESC";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, usuarioId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<NotificacaoModel> notificacoes = new ArrayList<>();
            while (resultSet.next()) {
                notificacoes.add(mapNotificacao(resultSet));
            }
            resultSet.close();
            preparedStatement.close();
            return notificacoes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<NotificacaoModel> readNaoLidasByUsuarioId(final int usuarioId) {
        final String sql = "SELECT * FROM notificacoes WHERE id_usuario = ? AND lida = false ORDER BY data_envio DESC";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, usuarioId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<NotificacaoModel> notificacoes = new ArrayList<>();
            while (resultSet.next()) {
                notificacoes.add(mapNotificacao(resultSet));
            }
            resultSet.close();
            preparedStatement.close();
            return notificacoes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateInformation(final int id, final NotificacaoModel entity) {
        final String sql = "UPDATE notificacoes SET id_usuario = ?, titulo = ?, mensagem = ?, tipo_notificacao = ?, lida = ?, data_envio = ? WHERE id_notificacao = ?";
        try {
            connection.setAutoCommit(false);
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, entity.getUsuarioId());
            preparedStatement.setString(2, entity.getTitulo());
            preparedStatement.setString(3, entity.getMensagem());
            preparedStatement.setString(4, entity.getTipoNotificacao());
            preparedStatement.setBoolean(5, entity.isLida());
            preparedStatement.setTimestamp(6, entity.getDataEnvio() != null ? Timestamp.valueOf(entity.getDataEnvio()) : null);
            preparedStatement.setInt(7, id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void marcarComoLida(final int id) {
        final String sql = "UPDATE notificacoes SET lida = true WHERE id_notificacao = ?";
        try {
            connection.setAutoCommit(false);
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    private NotificacaoModel mapNotificacao(final ResultSet resultSet) throws SQLException {
        final NotificacaoModel notificacao = new NotificacaoModel();
        notificacao.setId(resultSet.getInt("id_notificacao"));
        notificacao.setUsuarioId(resultSet.getInt("id_usuario"));
        notificacao.setTitulo(resultSet.getString("titulo"));
        notificacao.setMensagem(resultSet.getString("mensagem"));
        notificacao.setTipoNotificacao(resultSet.getString("tipo_notificacao"));
        notificacao.setLida(resultSet.getBoolean("lida"));
        final Timestamp dataEnvio = resultSet.getTimestamp("data_envio");
        notificacao.setDataEnvio(dataEnvio != null ? dataEnvio.toLocalDateTime().format(formatter) : "");
        return notificacao;
    }
}
