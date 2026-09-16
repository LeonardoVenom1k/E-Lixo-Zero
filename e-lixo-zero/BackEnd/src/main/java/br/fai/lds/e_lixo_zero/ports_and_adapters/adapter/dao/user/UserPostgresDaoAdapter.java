package br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.dao.user;

import br.fai.lds.e_lixo_zero.domain.UserModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.user.UserDao;

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
        final String sql = "INSERT INTO users (full_name, cpf, email, phone, street, number, neighborhood, city, state, password, user_type, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            connection.setAutoCommit(false);

            final PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, entity.getFullName());
            preparedStatement.setString(2, entity.getCpf());
            preparedStatement.setString(3, entity.getEmail());
            preparedStatement.setString(4, entity.getPhone());
            preparedStatement.setString(5, entity.getStreet());
            preparedStatement.setString(6, entity.getNumber());
            preparedStatement.setString(7, entity.getNeighborhood());
            preparedStatement.setString(8, entity.getCity());
            preparedStatement.setString(9, entity.getState());
            preparedStatement.setString(10, entity.getPassword());
            preparedStatement.setString(11, entity.getUserType());
            preparedStatement.setBoolean(12, entity.isActive());

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
        final String sql = "DELETE FROM users WHERE user_id = ?";
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
    public UserModel readById(final int id) {
        final String sql = "SELECT * FROM users WHERE user_id = ?";
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
        final String sql = "SELECT * FROM users";
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
        final String sql = "UPDATE users SET full_name = ?, cpf = ?, phone = ?, street = ?, number = ?, neighborhood = ?, city = ?, state = ?, user_type = ?, active = ? WHERE user_id = ?";
        try {
            connection.setAutoCommit(false);

            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, entity.getFullName());
            preparedStatement.setString(2, entity.getCpf());
            preparedStatement.setString(3, entity.getPhone());
            preparedStatement.setString(4, entity.getStreet());
            preparedStatement.setString(5, entity.getNumber());
            preparedStatement.setString(6, entity.getNeighborhood());
            preparedStatement.setString(7, entity.getCity());
            preparedStatement.setString(8, entity.getState());
            preparedStatement.setString(9, entity.getUserType());
            preparedStatement.setBoolean(10, entity.isActive());
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
        final String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?)";
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
        user.setId(resultSet.getInt("user_id"));
        user.setFullName(resultSet.getString("full_name"));
        user.setCpf(resultSet.getString("cpf"));
        user.setEmail(resultSet.getString("email"));
        user.setPhone(resultSet.getString("phone"));
        user.setStreet(resultSet.getString("street"));
        user.setNumber(resultSet.getString("number"));
        user.setNeighborhood(resultSet.getString("neighborhood"));
        user.setCity(resultSet.getString("city"));
        user.setState(resultSet.getString("state"));
        user.setPassword(resultSet.getString("password"));
        user.setUserType(resultSet.getString("user_type"));
        user.setActive(resultSet.getBoolean("active"));
        return user;
    }
}
