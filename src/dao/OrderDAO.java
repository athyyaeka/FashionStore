package dao;

import database.DBConnection;
import model.Order;
import model.OrderItem;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    public boolean createOrder(String orderId, String customerId, String promoCode) {
        String sql = "EXEC sp_CreateOrder @Order_ID=?, @Customer_ID=?, @Promo_Code=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, orderId);
            ps.setString(2, customerId);
            if (promoCode != null && !promoCode.isEmpty())
                ps.setString(3, promoCode);
            else
                ps.setNull(3, Types.VARCHAR);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String pesan = rs.getString("Pesan");
                System.out.println("[OrderDAO.createOrder] " + pesan);
                return pesan.contains("berhasil");
            }
        } catch (SQLException e) {
            System.err.println("[OrderDAO.createOrder] " + e.getMessage());
        }
        return false;
    }
    public boolean addOrderItem(String orderId, String productId, int quantity) {
        String sql = "EXEC sp_AddOrderItem @Order_ID=?, @Product_ID=?, @Quantity=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, orderId);
            ps.setString(2, productId);
            ps.setInt(3, quantity);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String pesan = rs.getString("Pesan");
                System.out.println("[OrderDAO.addOrderItem] " + pesan);
                return pesan.contains("berhasil");
            }
        } catch (SQLException e) {
            System.err.println("[OrderDAO.addOrderItem] " + e.getMessage());
        }
        return false;
    }
    public boolean payWithBank(String referenceNo, String orderId,
                               String bankName, String accNo) {
        String sql = "EXEC sp_PayWithBank @ReferenceNo=?, @Order_ID=?, @BankName=?, @AccNo=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, referenceNo);
            ps.setString(2, orderId);
            ps.setString(3, bankName);
            ps.setString(4, accNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("Pesan").contains("berhasil");
        } catch (SQLException e) {
            System.err.println("[OrderDAO.payWithBank] " + e.getMessage());
        }
        return false;
    }
    public boolean payWithWallet(String referenceNo, String orderId,
                                 String provider, String phone) {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            BigDecimal total = getTotalPrice(orderId, conn);
            if (total == null) throw new SQLException("Order tidak ditemukan: " + orderId);

            try (PreparedStatement psPay = conn.prepareStatement(
                    "INSERT INTO PAYMENT (ReferenceNo, Order_ID, Payment_Date, Amount, Payment_Status) "
                  + "VALUES (?, ?, GETDATE(), ?, 'Lunas')")) {
                psPay.setString(1, referenceNo);
                psPay.setString(2, orderId);
                psPay.setBigDecimal(3, total);
                psPay.executeUpdate();
            }
            try (PreparedStatement psWallet = conn.prepareStatement(
                    "INSERT INTO E_WALLET (ReferenceNo, Order_ID, Provider_Name, Acc_Phone) "
                  + "VALUES (?, ?, ?, ?)")) {
                psWallet.setString(1, referenceNo);
                psWallet.setString(2, orderId);
                psWallet.setString(3, provider);
                psWallet.setString(4, phone);
                psWallet.executeUpdate();
            }
            try (PreparedStatement psUpd = conn.prepareStatement(
                    "UPDATE [ORDER] SET Status='Menunggu Konfirmasi' WHERE Order_ID=?")) {
                psUpd.setString(1, orderId);
                psUpd.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("[OrderDAO.payWithWallet] " + e.getMessage());
            try { conn.rollback(); } catch (SQLException ignored) {}
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }
    public List<Order> getByCustomer(String customerId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM vw_OrderSummary "
                   + "WHERE Order_ID IN (SELECT Order_ID FROM [ORDER] WHERE Customer_ID = ?) "
                   + "ORDER BY Order_Date DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapViewRow(rs));
        } catch (SQLException e) {
            System.err.println("[OrderDAO.getByCustomer] " + e.getMessage());
        }
        return list;
    }

    public List<Order> getAll() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM vw_OrderSummary ORDER BY Order_Date DESC";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapViewRow(rs));
        } catch (SQLException e) {
            System.err.println("[OrderDAO.getAll] " + e.getMessage());
        }
        return list;
    }

    public List<Order> getByStatus(String status) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM vw_OrderSummary WHERE Status = ? ORDER BY Order_Date DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapViewRow(rs));
        } catch (SQLException e) {
            System.err.println("[OrderDAO.getByStatus] " + e.getMessage());
        }
        return list;
    }
    public List<OrderItem> getItems(String orderId) {
        List<OrderItem> list = new ArrayList<>();
        String sql =
            "SELECT OI.*, P.PName AS ProductName, S.StoreName "
          + "FROM ORDER_ITEM OI "
          + "JOIN PRODUCT P ON OI.Product_ID = P.Product_ID "
          + "JOIN SELLER S  ON P.seller_Id   = S.seller_Id "   
          + "WHERE OI.Order_ID = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setOrderId(rs.getString("Order_ID"));
                item.setProductId(rs.getString("Product_ID"));
                item.setQuantity(rs.getInt("Quantity"));
                item.setUnitPrice(rs.getBigDecimal("Unit_Price"));
                item.setProductName(rs.getString("ProductName"));
                item.setStoreName(rs.getString("StoreName"));
                list.add(item);
            }
        } catch (SQLException e) {
            System.err.println("[OrderDAO.getItems] " + e.getMessage());
        }
        return list;
    }
    public boolean updateStatus(String orderId, String newStatus) {
        String sql = "UPDATE [ORDER] SET Status = ? WHERE Order_ID = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[OrderDAO.updateStatus] " + e.getMessage());
            return false;
        }
    }
    public String generateOrderId() {
        String sql = "SELECT MAX(Order_ID) AS MaxID FROM [ORDER]";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next() && rs.getString("MaxID") != null) {
                int num = Integer.parseInt(rs.getString("MaxID").substring(4)) + 1;
                return String.format("ORD-%03d", num);
            }
        } catch (SQLException e) {
            System.err.println("[OrderDAO.generateOrderId] " + e.getMessage());
        }
        return "ORD-101";
    }

    public String generatePaymentRef() {
        String sql = "SELECT MAX(ReferenceNo) AS MaxID FROM PAYMENT";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next() && rs.getString("MaxID") != null) {

                int num = Integer.parseInt(rs.getString("MaxID").substring(4)) + 1;
                return String.format("PAY-%03d", num);
            }
        } catch (SQLException e) {
            System.err.println("[OrderDAO.generatePaymentRef] " + e.getMessage());
        }
        return "PAY-001";
    }
    private BigDecimal getTotalPrice(String orderId, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT Total_Price FROM [ORDER] WHERE Order_ID = ?")) {
            ps.setString(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getBigDecimal("Total_Price");
        }
        return null;
    }

    private Order mapViewRow(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setOrderId(rs.getString("Order_ID"));
        try { o.setOrderDate(rs.getDate("Order_Date")); } catch (SQLException ignored) {}
        o.setStatus(rs.getString("Status"));
        o.setTotalPrice(rs.getBigDecimal("Total_Price"));
        o.setCustomerName(rs.getString("CustomerName"));
        o.setPromoCode(rs.getString("Promo_Code"));
        try { o.setTrackingNo(rs.getString("TrackingNo")); } catch (SQLException ignored) {}
        try { o.setShipStatus(rs.getString("Ship_Status")); } catch (SQLException ignored) {}
        return o;
    }
}