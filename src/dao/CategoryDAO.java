package dao;

import database.DBConnection;
import model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public List<Category> getAll() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM CATEGORY ORDER BY CName";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Category(
                    rs.getString("Category_ID"),
                    rs.getString("CName"),
                    rs.getString("Description")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[CategoryDAO.getAll] " + e.getMessage());
        }
        return list;
    }

    public boolean insert(Category c) {
        String sql = "INSERT INTO CATEGORY (Category_ID, CName, Description) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, c.getCategoryId());
            ps.setString(2, c.getCName());
            ps.setString(3, c.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CategoryDAO.insert] " + e.getMessage());
            return false;
        }
    }

    public boolean update(Category c) {
        String sql = "UPDATE CATEGORY SET CName=?, Description=? WHERE Category_ID=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, c.getCName());
            ps.setString(2, c.getDescription());
            ps.setString(3, c.getCategoryId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CategoryDAO.update] " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String categoryId) {
        String sql = "DELETE FROM CATEGORY WHERE Category_ID = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, categoryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CategoryDAO.delete] " + e.getMessage());
            return false;
        }
    }

    public String generateId() {
        String sql = "SELECT MAX(Category_ID) AS MaxID FROM CATEGORY";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next() && rs.getString("MaxID") != null) {
                int num = Integer.parseInt(rs.getString("MaxID").substring(4)) + 1;
                return String.format("CAT-%02d", num);
            }
        } catch (SQLException e) {
            System.err.println("[CategoryDAO.generateId] " + e.getMessage());
        }
        return "CAT-01";
    }
}