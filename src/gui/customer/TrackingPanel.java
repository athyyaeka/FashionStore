



package gui.customer;

import dao.ShipmentDAO;

import javax.swing.*;
import java.awt.*;

public class TrackingPanel extends JPanel {

    private final CustomerDashboard dashboard;
    private final ShipmentDAO shipmentDAO = new ShipmentDAO();

    private JTextField txtOrderId;
    private JLabel lblResi, lblKurir, lblStatus, lblAlamat, lblTanggal;
    private JButton btnCari;

    public TrackingPanel(CustomerDashboard dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        buildUI();
    }

    private void buildUI() {
        JLabel lblTitle = new JLabel("Lacak Pengiriman Pesanan");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(lblTitle, BorderLayout.NORTH);


        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchPanel.add(new JLabel("ID Order:"));
        txtOrderId = new JTextField(12);
        txtOrderId.setToolTipText("Contoh: ORD-101");
        btnCari = new JButton("Lacak");
        btnCari.setBackground(new Color(41, 128, 185));
        btnCari.setForeground(Color.WHITE);
        btnCari.setFocusPainted(false);
        searchPanel.add(txtOrderId);
        searchPanel.add(btnCari);


        JPanel infoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Status Pengiriman"));
        infoPanel.add(bold("No. Resi:")); lblResi = new JLabel("-"); infoPanel.add(lblResi);
        infoPanel.add(bold("Kurir/Ekspedisi:")); lblKurir = new JLabel("-"); infoPanel.add(lblKurir);
        infoPanel.add(bold("Tanggal Kirim:")); lblTanggal = new JLabel("-"); infoPanel.add(lblTanggal);
        infoPanel.add(bold("Status Saat Ini:")); lblStatus = new JLabel("-"); infoPanel.add(lblStatus);
        infoPanel.add(bold("Alamat Tujuan:")); lblAlamat = new JLabel("-"); infoPanel.add(lblAlamat);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(infoPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        btnCari.addActionListener(e -> cariShipment());
        txtOrderId.addActionListener(e -> cariShipment());
    }

    private void cariShipment() {
        String orderId = txtOrderId.getText().trim().toUpperCase();
        if (orderId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan ID Order terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Object[] data = shipmentDAO.getByOrderId(orderId);
        if (data == null) {
            lblResi.setText("-"); lblKurir.setText("-");
            lblTanggal.setText("-"); lblAlamat.setText("-");
            lblStatus.setText("Belum Ada Data Pengiriman");
            lblStatus.setForeground(Color.GRAY);
            JOptionPane.showMessageDialog(this,
                    "Data pengiriman untuk " + orderId + " belum tersedia.",
                    "Tidak Ditemukan", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        lblResi.setText(data[0] != null ? data[0].toString() : "-");
        lblKurir.setText(data[1] != null ? data[1].toString() : "-");
        lblTanggal.setText(data[2] != null ? data[2].toString() : "-");
        String status = data[3] != null ? data[3].toString() : "-";
        lblStatus.setText(status);
        lblStatus.setForeground(status.equals("Delivered") ? new Color(39,174,96) : new Color(41,128,185));
        lblAlamat.setText(data[4] != null ? data[4].toString() : "-");
    }

    private JLabel bold(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return l;
    }
}