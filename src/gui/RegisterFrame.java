package gui;

import dao.CustomerDAO;
import model.Customer;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;


public class RegisterFrame extends JDialog {

    private JTextField txtName;
    private JTextField txtEmail;
    private JPasswordField txtPass;
    private JPasswordField txtPassConfirm;
    private JButton btnDaftar;
    private JButton btnBatal;

    private final CustomerDAO customerDAO = new CustomerDAO();

    private static final Color COLOR_PRIMARY = new Color(41, 128, 185);
    private static final Color COLOR_WHITE   = Color.WHITE;

    public RegisterFrame(JFrame parent) {
        super(parent, "Daftar Akun Baru", true);
        initUI();
    }

    private void initUI() {
        setSize(420, 370);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());


        JPanel header = new JPanel();
        header.setBackground(COLOR_PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("Buat Akun Customer Baru");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(COLOR_WHITE);
        header.add(title);


        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(COLOR_WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.insets = new Insets(5, 0, 5, 0);


        gbc.gridy = 0; form.add(makeLabel("Nama Lengkap:"), gbc);
        gbc.gridy = 1;
        txtName = new JTextField();
        styleField(txtName);
        form.add(txtName, gbc);


        gbc.gridy = 2; form.add(makeLabel("Email:"), gbc);
        gbc.gridy = 3;
        txtEmail = new JTextField();
        styleField(txtEmail);
        form.add(txtEmail, gbc);


        gbc.gridy = 4; form.add(makeLabel("Password:"), gbc);
        gbc.gridy = 5;
        txtPass = new JPasswordField();
        styleField(txtPass);
        form.add(txtPass, gbc);


        gbc.gridy = 6; form.add(makeLabel("Konfirmasi Password:"), gbc);
        gbc.gridy = 7;
        txtPassConfirm = new JPasswordField();
        styleField(txtPassConfirm);
        form.add(txtPassConfirm, gbc);


        gbc.gridy = 8;
        gbc.insets = new Insets(15, 0, 5, 0);
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setOpaque(false);

        btnDaftar = new JButton("Daftar");
        btnDaftar.setBackground(COLOR_PRIMARY);
        btnDaftar.setForeground(COLOR_WHITE);
        btnDaftar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDaftar.setFocusPainted(false);
        btnDaftar.setBorderPainted(false);
        btnDaftar.setPreferredSize(new Dimension(0, 36));

        btnBatal = new JButton("Batal");
        btnBatal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnBatal.setPreferredSize(new Dimension(0, 36));

        btnPanel.add(btnDaftar);
        btnPanel.add(btnBatal);
        form.add(btnPanel, gbc);


        btnDaftar.addActionListener(e -> daftar());
        btnBatal.addActionListener(e -> dispose());

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(form, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }

    private void daftar() {
        String nama   = txtName.getText().trim();
        String email  = txtEmail.getText().trim();
        String pass   = new String(txtPass.getPassword()).trim();
        String pass2  = new String(txtPassConfirm.getPassword()).trim();


        if (nama.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            showWarn("Semua field wajib diisi.");
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            showWarn("Format email tidak valid.");
            return;
        }
        if (pass.length() < 6) {
            showWarn("Password minimal 6 karakter.");
            return;
        }
        if (!pass.equals(pass2)) {
            showWarn("Password dan konfirmasi password tidak sama.");
            return;
        }


        Customer c = new Customer();
        c.setCustomerId(customerDAO.generateId());
        c.setFullName(nama);
        c.setEmail(email);
        c.setPassword(pass);
        c.setBalance(BigDecimal.ZERO);

        boolean ok = customerDAO.register(c);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                "Pendaftaran berhasil!\nSilakan login dengan email dan password kamu.",
                "Registrasi Berhasil", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            showWarn("Pendaftaran gagal. Email mungkin sudah terdaftar.");
        }
    }





    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(52, 73, 94));
        return lbl;
    }

    private void styleField(JComponent field) {
        field.setPreferredSize(new Dimension(0, 32));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
    }

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Perhatian", JOptionPane.WARNING_MESSAGE);
    }
}