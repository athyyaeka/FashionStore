package dao;

import database.DBConnection;
import model.Seller;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SellerDAO {

    public Seller login(String username, String password) {
        String sql = "SELECT * FROM SELLER WHERE username = ? AND password = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[SellerDAO.login] " + e.getMessage());
        }
        return null;
    }

    public List<Seller> getAll() {
        List<Seller> list = new ArrayList<>();
        String sql = "SELECT * FROM SELLER ORDER BY StoreName";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[SellerDAO.getAll] " + e.getMessage());
        }
        return list;
    }

    public Seller getById(String sellerId) {
        String sql = "SELECT * FROM SELLER WHERE seller_Id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, sellerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[SellerDAO.getById] " + e.getMessage());
        }
        return null;
    }

    public boolean insert(Seller s) {
        String sql = "INSERT INTO SELLER (seller_Id, username, password, email, StoreName, Balance) "
                   + "VALUES (?, ?, ?, ?, ?, 0)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, s.getSellerId());
            ps.setString(2, s.getUsername());
            ps.setString(3, s.getPassword());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getStoreName());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[SellerDAO.insert] " + e.getMessage());
            return false;
        }
    }

    public boolean update(Seller s) {
        String sql = "UPDATE SELLER SET username=?, password=?, email=?, StoreName=? "
                   + "WHERE seller_Id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, s.getUsername());
            ps.setString(2, s.getPassword());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getStoreName());
            ps.setString(5, s.getSellerId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[SellerDAO.update] " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String sellerId) {
        String sql = "DELETE FROM SELLER WHERE seller_Id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, sellerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[SellerDAO.delete] " + e.getMessage());
            return false;
        }
    }

    public String generateId() {
        String sql = "SELECT MAX(seller_Id) AS MaxID FROM SELLER";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next() && rs.getString("MaxID") != null) {
                int num = Integer.parseInt(rs.getString("MaxID").substring(1)) + 1;
                return String.format("S%03d", num);
            }
        } catch (SQLException e) {
            System.err.println("[SellerDAO.generateId] " + e.getMessage());
        }
        return "S001";
    }

    private Seller mapRow(ResultSet rs) throws SQLException {
        Seller s = new Seller();
        s.setSellerId(rs.getString("seller_Id"));
        s.setUsername(rs.getString("username"));
        s.setPassword(rs.getString("password"));
        s.setEmail(rs.getString("email"));
        s.setStoreName(rs.getString("StoreName"));
        s.setBalance(rs.getBigDecimal("Balance"));
        return s;
    }
}