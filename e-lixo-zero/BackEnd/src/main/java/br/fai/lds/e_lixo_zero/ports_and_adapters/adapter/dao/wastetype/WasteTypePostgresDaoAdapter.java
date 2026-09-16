package br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.dao.wastetype;

import br.fai.lds.e_lixo_zero.domain.WasteTypeModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.wastetype.WasteTypeDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WasteTypePostgresDaoAdapter implements WasteTypeDao {

    private final Connection connection;

    public WasteTypePostgresDaoAdapter(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public int add(final WasteTypeModel entity) {
        final String sql = "INSERT INTO waste_types (name, category, description, active) VALUES (?, ?, ?, ?)";
        try {
            connection.setAutoCommit(false);
            final PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, entity.getName());
            preparedStatement.setString(2, entity.getCategory());
            preparedStatement.setString(3, entity.getDescription());
            preparedStatement.setBoolean(4, entity.isActive());
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
        final String sql = "DELETE FROM waste_types WHERE waste_type_id = ?";
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
    public WasteTypeModel readById(final int id) {
        final String sql = "SELECT * FROM waste_types WHERE waste_type_id = ?";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            WasteTypeModel waste = null;
            if (resultSet.next()) {
                waste = mapWasteType(resultSet);
            }
            resultSet.close();
            preparedStatement.close();
            return waste;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<WasteTypeModel> readAll() {
        final String sql = "SELECT * FROM waste_types WHERE active = true";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<WasteTypeModel> wasteTypes = new ArrayList<>();
            while (resultSet.next()) {
                wasteTypes.add(mapWasteType(resultSet));
            }
            resultSet.close();
            preparedStatement.close();
            return wasteTypes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public WasteTypeModel readByName(final String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        final String sql = "SELECT * FROM waste_types WHERE LOWER(name) = LOWER(?)";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name.trim());
            final ResultSet resultSet = preparedStatement.executeQuery();
            WasteTypeModel waste = null;
            if (resultSet.next()) {
                waste = mapWasteType(resultSet);
            }
            resultSet.close();
            preparedStatement.close();
            return waste;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateInformation(final int id, final WasteTypeModel entity) {
        final String sql = "UPDATE waste_types SET name = ?, category = ?, description = ?, active = ? WHERE waste_type_id = ?";
        try {
            connection.setAutoCommit(false);
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, entity.getName());
            preparedStatement.setString(2, entity.getCategory());
            preparedStatement.setString(3, entity.getDescription());
            preparedStatement.setBoolean(4, entity.isActive());
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

    private WasteTypeModel mapWasteType(final ResultSet resultSet) throws SQLException {
        final WasteTypeModel waste = new WasteTypeModel();
        waste.setId(resultSet.getInt("waste_type_id"));
        waste.setName(resultSet.getString("name"));
        waste.setCategory(resultSet.getString("category"));
        waste.setDescription(resultSet.getString("description"));
        waste.setActive(resultSet.getBoolean("active"));
        return waste;
    }
}
