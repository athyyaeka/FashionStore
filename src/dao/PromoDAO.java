package dao;

import database.DBConnection;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class PromoDAO {

    public List<Object[]> getAll() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT Promo_Code, Discount_Pct, Is_Active FROM PROMO ORDER BY Promo_Code";
        
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
             
            while (rs.next()) {
                BigDecimal rawDiscount = rs.getBigDecimal("Discount_Pct");
                String displayPercent = "0%";
                if (rawDiscount != null) {
                    displayPercent = String.format("%.0f%%", rawDiscount.multiply(new BigDecimal(100)));
                }
                
                list.add(new Object[]{
                    rs.getString("Promo_Code"),
                    displayPercent,
                    rs.getBoolean("Is_Active") ? "Aktif" : "Tidak Aktif"
                });
            }
        } catch (SQLException e) {
            System.err.println("[PromoDAO.getAll] " + e.getMessage());
        }
        return list;
    }

    public BigDecimal applyPromo(String promoCode, BigDecimal amount) {
        String sql = "SELECT dbo.fn_ApplyPromo(?, ?) AS HargaAkhir";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, promoCode);
            ps.setBigDecimal(2, amount);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal("HargaAkhir");
            }
        } catch (SQLException e) {
            System.err.println("[PromoDAO.applyPromo] " + e.getMessage());
        }
        return amount;
    }

    public boolean insert(String promoCode, BigDecimal discPct, boolean isActive) {
        String sql = "INSERT INTO PROMO (Promo_Code, Description, Discount_Pct, Min_Purchase, Valid_From, Valid_Until, Is_Active) "
                   + "VALUES (?, ?, ?, 0, GETDATE(), DATEADD(year, 1, GETDATE()), ?)";
                   
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, promoCode.toUpperCase());
            ps.setString(2, "Voucher Promo " + promoCode); 
            ps.setBigDecimal(3, discPct);
            ps.setBoolean(4, isActive);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PromoDAO.insert] " + e.getMessage());
            return false;
        }
    }

    public boolean update(String promoCode, BigDecimal discPct, boolean isActive) {
        String sql = "UPDATE PROMO SET Discount_Pct = ?, Is_Active = ? WHERE Promo_Code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setBigDecimal(1, discPct);
            ps.setBoolean(2, isActive);
            ps.setString(3, promoCode);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PromoDAO.update] " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String promoCode) {
        String sql = "DELETE FROM PROMO WHERE Promo_Code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, promoCode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PromoDAO.delete] " + e.getMessage());
            return false;
        }
    }

    public BigDecimal getRawDiscountValue(String promoCode) {
        String sql = "SELECT Discount_Pct FROM PROMO WHERE Promo_Code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, promoCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal("Discount_Pct");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public List<Object[]> search(String keyword) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT Promo_Code, Discount_Pct, Is_Active FROM PROMO WHERE Promo_Code LIKE ? ORDER BY Promo_Code";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BigDecimal rawDiscount = rs.getBigDecimal("Discount_Pct");
                    String displayPercent = String.format("%.0f%%", rawDiscount.multiply(new BigDecimal(100)));
                    
                    list.add(new Object[]{
                        rs.getString("Promo_Code"),
                        displayPercent,
                        rs.getBoolean("Is_Active") ? "Aktif" : "Tidak Aktif"
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("[PromoDAO.search] " + e.getMessage());
        }
        return list;
    }
}