



package gui.admin;

import database.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;

public class PromoManagePanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtKode, txtDiskon;
    private JComboBox<String> cmbStatus;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnRefresh;
    private JTextField txtSearch;

    public PromoManagePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        buildUI();
        loadData();
    }

    private void buildUI() {
        JLabel title = new JLabel("Pengaturan Voucher & Promo");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);


        String[] cols = {"Kode Voucher", "Potongan Diskon", "Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromTable();
        });


        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Form Voucher"));
        formPanel.setPreferredSize(new Dimension(280, 0));
        GridBagConstraints gbc = formGbc();

        txtKode   = addRow(formPanel, gbc, "Kode Voucher:", 0);
        txtDiskon = addRow(formPanel, gbc, "Diskon (0.00 - 1.00):", 1);

        gbc.gridy = 2; gbc.gridx = 0; gbc.weightx = 0;
        formPanel.add(lbl("Status Aktif:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbStatus = new JComboBox<>(new String[]{"Aktif", "Tidak Aktif"});
        formPanel.add(cmbStatus, gbc);


        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 4, 4, 4);
        JPanel btnF = new JPanel(new GridLayout(2, 2, 6, 6));
        btnAdd    = btn("Rilis / Tambah", new Color(39, 174, 96));
        btnUpdate = btn("Update Status",  new Color(41, 128, 185));
        btnDelete = btn("Hapus Permanen", new Color(192, 57, 43));
        btnClear  = btn("Bersihkan Form", null);
        btnF.add(btnAdd); btnF.add(btnUpdate);
        btnF.add(btnDelete); btnF.add(btnClear);
        formPanel.add(btnF, gbc);


        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        txtSearch = new JTextField(18);
        JButton btnSearch = new JButton("Cari");
        btnRefresh = new JButton("Refresh");
        searchPanel.add(new JLabel("Cari Kode:")); searchPanel.add(txtSearch);
        searchPanel.add(btnSearch); searchPanel.add(btnRefresh);

        JPanel center = new JPanel(new BorderLayout(5, 5));
        center.add(searchPanel, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
        add(formPanel, BorderLayout.EAST);


        btnAdd.addActionListener(e    -> insertPromo());
        btnUpdate.addActionListener(e -> updatePromo());
        btnDelete.addActionListener(e -> deletePromo());
        btnClear.addActionListener(e  -> clearForm());
        btnRefresh.addActionListener(e-> loadData());
        btnSearch.addActionListener(e -> searchPromo());
        txtSearch.addActionListener(e -> btnSearch.doClick());
    }

    private void loadData() {
        model.setRowCount(0);

        String sql = "SELECT Promo_Code, Discount_Pct, Is_Active FROM PROMO ORDER BY Promo_Code";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                BigDecimal pct = rs.getBigDecimal("Discount_Pct");
                BigDecimal displayPct = (pct != null) ? pct.multiply(new BigDecimal(100)) : BigDecimal.ZERO;

                model.addRow(new Object[]{
                        rs.getString("Promo_Code"),
                        String.format("%.0f%%", displayPct),
                        rs.getBoolean("Is_Active") ? "Aktif" : "Tidak Aktif"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil data promo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        String code = (String) model.getValueAt(row, 0);
        txtKode.setText(code);
        txtKode.setEditable(false); 


        String sql = "SELECT Discount_Pct, Is_Active FROM PROMO WHERE Promo_Code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    txtDiskon.setText(rs.getBigDecimal("Discount_Pct").toPlainString());
                    cmbStatus.setSelectedItem(rs.getBoolean("Is_Active") ? "Aktif" : "Tidak Aktif");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertPromo() {
        String code = txtKode.getText().trim().toUpperCase();
        String discountStr = txtDiskon.getText().trim();

        if (code.isEmpty() || discountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua form input wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            BigDecimal discount = new BigDecimal(discountStr);
            if (discount.compareTo(BigDecimal.ZERO) < 0 || discount.compareTo(BigDecimal.ONE) > 0) {
                throw new IllegalArgumentException("Nilai diskon berkisar antara 0.00 sampai 1.00 (Contoh: 0.10 untuk 10%)");
            }

            boolean isActive = cmbStatus.getSelectedItem().equals("Aktif");


            String sql = "INSERT INTO PROMO (Promo_Code, Description, Discount_Pct, Min_Purchase, Valid_From, Valid_Until, Is_Active) "
                       + "VALUES (?, ?, ?, 0, GETDATE(), DATEADD(year, 1, GETDATE()), ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, code);
                ps.setString(2, "Voucher Promo " + code); 
                ps.setBigDecimal(3, discount);
                ps.setBoolean(4, isActive);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Voucher baru berhasil dirilis!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                clearForm();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Diskon harus berupa format angka desimal (cth: 0.15)", "Validasi", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validasi", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menambah voucher. " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePromo() {
        String code = txtKode.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih baris voucher dari tabel untuk diupdate.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            BigDecimal discount = new BigDecimal(txtDiskon.getText().trim());
            boolean isActive = cmbStatus.getSelectedItem().equals("Aktif");


            String sql = "UPDATE PROMO SET Discount_Pct = ?, Is_Active = ? WHERE Promo_Code = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setBigDecimal(1, discount);
                ps.setBoolean(2, isActive);
                ps.setString(3, code);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data Voucher berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePromo() {
        String code = txtKode.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih voucher dari tabel untuk dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Hapus permanen kode voucher " + code + "?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM PROMO WHERE Promo_Code = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, code);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Voucher berhasil dihapus dari sistem!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                clearForm();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus. Kode voucher mungkin sudah pernah digunakan dalam transaksi pembelian.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchPromo() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
            return;
        }

        model.setRowCount(0);

        String sql = "SELECT Promo_Code, Discount_Pct, Is_Active FROM PROMO WHERE Promo_Code LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BigDecimal pct = rs.getBigDecimal("Discount_Pct");
                    BigDecimal displayPct = (pct != null) ? pct.multiply(new BigDecimal(100)) : BigDecimal.ZERO;

                    model.addRow(new Object[]{
                            rs.getString("Promo_Code"),
                            String.format("%.0f%%", displayPct),
                            rs.getBoolean("Is_Active") ? "Aktif" : "Tidak Aktif"
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearForm() {
        txtKode.setText("");
        txtKode.setEditable(true);
        txtDiskon.setText("");
        cmbStatus.setSelectedIndex(0);
        table.clearSelection();
    }


    private GridBagConstraints formGbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(4, 4, 4, 4);
        g.weightx = 1.0;
        return g;
    }

    private JTextField addRow(JPanel p, GridBagConstraints gbc, String label, int row) {
        gbc.gridy = row; gbc.gridx = 0; gbc.weightx = 0;
        p.add(lbl(label), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField tf = new JTextField();
        p.add(tf, gbc);
        return tf;
    }

    private JLabel lbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        return l;
    }

    private JButton btn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        if (bg != null) { b.setBackground(bg); b.setForeground(Color.WHITE); }
        return b;
    }
}