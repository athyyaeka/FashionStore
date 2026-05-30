package dao;

import database.DBConnection;
import model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql =
            "SELECT P.*, CAT.CName AS CategoryName, S.StoreName, "
          + "CASE WHEN CL.Product_ID IS NOT NULL THEN 'Pakaian' "
          + "     WHEN AC.Product_ID IS NOT NULL THEN 'Aksesoris' "
          + "     ELSE '-' END AS JenisProduk "
          + "FROM PRODUCT P "
          + "JOIN CATEGORY CAT ON P.Category_ID = CAT.Category_ID "
          + "JOIN SELLER S     ON P.seller_Id   = S.seller_Id " 
          + "LEFT JOIN CLOTHING CL  ON P.Product_ID = CL.Product_ID "
          + "LEFT JOIN ACCESSORY AC ON P.Product_ID = AC.Product_ID "
          + "ORDER BY P.PName";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[ProductDAO.getAll] " + e.getMessage());
        }
        return list;
    }

    public List<Product> getBySeller(String sellerId) {
        List<Product> list = new ArrayList<>();
        String sql =
            "SELECT P.*, CAT.CName AS CategoryName, S.StoreName, "
          + "CASE WHEN CL.Product_ID IS NOT NULL THEN 'Pakaian' "
          + "     WHEN AC.Product_ID IS NOT NULL THEN 'Aksesoris' "
          + "     ELSE '-' END AS JenisProduk "
          + "FROM PRODUCT P "
          + "JOIN CATEGORY CAT ON P.Category_ID = CAT.Category_ID "
          + "JOIN SELLER S     ON P.seller_Id   = S.seller_Id "  
          + "LEFT JOIN CLOTHING CL  ON P.Product_ID = CL.Product_ID "
          + "LEFT JOIN ACCESSORY AC ON P.Product_ID = AC.Product_ID "
          + "WHERE P.seller_Id = ? ORDER BY P.PName";           
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ProductDAO.getBySeller] " + e.getMessage());
        }
        return list;
    }

    public Product getById(String productId) {
        String sql =
            "SELECT P.*, CAT.CName AS CategoryName, S.StoreName, "
          + "CASE WHEN CL.Product_ID IS NOT NULL THEN 'Pakaian' "
          + "     WHEN AC.Product_ID IS NOT NULL THEN 'Aksesoris' "
          + "     ELSE '-' END AS JenisProduk "
          + "FROM PRODUCT P "
          + "JOIN CATEGORY CAT ON P.Category_ID = CAT.Category_ID "
          + "JOIN SELLER S     ON P.seller_Id   = S.seller_Id "
          + "LEFT JOIN CLOTHING CL  ON P.Product_ID = CL.Product_ID "
          + "LEFT JOIN ACCESSORY AC ON P.Product_ID = AC.Product_ID "
          + "WHERE P.Product_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[ProductDAO.getById] " + e.getMessage());
        }
        return null;
    }
    public List<Product> search(String keyword, String categoryId,
                                Double minPrice, Double maxPrice) {
        List<Product> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder(
            "SELECT P.*, CAT.CName AS CategoryName, S.StoreName, "
          + "CASE WHEN CL.Product_ID IS NOT NULL THEN 'Pakaian' "
          + "     WHEN AC.Product_ID IS NOT NULL THEN 'Aksesoris' "
          + "     ELSE '-' END AS JenisProduk "
          + "FROM PRODUCT P "
          + "JOIN CATEGORY CAT ON P.Category_ID = CAT.Category_ID "
          + "JOIN SELLER S     ON P.seller_Id   = S.seller_Id "
          + "LEFT JOIN CLOTHING CL  ON P.Product_ID = CL.Product_ID "
          + "LEFT JOIN ACCESSORY AC ON P.Product_ID = AC.Product_ID "
          + "WHERE 1=1 "
        );
        if (keyword != null && !keyword.isEmpty())
            sb.append("AND P.PName LIKE ? ");
        if (categoryId != null && !categoryId.isEmpty())
            sb.append("AND P.Category_ID = ? ");
        if (minPrice != null)
            sb.append("AND P.Price >= ? ");
        if (maxPrice != null)
            sb.append("AND P.Price <= ? ");
        sb.append("ORDER BY P.PName");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            int idx = 1;
            if (keyword != null && !keyword.isEmpty())
                ps.setString(idx++, "%" + keyword + "%");
            if (categoryId != null && !categoryId.isEmpty())
                ps.setString(idx++, categoryId);
            if (minPrice != null)
                ps.setDouble(idx++, minPrice);
            if (maxPrice != null)
                ps.setDouble(idx++, maxPrice);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ProductDAO.search] " + e.getMessage());
        }
        return list;
    }
    public boolean insert(Product p) {
        String sql = "INSERT INTO PRODUCT "
                   + "(Product_ID, PName, Price, Stock, Weight_gram, Description, Category_ID, seller_Id) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getProductId());
            ps.setString(2, p.getPName());
            ps.setBigDecimal(3, p.getPrice());
            ps.setInt(4, p.getStock());
            ps.setInt(5, p.getWeightGram());
            ps.setString(6, p.getDescription());
            ps.setString(7, p.getCategoryId());
            ps.setString(8, p.getSellerId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ProductDAO.insert] " + e.getMessage());
            return false;
        }
    }

    public boolean insertClothing(String productId, String size,
                                  String material, String genderCategory) {
        String sql = "INSERT INTO CLOTHING (Product_ID, Size, Material, Gender_Category) "
                   + "VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productId);
            ps.setString(2, size);
            ps.setString(3, material);
            ps.setString(4, genderCategory);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ProductDAO.insertClothing] " + e.getMessage());
            return false;
        }
    }

    public boolean insertAccessory(String productId, String type, String material) {
        String sql = "INSERT INTO ACCESSORY (Product_ID, Type, Material) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productId);
            ps.setString(2, type);
            ps.setString(3, material);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ProductDAO.insertAccessory] " + e.getMessage());
            return false;
        }
    }
    public boolean update(Product p) {
        String sql = "UPDATE PRODUCT SET PName=?, Price=?, Stock=?, Weight_gram=?, "
                   + "Description=?, Category_ID=? WHERE Product_ID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getPName());
            ps.setBigDecimal(2, p.getPrice());
            ps.setInt(3, p.getStock());
            ps.setInt(4, p.getWeightGram());
            ps.setString(5, p.getDescription());
            ps.setString(6, p.getCategoryId());
            ps.setString(7, p.getProductId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ProductDAO.update] " + e.getMessage());
            return false;
        }
    }
    public boolean delete(String productId) {
        try {
            deleteClothing(productId);
            deleteAccessory(productId);
            String sql = "DELETE FROM PRODUCT WHERE Product_ID = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, productId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[ProductDAO.delete] " + e.getMessage());
            return false;
        }
    }

    private void deleteClothing(String productId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "DELETE FROM CLOTHING WHERE Product_ID = ?")) {
            ps.setString(1, productId);
            ps.executeUpdate();
        }
    }

    private void deleteAccessory(String productId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "DELETE FROM ACCESSORY WHERE Product_ID = ?")) {
            ps.setString(1, productId);
            ps.executeUpdate();
        }
    }
    public String generateId() {
        String sql = "SELECT MAX(Product_ID) AS MaxID FROM PRODUCT";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next() && rs.getString("MaxID") != null) {
                int num = Integer.parseInt(rs.getString("MaxID").substring(1)) + 1;
                return String.format("P%03d", num);
            }
        } catch (SQLException e) {
            System.err.println("[ProductDAO.generateId] " + e.getMessage());
        }
        return "P001";
    }
    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getString("Product_ID"));
        p.setPName(rs.getString("PName"));
        p.setPrice(rs.getBigDecimal("Price"));
        p.setStock(rs.getInt("Stock"));
        p.setWeightGram(rs.getInt("Weight_gram"));
        p.setDescription(rs.getString("Description"));
        p.setCategoryId(rs.getString("Category_ID"));
        p.setSellerId(rs.getString("seller_Id"));   
        p.setCategoryName(rs.getString("CategoryName"));
        p.setStoreName(rs.getString("StoreName"));
        p.setJenisProduk(rs.getString("JenisProduk"));
        return p;
    }
}