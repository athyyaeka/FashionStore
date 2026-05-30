package gui.admin;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import database.DBConnection; 

public class OrderManagePanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> cmbStatus;
    private JTextField txtResi;
    private JButton btnUpdate;

    public OrderManagePanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Manajemen Pesanan Masuk", SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        String[] columns = {"ID Transaksi", "Customer", "Total Bayar", "Status Aktual", "No. Resi"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);


        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Aksi Proses Pengiriman"));

        controlPanel.add(new JLabel("Ubah Status:"));
        String[] statusList = {"Menunggu Pembayaran", "Diproses", "Sedang Dikirim", "Selesai", "Dibatalkan"};
        cmbStatus = new JComboBox<>(statusList);
        controlPanel.add(cmbStatus);

        controlPanel.add(new JLabel("Input No. Resi:"));
        txtResi = new JTextField(15);
        controlPanel.add(txtResi);

        btnUpdate = new JButton("Update Status & Resi");
        btnUpdate.setBackground(new Color(51, 122, 183));
        btnUpdate.setForeground(Color.WHITE);
        controlPanel.add(btnUpdate);

        add(controlPanel, BorderLayout.SOUTH);




        table.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String status = model.getValueAt(selectedRow, 3).toString();
                String resi = model.getValueAt(selectedRow, 4).toString();
                
                cmbStatus.setSelectedItem(status);
                txtResi.setText("-".equals(resi) ? "" : resi);
            }
        });


        btnUpdate.addActionListener(e -> updateOrderAndShipment());


        loadOrders();
    }


    private void loadOrders() {
        model.setRowCount(0); 
        String query = "SELECT Order_ID, CustomerName, Total_Price, Status, ISNULL(TrackingNo, '-') AS TrackingNo FROM vw_OrderSummary";


        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("Order_ID"),
                        rs.getString("CustomerName"),
                        rs.getBigDecimal("Total_Price"),
                        rs.getString("Status"),
                        rs.getString("TrackingNo")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data dari database:\n" + ex.getMessage(), 
                    "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void updateOrderAndShipment() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih baris pesanan pada tabel terlebih dahulu!", 
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String orderId = model.getValueAt(selectedRow, 0).toString();
        String newStatus = cmbStatus.getSelectedItem().toString();
        String resi = txtResi.getText().trim();


        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); 


            String sqlOrder = "UPDATE [ORDER] SET Status = ? WHERE Order_ID = ?";
            try (PreparedStatement psOrder = conn.prepareStatement(sqlOrder)) {
                psOrder.setString(1, newStatus);
                psOrder.setString(2, orderId);
                psOrder.executeUpdate();
            }


            if (!resi.isEmpty()) {
                String checkShipment = "SELECT COUNT(*) FROM SHIPMENT WHERE Order_ID = ?";
                boolean exists = false;
                try (PreparedStatement psCheck = conn.prepareStatement(checkShipment)) {
                    psCheck.setString(1, orderId);
                    try (ResultSet rs = psCheck.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) exists = true;
                    }
                }

                if (exists) {

                    String sqlUpdateShip = "UPDATE SHIPMENT SET TrackingNo = ?, Ship_Status = ? WHERE Order_ID = ?";
                    try (PreparedStatement psUpdateShip = conn.prepareStatement(sqlUpdateShip)) {
                        psUpdateShip.setString(1, resi);
                        psUpdateShip.setString(2, "Sedang Dikirim".equals(newStatus) ? "On Transit" : ("Selesai".equals(newStatus) ? "Delivered" : "Menunggu Konfirmasi"));
                        psUpdateShip.setString(3, orderId);
                        psUpdateShip.executeUpdate();
                    }
                } else {

                    String sqlInsertShip = "INSERT INTO SHIPMENT (TrackingNo, Courier, Ship_Date, Ship_Status, Deliv_Address, Order_ID) VALUES (?, 'JNE', GETDATE(), ?, 'Alamat Gudang Pusat', ?)";
                    try (PreparedStatement psInsertShip = conn.prepareStatement(sqlInsertShip)) {
                        psInsertShip.setString(1, resi);
                        psInsertShip.setString(2, "Sedang Dikirim".equals(newStatus) ? "On Transit" : "Menunggu Konfirmasi");
                        psInsertShip.setString(3, orderId);
                        psInsertShip.executeUpdate();
                    }
                }
            }

            conn.commit(); 
            JOptionPane.showMessageDialog(this, "Status & Resi Pesanan " + orderId + " berhasil diperbarui!");
            loadOrders(); 

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui data:\n" + ex.getMessage(), 
                    "Error Transaksi", JOptionPane.ERROR_MESSAGE);
        }
    }
}