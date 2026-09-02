package br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.dao.coleta;

import br.fai.lds.e_lixo_zero.domain.SolicitacaoColetaModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.coleta.SolicitacaoColetaDao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SolicitacaoColetaPostgresDaoAdapter implements SolicitacaoColetaDao {

    private final Connection connection;

    public SolicitacaoColetaPostgresDaoAdapter(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public int add(final SolicitacaoColetaModel entity) {
        final String sql = "INSERT INTO solicitacoes_coleta (id_usuario, id_residuo, logradouro, numero, bairro, cidade, estado, quantidade_estimada, data_desejada, status, observacoes, data_solicitacao, data_atualizacao) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp, current_timestamp)";
        try {
            connection.setAutoCommit(false);
            final PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id_solicitacao"});
            preparedStatement.setInt(1, entity.getUsuarioId());
            preparedStatement.setInt(2, entity.getTipoResiduoId());
            preparedStatement.setString(3, entity.getLogradouro());
            preparedStatement.setString(4, entity.getNumero());
            preparedStatement.setString(5, entity.getBairro());
            preparedStatement.setString(6, entity.getCidade());
            preparedStatement.setString(7, entity.getEstado());
            preparedStatement.setString(8, entity.getQuantidadeEstimada());
            preparedStatement.setDate(9, Date.valueOf(entity.getDataDesejada()));
            preparedStatement.setString(10, entity.getStatus());
            preparedStatement.setString(11, entity.getObservacoes());
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
        final String sql = "DELETE FROM solicitacoes_coleta WHERE id_solicitacao = ?";
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
    public SolicitacaoColetaModel readyById(final int id) {
        final String sql = "SELECT * FROM solicitacoes_coleta WHERE id_solicitacao = ?";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            SolicitacaoColetaModel coleta = null;
            if (resultSet.next()) {
                coleta = mapColeta(resultSet);
            }
            resultSet.close();
            preparedStatement.close();
            return coleta;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SolicitacaoColetaModel> readAll() {
        final String sql = "SELECT * FROM solicitacoes_coleta";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<SolicitacaoColetaModel> coletas = new ArrayList<>();
            while (resultSet.next()) {
                coletas.add(mapColeta(resultSet));
            }
            resultSet.close();
            preparedStatement.close();
            return coletas;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SolicitacaoColetaModel> readByUsuarioId(final int usuarioId) {
        final String sql = "SELECT * FROM solicitacoes_coleta WHERE id_usuario = ?";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, usuarioId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<SolicitacaoColetaModel> coletas = new ArrayList<>();
            while (resultSet.next()) {
                coletas.add(mapColeta(resultSet));
            }
            resultSet.close();
            preparedStatement.close();
            return coletas;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateInformation(final int id, final SolicitacaoColetaModel entity) {
        final String sql = "UPDATE solicitacoes_coleta SET id_usuario = ?, id_residuo = ?, logradouro = ?, numero = ?, bairro = ?, cidade = ?, estado = ?, quantidade_estimada = ?, data_desejada = ?, status = ?, observacoes = ?, data_atualizacao = current_timestamp WHERE id_solicitacao = ?";
        try {
            connection.setAutoCommit(false);
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, entity.getUsuarioId());
            preparedStatement.setInt(2, entity.getTipoResiduoId());
            preparedStatement.setString(3, entity.getLogradouro());
            preparedStatement.setString(4, entity.getNumero());
            preparedStatement.setString(5, entity.getBairro());
            preparedStatement.setString(6, entity.getCidade());
            preparedStatement.setString(7, entity.getEstado());
            preparedStatement.setString(8, entity.getQuantidadeEstimada());
            preparedStatement.setDate(9, Date.valueOf(entity.getDataDesejada()));
            preparedStatement.setString(10, entity.getStatus());
            preparedStatement.setString(11, entity.getObservacoes());
            preparedStatement.setInt(12, id);
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
    public void updateStatus(final int id, final String status) {
        final String sql = "UPDATE solicitacoes_coleta SET status = ?, data_atualizacao = current_timestamp WHERE id_solicitacao = ?";
        try {
            connection.setAutoCommit(false);
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, id);
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

    private SolicitacaoColetaModel mapColeta(final ResultSet resultSet) throws SQLException {
        final SolicitacaoColetaModel coleta = new SolicitacaoColetaModel();
        coleta.setId(resultSet.getInt("id_solicitacao"));
        coleta.setUsuarioId(resultSet.getInt("id_usuario"));
        coleta.setTipoResiduoId(resultSet.getInt("id_residuo"));
        final int coletorId = resultSet.getInt("id_coletor");
        coleta.setColetorId(resultSet.wasNull() ? 0 : coletorId);
        coleta.setLogradouro(resultSet.getString("logradouro"));
        coleta.setNumero(resultSet.getString("numero"));
        coleta.setBairro(resultSet.getString("bairro"));
        coleta.setCidade(resultSet.getString("cidade"));
        coleta.setEstado(resultSet.getString("estado"));
        coleta.setQuantidadeEstimada(resultSet.getString("quantidade_estimada"));
        final Date data = resultSet.getDate("data_desejada");
        coleta.setDataDesejada(data != null ? data.toLocalDate().toString() : null);
        coleta.setStatus(resultSet.getString("status"));
        coleta.setObservacoes(resultSet.getString("observacoes"));
        return coleta;
    }
}
