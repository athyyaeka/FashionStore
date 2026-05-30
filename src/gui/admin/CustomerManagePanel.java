



package gui.admin;

import dao.CustomerDAO;
import model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class CustomerManagePanel extends JPanel {

    private final CustomerDAO customerDAO = new CustomerDAO();

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtId, txtNama, txtEmail, txtPassword, txtSaldo;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnRefresh;
    private JTextField txtSearch;

    public CustomerManagePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        buildUI();
        loadData();
    }

    private void buildUI() {
        JLabel title = new JLabel("Manajemen Data Customer / Member");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);


        String[] cols = {"ID Customer", "Nama Lengkap", "Email", "Password", "Saldo (Rp)", "Tgl Registrasi"};
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
        formPanel.setBorder(BorderFactory.createTitledBorder("Form Customer"));
        formPanel.setPreferredSize(new Dimension(280, 0));
        GridBagConstraints gbc = formGbc();

        txtId       = addRow(formPanel, gbc, "ID Customer:", 0); txtId.setEditable(false);
        txtNama     = addRow(formPanel, gbc, "Nama Lengkap:", 1);
        txtEmail    = addRow(formPanel, gbc, "Email:", 2);
        txtPassword = addRow(formPanel, gbc, "Password:", 3);
        txtSaldo    = addRow(formPanel, gbc, "Saldo E-Wallet:", 4);


        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 4, 4, 4);
        JPanel btnF = new JPanel(new GridLayout(2, 2, 6, 6));
        btnAdd    = btn("Tambah",          new Color(39, 174, 96));
        btnUpdate = btn("Update",          new Color(41, 128, 185));
        btnDelete = btn("Hapus / Blokir",   new Color(192, 57, 43));
        btnClear  = btn("Bersihkan Form",  null);
        btnF.add(btnAdd); btnF.add(btnUpdate);
        btnF.add(btnDelete); btnF.add(btnClear);
        formPanel.add(btnF, gbc);


        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        txtSearch = new JTextField(18);
        JButton btnSearch = new JButton("Cari");
        btnRefresh = new JButton("Refresh");
        searchPanel.add(new JLabel("Cari:")); searchPanel.add(txtSearch);
        searchPanel.add(btnSearch); searchPanel.add(btnRefresh);

        JPanel center = new JPanel(new BorderLayout(5, 5));
        center.add(searchPanel, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
        add(formPanel, BorderLayout.EAST);


        btnAdd.addActionListener(e    -> insertCustomer());
        btnUpdate.addActionListener(e -> updateCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnClear.addActionListener(e  -> clearForm());
        btnRefresh.addActionListener(e-> loadData());
        btnSearch.addActionListener(e -> {
            model.setRowCount(0);
            customerDAO.search(txtSearch.getText().trim())
                .forEach(c -> model.addRow(toRow(c)));
        });
        txtSearch.addActionListener(e -> btnSearch.doClick());
    }

    private void loadData() {
        model.setRowCount(0);
        customerDAO.getAll().forEach(c -> model.addRow(toRow(c)));
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        txtId.setText((String) model.getValueAt(row, 0));
        txtNama.setText((String) model.getValueAt(row, 1));
        txtEmail.setText((String) model.getValueAt(row, 2));
        txtPassword.setText((String) model.getValueAt(row, 3));
        
        Customer c = customerDAO.getById(txtId.getText());
        if (c == null) return;
        txtSaldo.setText(c.getBalance().toPlainString());
    }

    private void insertCustomer() {
        Customer c = buildFromForm(true);
        if (c == null) return;
        if (customerDAO.register(c)) {
            JOptionPane.showMessageDialog(this, "Customer berhasil ditambahkan!", "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
            loadData(); clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambah customer. ID atau Email mungkin sudah ada.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCustomer() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih customer dari tabel untuk diupdate.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Customer c = buildFromForm(false);
        if (c == null) return;
        if (customerDAO.update(c)) {
            JOptionPane.showMessageDialog(this, "Data customer berhasil diupdate!", "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal update data customer.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCustomer() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih customer dari tabel untuk dihapus.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus / Suspend akun customer " + id + "?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (customerDAO.delete(id)) {
                JOptionPane.showMessageDialog(this, "Akun customer berhasil dihapus!", "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);
                loadData(); clearForm();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Gagal menghapus. Customer mungkin memiliki riwayat transaksi di tabel [ORDER].",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Customer buildFromForm(boolean isInsert) {
        try {
            Customer c = new Customer();
            if (isInsert) c.setCustomerId(customerDAO.generateId());
            else          c.setCustomerId(txtId.getText().trim());
            c.setFullName(txtNama.getText().trim());
            c.setEmail(txtEmail.getText().trim());
            c.setPassword(txtPassword.getText().trim());
            c.setBalance(new BigDecimal(txtSaldo.getText().trim().replace(",", "")));

            if (c.getFullName().isEmpty() || c.getEmail().isEmpty()) {
                throw new IllegalArgumentException("Nama lengkap dan Email wajib diisi.");
            }
            return c;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Saldo harus berupa angka desimal/nominal murni.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return null;
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private void clearForm() {
        txtId.setText(""); txtNama.setText(""); txtEmail.setText("");
        txtPassword.setText(""); txtSaldo.setText("");
        table.clearSelection();
    }

    private Object[] toRow(Customer c) {
        return new Object[]{
            c.getCustomerId(), c.getFullName(), c.getEmail(), c.getPassword(),
            String.format("%,.0f", c.getBalance()), 
            c.getRegDate() != null ? c.getRegDate().toString() : "-"
        };
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