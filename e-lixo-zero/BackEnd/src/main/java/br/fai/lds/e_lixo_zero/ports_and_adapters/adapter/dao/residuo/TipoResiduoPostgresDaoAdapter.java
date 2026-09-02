package br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.dao.residuo;

import br.fai.lds.e_lixo_zero.domain.TipoResiduoModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.residuo.TipoResiduoDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TipoResiduoPostgresDaoAdapter implements TipoResiduoDao {

    private final Connection connection;

    public TipoResiduoPostgresDaoAdapter(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public int add(final TipoResiduoModel entity) {
        final String sql = "INSERT INTO tipos_residuos (nome, categoria, descricao, ativo) VALUES (?, ?, ?, ?)";
        try {
            connection.setAutoCommit(false);
            final PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, entity.getNome());
            preparedStatement.setString(2, entity.getCategoria());
            preparedStatement.setString(3, entity.getDescricao());
            preparedStatement.setBoolean(4, entity.isAtivo());
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
        final String sql = "DELETE FROM tipos_residuos WHERE id_residuo = ?";
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
    public TipoResiduoModel readyById(final int id) {
        final String sql = "SELECT * FROM tipos_residuos WHERE id_residuo = ?";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            TipoResiduoModel residuo = null;
            if (resultSet.next()) {
                residuo = mapResiduo(resultSet);
            }
            resultSet.close();
            preparedStatement.close();
            return residuo;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<TipoResiduoModel> readAll() {
        final String sql = "SELECT * FROM tipos_residuos WHERE ativo = true";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<TipoResiduoModel> residuos = new ArrayList<>();
            while (resultSet.next()) {
                residuos.add(mapResiduo(resultSet));
            }
            resultSet.close();
            preparedStatement.close();
            return residuos;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TipoResiduoModel readByNome(final String nome) {
        if (nome == null || nome.isBlank()) {
            return null;
        }
        final String sql = "SELECT * FROM tipos_residuos WHERE LOWER(nome) = LOWER(?)";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, nome.trim());
            final ResultSet resultSet = preparedStatement.executeQuery();
            TipoResiduoModel residuo = null;
            if (resultSet.next()) {
                residuo = mapResiduo(resultSet);
            }
            resultSet.close();
            preparedStatement.close();
            return residuo;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateInformation(final int id, final TipoResiduoModel entity) {
        final String sql = "UPDATE tipos_residuos SET nome = ?, categoria = ?, descricao = ?, ativo = ? WHERE id_residuo = ?";
        try {
            connection.setAutoCommit(false);
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, entity.getNome());
            preparedStatement.setString(2, entity.getCategoria());
            preparedStatement.setString(3, entity.getDescricao());
            preparedStatement.setBoolean(4, entity.isAtivo());
            preparedStatement.setInt(5, id);
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

    private TipoResiduoModel mapResiduo(final ResultSet resultSet) throws SQLException {
        final TipoResiduoModel residuo = new TipoResiduoModel();
        residuo.setId(resultSet.getInt("id_residuo"));
        residuo.setNome(resultSet.getString("nome"));
        residuo.setCategoria(resultSet.getString("categoria"));
        residuo.setDescricao(resultSet.getString("descricao"));
        residuo.setAtivo(resultSet.getBoolean("ativo"));
        return residuo;
    }
}
