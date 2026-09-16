package br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.dao.collectionpoint;

import br.fai.lds.e_lixo_zero.domain.CollectionPointModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.collectionpoint.CollectionPointDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CollectionPointPostgresDaoAdapter implements CollectionPointDao {

    private final Connection connection;

    public CollectionPointPostgresDaoAdapter(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public int add(final CollectionPointModel entity) {
        final String sql = "INSERT INTO collection_points (name, street, number, neighborhood, city, state, latitude, longitude, phone, opening_hours, accepted_waste_types, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            connection.setAutoCommit(false);
            prepareDefaults(entity);
            final PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, entity.getName());
            preparedStatement.setString(2, entity.getStreet());
            preparedStatement.setString(3, entity.getNumber());
            preparedStatement.setString(4, entity.getNeighborhood());
            preparedStatement.setString(5, entity.getCity());
            preparedStatement.setString(6, entity.getState());
            preparedStatement.setDouble(7, entity.getLatitude());
            preparedStatement.setDouble(8, entity.getLongitude());
            preparedStatement.setString(9, entity.getPhone());
            preparedStatement.setString(10, entity.getOpeningHours());
            preparedStatement.setString(11, joinAcceptedWastes(entity.getAcceptedWastes()));
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
        final String sql = "DELETE FROM collection_points WHERE point_id = ?";
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
    public CollectionPointModel readById(final int id) {
        final String sql = "SELECT * FROM collection_points WHERE point_id = ?";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            CollectionPointModel point = null;
            if (resultSet.next()) {
                point = mapPoint(resultSet);
            }
            resultSet.close();
            preparedStatement.close();
            return point;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CollectionPointModel> readAll() {
        final String sql = "SELECT * FROM collection_points WHERE active = true";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<CollectionPointModel> points = new ArrayList<>();
            while (resultSet.next()) {
                points.add(mapPoint(resultSet));
            }
            resultSet.close();
            preparedStatement.close();
            return points;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CollectionPointModel> readByCity(final String city) {
        if (city == null || city.isBlank()) {
            return readAll();
        }
        final String term = "%" + city.trim().toLowerCase() + "%";
        final String sql = "SELECT * FROM collection_points WHERE active = true AND (LOWER(name) LIKE ? OR LOWER(city) LIKE ? OR LOWER(street) LIKE ? OR LOWER(neighborhood) LIKE ?)";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, term);
            preparedStatement.setString(2, term);
            preparedStatement.setString(3, term);
            preparedStatement.setString(4, term);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<CollectionPointModel> points = new ArrayList<>();
            while (resultSet.next()) {
                points.add(mapPoint(resultSet));
            }
            resultSet.close();
            preparedStatement.close();
            return points;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateInformation(final int id, final CollectionPointModel entity) {
        final String sql = "UPDATE collection_points SET name = ?, street = ?, number = ?, neighborhood = ?, city = ?, state = ?, latitude = ?, longitude = ?, phone = ?, opening_hours = ?, accepted_waste_types = ?, active = ? WHERE point_id = ?";
        try {
            connection.setAutoCommit(false);
            prepareDefaults(entity);
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, entity.getName());
            preparedStatement.setString(2, entity.getStreet());
            preparedStatement.setString(3, entity.getNumber());
            preparedStatement.setString(4, entity.getNeighborhood());
            preparedStatement.setString(5, entity.getCity());
            preparedStatement.setString(6, entity.getState());
            preparedStatement.setDouble(7, entity.getLatitude());
            preparedStatement.setDouble(8, entity.getLongitude());
            preparedStatement.setString(9, entity.getPhone());
            preparedStatement.setString(10, entity.getOpeningHours());
            preparedStatement.setString(11, joinAcceptedWastes(entity.getAcceptedWastes()));
            preparedStatement.setBoolean(12, entity.isActive());
            preparedStatement.setInt(13, id);
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

    private void prepareDefaults(final CollectionPointModel entity) {
        if ((entity.getStreet() == null || entity.getStreet().isBlank())
                && entity.getAddress() != null && !entity.getAddress().isBlank()) {
            entity.setStreet(entity.getAddress().trim());
        }
        if (entity.getStreet() == null || entity.getStreet().isBlank()) {
            entity.setStreet("");
        }
        if (entity.getNumber() == null || entity.getNumber().isBlank()) {
            entity.setNumber("");
        }
        if (entity.getNeighborhood() == null || entity.getNeighborhood().isBlank()) {
            entity.setNeighborhood("");
        }
        if (entity.getCity() == null || entity.getCity().isBlank()) {
            entity.setCity("Santa Rita do Sapucaí");
        }
        if (entity.getState() == null || entity.getState().isBlank()) {
            entity.setState("MG");
        }
        if (entity.getOpeningHours() == null || entity.getOpeningHours().isBlank()) {
            entity.setOpeningHours("");
        }
        if (entity.getAcceptedWastes() == null) {
            entity.setAcceptedWastes(List.of());
        }
    }

    private CollectionPointModel mapPoint(final ResultSet resultSet) throws SQLException {
        final CollectionPointModel point = new CollectionPointModel();
        point.setId(resultSet.getInt("point_id"));
        point.setName(resultSet.getString("name"));
        point.setStreet(resultSet.getString("street"));
        point.setNumber(resultSet.getString("number"));
        point.setNeighborhood(resultSet.getString("neighborhood"));
        point.setCity(resultSet.getString("city"));
        point.setState(resultSet.getString("state"));
        point.setLatitude(resultSet.getDouble("latitude"));
        point.setLongitude(resultSet.getDouble("longitude"));
        point.setPhone(resultSet.getString("phone"));
        point.setOpeningHours(resultSet.getString("opening_hours"));
        point.setAcceptedWastes(parseAcceptedWastes(resultSet.getString("accepted_waste_types")));
        point.setActive(resultSet.getBoolean("active"));
        return point;
    }

    private String joinAcceptedWastes(final List<String> acceptedWastes) {
        if (acceptedWastes == null || acceptedWastes.isEmpty()) {
            return "";
        }
        return String.join(",", acceptedWastes);
    }

    private List<String> parseAcceptedWastes(final String acceptedWastes) {
        final List<String> wastes = new ArrayList<>();
        if (acceptedWastes == null || acceptedWastes.isBlank()) {
            return wastes;
        }
        for (final String waste : acceptedWastes.split(",")) {
            final String trimmed = waste.trim();
            if (!trimmed.isEmpty()) {
                wastes.add(trimmed);
            }
        }
        return wastes;
    }
}
