package gui.customer;

import dao.CustomerDAO;
import model.Customer;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;


public class ProfilePanel extends JPanel {

    private final String customerId;
    private final CustomerDAO customerDAO = new CustomerDAO();

    private JTextField txtNama, txtEmail;
    private JPasswordField txtPass;
    private JLabel lblSaldo, lblId, lblPhone;
    private JButton btnSave, btnTopUp;

    public ProfilePanel(String customerId) {
        this.customerId = customerId;
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        buildUI();
        loadProfileData();
    }

    private void buildUI() {
        JLabel lblTitle = new JLabel("Profil Saya");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(lblTitle, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridx = 0; gbc.weightx = 0;


        gbc.gridy = 0; form.add(bold("Customer ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        lblId = new JLabel("-");
        form.add(lblId, gbc);


        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(bold("Nama Lengkap:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtNama = new JTextField();
        form.add(txtNama, gbc);


        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        form.add(bold("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtEmail = new JTextField();
        form.add(txtEmail, gbc);


        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        form.add(bold("Password Baru:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtPass = new JPasswordField();
        txtPass.setToolTipText("Kosongkan jika tidak ingin ubah password");
        form.add(txtPass, gbc);


        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        form.add(bold("Saldo:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        lblSaldo = new JLabel("-");
        lblSaldo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSaldo.setForeground(new Color(39, 174, 96));
        form.add(lblSaldo, gbc);


        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        form.add(bold("No. Telepon:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        lblPhone = new JLabel("-");
        form.add(lblPhone, gbc);


        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 6, 6, 6);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnSave  = new JButton("Simpan Perubahan");
        btnTopUp = new JButton("Top Up Saldo");
        btnSave.setBackground(new Color(41, 128, 185));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnTopUp.setBackground(new Color(39, 174, 96));
        btnTopUp.setForeground(Color.WHITE);
        btnTopUp.setFocusPainted(false);
        btnPanel.add(btnSave);
        btnPanel.add(btnTopUp);
        form.add(btnPanel, gbc);

        add(form, BorderLayout.CENTER);

        btnSave.addActionListener(e -> saveProfile());
        btnTopUp.addActionListener(e -> topUpBalance());
    }

    public void loadProfileData() {
        Customer c = customerDAO.getById(customerId);
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Data customer tidak ditemukan.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        lblId.setText(c.getCustomerId());
        txtNama.setText(c.getFullName());
        txtEmail.setText(c.getEmail());
        txtPass.setText("");
        lblSaldo.setText("Rp " + String.format("%,.2f", c.getBalance()));


        List<String> phones = customerDAO.getPhones(customerId);
        lblPhone.setText(phones.isEmpty() ? "Belum ada" : String.join(", ", phones));
    }

    private void saveProfile() {
        String nama  = txtNama.getText().trim();
        String email = txtEmail.getText().trim();
        String pass  = new String(txtPass.getPassword()).trim();

        if (nama.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama dan email tidak boleh kosong.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Format email tidak valid.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }


        if (pass.isEmpty()) {
            Customer existing = customerDAO.getById(customerId);
            if (existing != null) pass = existing.getPassword();
        } else if (pass.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password minimal 6 karakter.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Customer c = new Customer();
        c.setCustomerId(customerId);
        c.setFullName(nama);
        c.setEmail(email);
        c.setPassword(pass);

        if (customerDAO.update(c)) {
            JOptionPane.showMessageDialog(this, "Profil berhasil diperbarui!",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadProfileData();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui profil.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void topUpBalance() {
        String amountStr = JOptionPane.showInputDialog(this,
                "Masukkan jumlah top-up (Rp):", "Top Up Saldo",
                JOptionPane.QUESTION_MESSAGE);
        if (amountStr == null) return;
        try {
            BigDecimal amount = new BigDecimal(amountStr.trim().replace(",", "").replace(".", ""));
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah top-up harus lebih dari 0.",
                        "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            BigDecimal newBalance = customerDAO.topUpBalance(customerId, amount);
            if (newBalance != null) {
                JOptionPane.showMessageDialog(this,
                        "Top-up berhasil!\nSaldo baru: Rp " + String.format("%,.2f", newBalance),
                        "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                loadProfileData();
            } else {
                JOptionPane.showMessageDialog(this, "Top-up gagal. Coba lagi.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Masukkan angka yang valid.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel bold(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return l;
    }
}