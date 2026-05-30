package dao;

import database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class ShipmentDAO {

    
    public List<Object[]> getAll() {
        List<Object[]> list = new ArrayList<>();

        String sql =
            "SELECT SH.TrackingNo, SH.Courier, SH.Ship_Date, SH.Ship_Status, "
          + "       SH.Deliv_Address, SH.Order_ID, C.FullName AS CustomerName "
          + "FROM SHIPMENT SH "
          + "JOIN [ORDER] O  ON SH.Order_ID   = O.Order_ID "
          + "JOIN CUSTOMER C ON O.Customer_ID = C.Customer_ID "
          + "ORDER BY SH.Ship_Date DESC";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("TrackingNo"),
                    rs.getString("Courier"),
                    rs.getDate("Ship_Date"),
                    rs.getString("Ship_Status"),
                    rs.getString("Order_ID"),
                    rs.getString("CustomerName"),
                    rs.getString("Deliv_Address")
                });
            }
        } catch (SQLException e) {
            System.err.println("[ShipmentDAO.getAll] " + e.getMessage());
        }
        return list;
    }

    
    public Object[] getByOrderId(String orderId) {
        String sql = "SELECT TrackingNo, Courier, Ship_Date, Ship_Status, Deliv_Address "
                   + "FROM SHIPMENT WHERE Order_ID = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Object[]{
                    rs.getString("TrackingNo"),
                    rs.getString("Courier"),
                    rs.getDate("Ship_Date"),
                    rs.getString("Ship_Status"),
                    rs.getString("Deliv_Address")
                };
            }
        } catch (SQLException e) {
            System.err.println("[ShipmentDAO.getByOrderId] " + e.getMessage());
        }
        return null;
    }

    public boolean insert(String trackingNo, String courier,
                          String shipStatus, String delivAddress, String orderId) {
        String sql = "INSERT INTO SHIPMENT (TrackingNo, Courier, Ship_Date, Ship_Status, Deliv_Address, Order_ID) "
                   + "VALUES (?, ?, GETDATE(), ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, trackingNo);
            ps.setString(2, courier);
            ps.setString(3, shipStatus);
            ps.setString(4, delivAddress);
            ps.setString(5, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ShipmentDAO.insert] " + e.getMessage());
            return false;
        }
    }

    
    public boolean updateStatus(String trackingNo, String newStatus) {
        String sql = "UPDATE SHIPMENT SET Ship_Status = ? WHERE TrackingNo = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, trackingNo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ShipmentDAO.updateStatus] " + e.getMessage());
            return false;
        }
    }

    
    public String generateTrackingNo() {
        String sql = "SELECT MAX(TrackingNo) AS MaxID FROM SHIPMENT";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next() && rs.getString("MaxID") != null) {

                int num = Integer.parseInt(rs.getString("MaxID").substring(4)) + 1;
                return String.format("TRK-%03d", num);
            }
        } catch (SQLException e) {
            System.err.println("[ShipmentDAO.generateTrackingNo] " + e.getMessage());
        }
        return "TRK-001";
    }
}