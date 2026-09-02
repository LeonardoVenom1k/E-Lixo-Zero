package br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.dao.ponto;

import br.fai.lds.e_lixo_zero.domain.PontoColetaModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.ponto.PontoColetaDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PontoColetaPostgresDaoAdapter implements PontoColetaDao {

    private final Connection connection;

    public PontoColetaPostgresDaoAdapter(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public int add(final PontoColetaModel entity) {
        final String sql = "INSERT INTO pontos_coleta (nome, logradouro, numero, bairro, cidade, estado, latitude, longitude, horario_funcionamento, ativo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            connection.setAutoCommit(false);
            prepareDefaults(entity);
            final PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, entity.getNome());
            preparedStatement.setString(2, entity.getLogradouro());
            preparedStatement.setString(3, entity.getNumero());
            preparedStatement.setString(4, entity.getBairro());
            preparedStatement.setString(5, entity.getCidade());
            preparedStatement.setString(6, entity.getEstado());
            preparedStatement.setDouble(7, entity.getLatitude());
            preparedStatement.setDouble(8, entity.getLongitude());
            preparedStatement.setString(9, entity.getHorario());
            preparedStatement.setBoolean(10, entity.isAtivo());
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
        final String sql = "DELETE FROM pontos_coleta WHERE id_ponto = ?";
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
    public PontoColetaModel readyById(final int id) {
        final String sql = "SELECT * FROM pontos_coleta WHERE id_ponto = ?";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            PontoColetaModel ponto = null;
            if (resultSet.next()) {
                ponto = mapPonto(resultSet);
            }
            resultSet.close();
            preparedStatement.close();
            return ponto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PontoColetaModel> readAll() {
        final String sql = "SELECT * FROM pontos_coleta WHERE ativo = true";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<PontoColetaModel> pontos = new ArrayList<>();
            while (resultSet.next()) {
                pontos.add(mapPonto(resultSet));
            }
            resultSet.close();
            preparedStatement.close();
            return pontos;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PontoColetaModel> readByCidade(final String cidade) {
        if (cidade == null || cidade.isBlank()) {
            return readAll();
        }
        final String termo = "%" + cidade.trim().toLowerCase() + "%";
        final String sql = "SELECT * FROM pontos_coleta WHERE ativo = true AND (LOWER(nome) LIKE ? OR LOWER(cidade) LIKE ? OR LOWER(logradouro) LIKE ? OR LOWER(bairro) LIKE ?)";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, termo);
            preparedStatement.setString(2, termo);
            preparedStatement.setString(3, termo);
            preparedStatement.setString(4, termo);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<PontoColetaModel> pontos = new ArrayList<>();
            while (resultSet.next()) {
                pontos.add(mapPonto(resultSet));
            }
            resultSet.close();
            preparedStatement.close();
            return pontos;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateInformation(final int id, final PontoColetaModel entity) {
        final String sql = "UPDATE pontos_coleta SET nome = ?, logradouro = ?, numero = ?, bairro = ?, cidade = ?, estado = ?, latitude = ?, longitude = ?, horario_funcionamento = ?, ativo = ? WHERE id_ponto = ?";
        try {
            connection.setAutoCommit(false);
            prepareDefaults(entity);
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, entity.getNome());
            preparedStatement.setString(2, entity.getLogradouro());
            preparedStatement.setString(3, entity.getNumero());
            preparedStatement.setString(4, entity.getBairro());
            preparedStatement.setString(5, entity.getCidade());
            preparedStatement.setString(6, entity.getEstado());
            preparedStatement.setDouble(7, entity.getLatitude());
            preparedStatement.setDouble(8, entity.getLongitude());
            preparedStatement.setString(9, entity.getHorario());
            preparedStatement.setBoolean(10, entity.isAtivo());
            preparedStatement.setInt(11, id);
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

    private void prepareDefaults(final PontoColetaModel entity) {
        if (entity.getEndereco() != null && !entity.getEndereco().isBlank()) {
            entity.setLogradouro(entity.getEndereco().trim());
        }
        if (entity.getLogradouro() == null || entity.getLogradouro().isBlank()) {
            entity.setLogradouro("");
        }
        if (entity.getNumero() == null || entity.getNumero().isBlank()) {
            entity.setNumero("");
        }
        if (entity.getBairro() == null || entity.getBairro().isBlank()) {
            entity.setBairro("");
        }
        if (entity.getCidade() == null || entity.getCidade().isBlank()) {
            entity.setCidade("Santa Rita do Sapucaí");
        }
        if (entity.getEstado() == null || entity.getEstado().isBlank()) {
            entity.setEstado("MG");
        }
        if (entity.getHorario() == null || entity.getHorario().isBlank()) {
            entity.setHorario("");
        }
        if (entity.getResiduos() == null) {
            entity.setResiduos(List.of());
        }
    }

    private PontoColetaModel mapPonto(final ResultSet resultSet) throws SQLException {
        final PontoColetaModel ponto = new PontoColetaModel();
        ponto.setId(resultSet.getInt("id_ponto"));
        ponto.setNome(resultSet.getString("nome"));
        ponto.setLogradouro(resultSet.getString("logradouro"));
        ponto.setNumero(resultSet.getString("numero"));
        ponto.setBairro(resultSet.getString("bairro"));
        ponto.setCidade(resultSet.getString("cidade"));
        ponto.setEstado(resultSet.getString("estado"));
        ponto.setLatitude(resultSet.getDouble("latitude"));
        ponto.setLongitude(resultSet.getDouble("longitude"));
        ponto.setTelefone("");
        ponto.setHorario(resultSet.getString("horario_funcionamento"));
        ponto.setResiduos(List.of());
        ponto.setAtivo(resultSet.getBoolean("ativo"));
        return ponto;
    }
}
