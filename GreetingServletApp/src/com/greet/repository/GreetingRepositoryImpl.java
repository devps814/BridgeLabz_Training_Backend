package com.greet.repository;

import com.greet.model.Greeting;
import com.greet.util.DatabaseUtil;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GreetingRepositoryImpl implements GreetingRepository {
    @Override
    public List<Greeting> findAll() {
        String sql = "select g.id, g.message, g.image_path, g.created_by as created_by_id, "
                + "u.username as created_by_name "
                + "from greetings g "
                + "left join users u on g.created_by = u.id "
                + "order by g.id desc";
        List<Greeting> greetings = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                greetings.add(mapGreeting(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load greetings", e);
        }

        return greetings;
    }

    @Override
    public Greeting findById(int id) {
        String sql = "select g.id, g.message, g.image_path, g.created_by as created_by_id, "
                + "u.username as created_by_name "
                + "from greetings g "
                + "left join users u on g.created_by = u.id "
                + "where g.id = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapGreeting(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find greeting by id", e);
        }

        return null;
    }

    @Override
    public boolean save(Greeting greeting) {
        String sql = "insert into greetings (message, image_path, created_by) values (?, ?, ?)";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, greeting.getMessage());
            statement.setString(2, greeting.getImagePath());
            statement.setInt(3, greeting.getCreatedById());
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save greeting", e);
        }
    }

    @Override
    public boolean update(Greeting greeting) {
        String sql = "update greetings set message = ?, image_path = ? where id = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, greeting.getMessage());
            statement.setString(2, greeting.getImagePath());
            statement.setInt(3, greeting.getId());
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update greeting", e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "delete from greetings where id = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete greeting", e);
        }
    }

    private Greeting mapGreeting(ResultSet resultSet) throws SQLException {
        Greeting greeting = new Greeting();
        greeting.setId(resultSet.getInt("id"));
        greeting.setMessage(resultSet.getString("message"));
        greeting.setImagePath(resultSet.getString("image_path"));
        greeting.setCreatedById(resultSet.getInt("created_by_id"));
        greeting.setCreatedByName(resultSet.getString("created_by_name"));
        return greeting;
    }
}
