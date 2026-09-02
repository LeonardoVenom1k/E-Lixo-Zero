package br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.dao.configuration;

import br.fai.lds.e_lixo_zero.domain.UserModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.usuario.UserDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserPostgresDaoAdapter implements UserDao {

    private final Connection connection;

    public UserPostgresDaoAdapter(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public int add(final UserModel entity) {
        final String sql = "INSERT INTO usuarios (nome_completo, cpf, email, telefone, logradouro, numero, bairro, cidade, estado, senha, tipo_usuario, ativo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            connection.setAutoCommit(false);

            final PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, entity.getNomeCompleto());
            preparedStatement.setString(2, entity.getCpf());
            preparedStatement.setString(3, entity.getEmail());
            preparedStatement.setString(4, entity.getTelefone());
            preparedStatement.setString(5, entity.getLogradouro());
            preparedStatement.setString(6, entity.getNumero());
            preparedStatement.setString(7, entity.getBairro());
            preparedStatement.setString(8, entity.getCidade());
            preparedStatement.setString(9, entity.getEstado());
            preparedStatement.setString(10, entity.getSenha());
            preparedStatement.setString(11, entity.getTipoUsuario());
            preparedStatement.setBoolean(12, entity.isAtivo());

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
        final String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
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
    public UserModel readyById(final int id) {
        final String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);

            final ResultSet resultSet = preparedStatement.executeQuery();

            UserModel user = null;
            if (resultSet.next()) {
                user = mapUser(resultSet);
            }

            resultSet.close();
            preparedStatement.close();

            return user;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UserModel> readAll() {
        final String sql = "SELECT * FROM usuarios";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            final ResultSet resultSet = preparedStatement.executeQuery();

            final List<UserModel> users = new ArrayList<>();
            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }

            resultSet.close();
            preparedStatement.close();

            return users;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateInformation(final int id, final UserModel entity) {
        final String sql = "UPDATE usuarios SET nome_completo = ?, cpf = ?, telefone = ?, logradouro = ?, numero = ?, bairro = ?, cidade = ?, estado = ?, tipo_usuario = ?, ativo = ? WHERE id_usuario = ?";
        try {
            connection.setAutoCommit(false);

            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, entity.getNomeCompleto());
            preparedStatement.setString(2, entity.getCpf());
            preparedStatement.setString(3, entity.getTelefone());
            preparedStatement.setString(4, entity.getLogradouro());
            preparedStatement.setString(5, entity.getNumero());
            preparedStatement.setString(6, entity.getBairro());
            preparedStatement.setString(7, entity.getCidade());
            preparedStatement.setString(8, entity.getEstado());
            preparedStatement.setString(9, entity.getTipoUsuario());
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

    @Override
    public UserModel readByEmail(final String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        final String sql = "SELECT * FROM usuarios WHERE LOWER(email) = LOWER(?)";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, email.trim());

            final ResultSet resultSet = preparedStatement.executeQuery();

            UserModel user = null;
            if (resultSet.next()) {
                user = mapUser(resultSet);
            }

            resultSet.close();
            preparedStatement.close();

            return user;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private UserModel mapUser(final ResultSet resultSet) throws SQLException {
        final UserModel user = new UserModel();
        user.setId(resultSet.getInt("id_usuario"));
        user.setNomeCompleto(resultSet.getString("nome_completo"));
        user.setCpf(resultSet.getString("cpf"));
        user.setEmail(resultSet.getString("email"));
        user.setTelefone(resultSet.getString("telefone"));
        user.setLogradouro(resultSet.getString("logradouro"));
        user.setNumero(resultSet.getString("numero"));
        user.setBairro(resultSet.getString("bairro"));
        user.setCidade(resultSet.getString("cidade"));
        user.setEstado(resultSet.getString("estado"));
        user.setSenha(resultSet.getString("senha"));
        user.setTipoUsuario(resultSet.getString("tipo_usuario"));
        user.setAtivo(resultSet.getBoolean("ativo"));
        return user;
    }
}
