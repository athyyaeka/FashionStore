package dao;

import database.DBConnection;
import model.Customer;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    public Customer login(String email, String password) {
        String sql = "SELECT Customer_ID, FullName, Email, Password, Balance "
                   + "FROM CUSTOMER WHERE Email = ? AND Password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO.login] " + e.getMessage());
        }
        return null;
    }
    public boolean register(Customer c) {
        String sql = "INSERT INTO CUSTOMER (Customer_ID, FullName, Email, Password, Balance, Reg_Date) "
                   + "VALUES (?, ?, ?, ?, ?, GETDATE())";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, c.getCustomerId());
            ps.setString(2, c.getFullName());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getPassword());
            ps.setBigDecimal(5, c.getBalance() != null ? c.getBalance() : BigDecimal.ZERO);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO.register] " + e.getMessage());
            return false;
        }
    }
    public List<Customer> getAll() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM CUSTOMER ORDER BY FullName";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[CustomerDAO.getAll] " + e.getMessage());
        }
        return list;
    }

    public Customer getById(String customerId) {
        String sql = "SELECT * FROM CUSTOMER WHERE Customer_ID = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[CustomerDAO.getById] " + e.getMessage());
        }
        return null;
    }

    public List<Customer> search(String keyword) {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM CUSTOMER WHERE FullName LIKE ? OR Email LIKE ? ORDER BY FullName";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[CustomerDAO.search] " + e.getMessage());
        }
        return list;
    }

    public List<String> getPhones(String customerId) {
        List<String> phones = new ArrayList<>();
        String sql = "SELECT Phone_No FROM CUSTOMER_PHONE WHERE Customer_ID = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) phones.add(rs.getString("Phone_No"));
        } catch (SQLException e) {
            System.err.println("[CustomerDAO.getPhones] " + e.getMessage());
        }
        return phones;
    }
    public boolean addPhone(String customerId, String phoneNo) {
        String sql = "INSERT INTO CUSTOMER_PHONE (Customer_ID, Phone_No) VALUES (?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, customerId);
            ps.setString(2, phoneNo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO.addPhone] " + e.getMessage());
            return false;
        }
    }
    public boolean update(Customer c) {
        String sql = "UPDATE CUSTOMER SET FullName=?, Email=?, Password=? WHERE Customer_ID=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, c.getFullName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPassword());
            ps.setString(4, c.getCustomerId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO.update] " + e.getMessage());
            return false;
        }
    }
    public BigDecimal topUpBalance(String customerId, BigDecimal amount) {
        String sql = "EXEC sp_TopUpBalance @Customer_ID = ?, @Amount = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, customerId);
            ps.setBigDecimal(2, amount);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getBigDecimal("SaldoBaru");
        } catch (SQLException e) {
            System.err.println("[CustomerDAO.topUpBalance] " + e.getMessage());
        }
        return null;
    }
    public boolean delete(String customerId) {
        String sql = "DELETE FROM CUSTOMER WHERE Customer_ID = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, customerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO.delete] " + e.getMessage());
            return false;
        }
    }
    public String generateId() {
        String sql = "SELECT MAX(Customer_ID) AS MaxID FROM CUSTOMER";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next() && rs.getString("MaxID") != null) {
                int num = Integer.parseInt(rs.getString("MaxID").substring(1)) + 1;
                return String.format("C%03d", num);
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO.generateId] " + e.getMessage());
        }
        return "C001";
    }
    private Customer mapRow(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getString("Customer_ID"));
        c.setFullName(rs.getString("FullName"));
        c.setEmail(rs.getString("Email"));
        c.setPassword(rs.getString("Password"));
        c.setBalance(rs.getBigDecimal("Balance"));
        try { c.setRegDate(rs.getDate("Reg_Date")); } catch (SQLException ignored) {}
        return c;
    }
}