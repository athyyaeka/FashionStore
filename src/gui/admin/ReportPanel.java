



package gui.admin;

import database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

public class ReportPanel extends JPanel {
    private JLabel lblTotalOmset, lblTotalOrder;
    private JButton btnRefresh;

    public ReportPanel() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Ringkasan Laporan Penjualan Toko", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        
        btnRefresh = new JButton("Refresh Data");
        btnRefresh.setFocusPainted(false);
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(btnRefresh, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);


        JPanel cardContainer = new JPanel(new GridLayout(1, 2, 20, 20));


        JPanel cardOmset = new JPanel(new BorderLayout());
        cardOmset.setBackground(new Color(46, 139, 87)); 
        cardOmset.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblOmsetTitle = new JLabel("TOTAL OMSET PENJUALAN", SwingConstants.CENTER);
        lblOmsetTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblOmsetTitle.setForeground(Color.WHITE);
        
        lblTotalOmset = new JLabel("Rp 0", SwingConstants.CENTER);
        lblTotalOmset.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTotalOmset.setForeground(Color.WHITE);
        
        cardOmset.add(lblOmsetTitle, BorderLayout.NORTH);
        cardOmset.add(lblTotalOmset, BorderLayout.CENTER);


        JPanel cardOrder = new JPanel(new BorderLayout());
        cardOrder.setBackground(new Color(70, 130, 180)); 
        cardOrder.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblOrderTitle = new JLabel("TOTAL BARANG TERJUAL", SwingConstants.CENTER);
        lblOrderTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblOrderTitle.setForeground(Color.WHITE);
        
        lblTotalOrder = new JLabel("0 Pcs", SwingConstants.CENTER);
        lblTotalOrder.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTotalOrder.setForeground(Color.WHITE);
        
        cardOrder.add(lblOrderTitle, BorderLayout.NORTH);
        cardOrder.add(lblTotalOrder, BorderLayout.CENTER);

        cardContainer.add(cardOmset);
        cardContainer.add(cardOrder);
        add(cardContainer, BorderLayout.CENTER);


        btnRefresh.addActionListener(e -> loadLiveDashboardStats());


        loadLiveDashboardStats();
    }

    
    private void loadLiveDashboardStats() {

        String sqlOmset = "SELECT SUM(Total_Price) AS TotalOmset FROM [ORDER] WHERE Status NOT IN ('Menunggu Pembayaran', 'Dibatalkan')";
        String sqlTerjual = "SELECT SUM(Quantity) AS TotalBarang FROM ORDER_ITEM OI " +
                            "JOIN [ORDER] O ON OI.Order_ID = O.Order_ID " +
                            "WHERE O.Status NOT IN ('Menunggu Pembayaran', 'Dibatalkan')";
        
        try (Connection conn = DBConnection.getConnection()) {
            

            try (PreparedStatement ps = conn.prepareStatement(sqlOmset);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getBigDecimal("TotalOmset") != null) {

                    NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                    String formattedPrice = nf.format(rs.getBigDecimal("TotalOmset"));
                    

                    if (formattedPrice.endsWith(",00")) {
                        formattedPrice = formattedPrice.substring(0, formattedPrice.length() - 3);
                    }
                    lblTotalOmset.setText(formattedPrice);
                } else {
                    lblTotalOmset.setText("Rp 0");
                }
            }


            try (PreparedStatement ps2 = conn.prepareStatement(sqlTerjual);
                 ResultSet rs2 = ps2.executeQuery()) {
                if (rs2.next() && rs2.getObject("TotalBarang") != null) {
                    lblTotalOrder.setText(rs2.getInt("TotalBarang") + " Pcs");
                } else {
                    lblTotalOrder.setText("0 Pcs");
                }
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui dashboard laporan:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("[ReportPanel.loadLiveDashboardStats] " + e.getMessage());
        }
    }
}