package dao;

import database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    public List<Object[]> getByProduct(String productId) {
        List<Object[]> list = new ArrayList<>();
        String sql =
            "SELECT R.Review_ID, C.FullName AS CustomerName, R.Rating, R.Comment, R.Review_Date "
          + "FROM REVIEW R "
          + "JOIN [ORDER] O    ON R.Order_ID    = O.Order_ID "
          + "JOIN CUSTOMER C   ON O.Customer_ID = C.Customer_ID "
          + "WHERE R.Product_ID = ? ORDER BY R.Review_Date DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, productId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("Review_ID"),
                    rs.getString("CustomerName"),
                    rs.getInt("Rating"),
                    rs.getString("Comment"),
                    rs.getDate("Review_Date")
                });
            }
        } catch (SQLException e) {
            System.err.println("[ReviewDAO.getByProduct] " + e.getMessage());
        }
        return list;
    }

    public double getAvgRating(String productId) {
        String sql = "SELECT dbo.fn_ProductRating(?) AS AvgRating";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("AvgRating");
        } catch (SQLException e) {
            System.err.println("[ReviewDAO.getAvgRating] " + e.getMessage());
        }
        return 0.0;
    }

    public boolean insert(String reviewId, int rating, String comment,
                          String orderId, String productId) {
        String sql = "INSERT INTO REVIEW (Review_ID, Rating, Comment, Review_Date, Order_ID, Product_ID) "
                   + "VALUES (?, ?, ?, GETDATE(), ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, reviewId);
            ps.setInt(2, rating);
            ps.setString(3, comment);
            ps.setString(4, orderId);
            ps.setString(5, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ReviewDAO.insert] " + e.getMessage());
            return false;
        }
    }

    public String generateId() {
        String sql = "SELECT MAX(Review_ID) AS MaxID FROM REVIEW";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next() && rs.getString("MaxID") != null) {

                int num = Integer.parseInt(rs.getString("MaxID").substring(4)) + 1;
                return String.format("REV-%03d", num);
            }
        } catch (SQLException e) {
            System.err.println("[ReviewDAO.generateId] " + e.getMessage());
        }
        return "REV-001";
    }
}