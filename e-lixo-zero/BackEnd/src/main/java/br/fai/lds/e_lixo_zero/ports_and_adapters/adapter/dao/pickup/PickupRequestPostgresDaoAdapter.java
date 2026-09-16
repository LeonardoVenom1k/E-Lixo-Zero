package br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.dao.pickup;

import br.fai.lds.e_lixo_zero.domain.PickupRequestModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.pickup.PickupRequestDao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PickupRequestPostgresDaoAdapter implements PickupRequestDao {

    private final Connection connection;

    public PickupRequestPostgresDaoAdapter(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public int add(final PickupRequestModel entity) {
        final String sql = "INSERT INTO pickup_requests (user_id, waste_type_id, street, number, neighborhood, city, state, estimated_quantity, desired_date, status, notes, requested_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp, current_timestamp)";
        try {
            connection.setAutoCommit(false);
            final PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"pickup_id"});
            preparedStatement.setInt(1, entity.getUserId());
            preparedStatement.setInt(2, entity.getWasteTypeId());
            preparedStatement.setString(3, entity.getStreet());
            preparedStatement.setString(4, entity.getNumber());
            preparedStatement.setString(5, entity.getNeighborhood());
            preparedStatement.setString(6, entity.getCity());
            preparedStatement.setString(7, entity.getState());
            preparedStatement.setString(8, entity.getEstimatedQuantity());
            preparedStatement.setDate(9, Date.valueOf(entity.getDesiredDate()));
            preparedStatement.setString(10, entity.getStatus());
            preparedStatement.setString(11, entity.getNotes());
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
        final String sql = "DELETE FROM pickup_requests WHERE pickup_id = ?";
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
    public PickupRequestModel readById(final int id) {
        final String sql = "SELECT * FROM pickup_requests WHERE pickup_id = ?";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            PickupRequestModel pickup = null;
            if (resultSet.next()) {
                pickup = mapPickup(resultSet);
            }
            resultSet.close();
            preparedStatement.close();
            return pickup;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PickupRequestModel> readAll() {
        final String sql = "SELECT * FROM pickup_requests";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<PickupRequestModel> pickups = new ArrayList<>();
            while (resultSet.next()) {
                pickups.add(mapPickup(resultSet));
            }
            resultSet.close();
            preparedStatement.close();
            return pickups;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PickupRequestModel> readByUserId(final int userId) {
        final String sql = "SELECT * FROM pickup_requests WHERE user_id = ?";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<PickupRequestModel> pickups = new ArrayList<>();
            while (resultSet.next()) {
                pickups.add(mapPickup(resultSet));
            }
            resultSet.close();
            preparedStatement.close();
            return pickups;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateInformation(final int id, final PickupRequestModel entity) {
        final String sql = "UPDATE pickup_requests SET user_id = ?, waste_type_id = ?, street = ?, number = ?, neighborhood = ?, city = ?, state = ?, estimated_quantity = ?, desired_date = ?, status = ?, notes = ?, updated_at = current_timestamp WHERE pickup_id = ?";
        try {
            connection.setAutoCommit(false);
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, entity.getUserId());
            preparedStatement.setInt(2, entity.getWasteTypeId());
            preparedStatement.setString(3, entity.getStreet());
            preparedStatement.setString(4, entity.getNumber());
            preparedStatement.setString(5, entity.getNeighborhood());
            preparedStatement.setString(6, entity.getCity());
            preparedStatement.setString(7, entity.getState());
            preparedStatement.setString(8, entity.getEstimatedQuantity());
            preparedStatement.setDate(9, Date.valueOf(entity.getDesiredDate()));
            preparedStatement.setString(10, entity.getStatus());
            preparedStatement.setString(11, entity.getNotes());
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
        final String sql = "UPDATE pickup_requests SET status = ?, updated_at = current_timestamp WHERE pickup_id = ?";
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

    private PickupRequestModel mapPickup(final ResultSet resultSet) throws SQLException {
        final PickupRequestModel pickup = new PickupRequestModel();
        pickup.setId(resultSet.getInt("pickup_id"));
        pickup.setUserId(resultSet.getInt("user_id"));
        pickup.setWasteTypeId(resultSet.getInt("waste_type_id"));
        final int collectorId = resultSet.getInt("collector_id");
        pickup.setCollectorId(resultSet.wasNull() ? 0 : collectorId);
        pickup.setStreet(resultSet.getString("street"));
        pickup.setNumber(resultSet.getString("number"));
        pickup.setNeighborhood(resultSet.getString("neighborhood"));
        pickup.setCity(resultSet.getString("city"));
        pickup.setState(resultSet.getString("state"));
        pickup.setEstimatedQuantity(resultSet.getString("estimated_quantity"));
        final Date date = resultSet.getDate("desired_date");
        pickup.setDesiredDate(date != null ? date.toLocalDate().toString() : null);
        pickup.setStatus(resultSet.getString("status"));
        pickup.setNotes(resultSet.getString("notes"));
        return pickup;
    }
}
