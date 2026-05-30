package gui;

import dao.CustomerDAO;
import dao.SellerDAO;
import gui.admin.AdminDashboard;
import gui.customer.CustomerDashboard;
import model.Customer;
import model.Seller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class LoginFrame extends JFrame {


    private JTabbedPane tabbedPane;


    private JTextField txtCustEmail;
    private JPasswordField txtCustPass;
    private JButton btnCustLogin;
    private JButton btnRegister;


    private JTextField txtAdminUser;
    private JPasswordField txtAdminPass;
    private JButton btnAdminLogin;


    private final CustomerDAO customerDAO = new CustomerDAO();
    private final SellerDAO   sellerDAO   = new SellerDAO();


    private static final Color COLOR_PRIMARY   = new Color(41, 128, 185);   
    private static final Color COLOR_SECONDARY = new Color(52, 73, 94);     
    private static final Color COLOR_BG        = new Color(236, 240, 241);  
    private static final Color COLOR_WHITE     = Color.WHITE;


    private static final String PLACEHOLDER_EMAIL = "contoh@email.com";
    private static final String PLACEHOLDER_ADMIN = "username seller";

    public LoginFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("FashionStore — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 420);
        setLocationRelativeTo(null);   
        setResizable(false);
        getContentPane().setBackground(COLOR_BG);


        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(COLOR_BG);


        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);


        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabbedPane.addTab("👤  Customer", buildCustomerTab());
        tabbedPane.addTab("🔧  Admin / Seller", buildAdminTab());
        tabbedPane.setBackground(COLOR_BG);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);


        JLabel footer = new JLabel("© 2026 FashionStore — Tugas Akhir BD", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        footer.setForeground(Color.GRAY);
        footer.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        mainPanel.add(footer, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel title = new JLabel("🛍️ FashionStore");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(COLOR_WHITE);

        JLabel sub = new JLabel("Platform E-Commerce Fashion & Aksesoris");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(200, 230, 255));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(sub);

        panel.add(textPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildCustomerTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = makeGBC();


        gbc.gridy = 0;
        panel.add(makeLabel("Email:"), gbc);
        gbc.gridy = 1;
        txtCustEmail = makeTextField(PLACEHOLDER_EMAIL);
        panel.add(txtCustEmail, gbc);


        gbc.gridy = 2;
        panel.add(makeLabel("Password:"), gbc);
        gbc.gridy = 3;
        txtCustPass = new JPasswordField();
        styleTextField(txtCustPass);
        panel.add(txtCustPass, gbc);


        gbc.gridy = 4;
        gbc.insets = new Insets(15, 0, 5, 0);
        btnCustLogin = makePrimaryButton("Masuk sebagai Customer");
        panel.add(btnCustLogin, gbc);


        gbc.gridy = 5;
        gbc.insets = new Insets(5, 0, 0, 0);
        btnRegister = makeSecondaryButton("Belum punya akun? Daftar di sini");
        panel.add(btnRegister, gbc);


        btnCustLogin.addActionListener(e -> loginCustomer());
        btnRegister.addActionListener(e -> openRegister());


        txtCustPass.addActionListener(e -> loginCustomer());

        return panel;
    }

    private JPanel buildAdminTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = makeGBC();

        gbc.gridy = 0;
        panel.add(makeLabel("Username:"), gbc);
        gbc.gridy = 1;
        txtAdminUser = makeTextField(PLACEHOLDER_ADMIN);
        panel.add(txtAdminUser, gbc);

        gbc.gridy = 2;
        panel.add(makeLabel("Password:"), gbc);
        gbc.gridy = 3;
        txtAdminPass = new JPasswordField();
        styleTextField(txtAdminPass);
        panel.add(txtAdminPass, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(15, 0, 0, 0);
        btnAdminLogin = makePrimaryButton("Masuk sebagai Admin / Seller");
        btnAdminLogin.setBackground(COLOR_SECONDARY);
        panel.add(btnAdminLogin, gbc);

        btnAdminLogin.addActionListener(e -> loginAdmin());
        txtAdminPass.addActionListener(e -> loginAdmin());

        return panel;
    }

    private void loginCustomer() {
        String email = txtCustEmail.getText().trim();
        String pass  = new String(txtCustPass.getPassword()).trim();


        if (email.isEmpty() || email.equals(PLACEHOLDER_EMAIL) || pass.isEmpty()) {
            showWarning("Email dan password tidak boleh kosong.");
            return;
        }

        Customer c = customerDAO.login(email, pass);
        if (c != null) {
            this.dispose();
            new CustomerDashboard(c.getCustomerId()).setVisible(true);
        } else {
            showError("Email atau password salah.");
            txtCustPass.setText("");
        }
    }

    private void loginAdmin() {
        String username = txtAdminUser.getText().trim();
        String pass     = new String(txtAdminPass.getPassword()).trim();


        if (username.isEmpty() || username.equals(PLACEHOLDER_ADMIN) || pass.isEmpty()) {
            showWarning("Username dan password tidak boleh kosong.");
            return;
        }

        Seller s = sellerDAO.login(username, pass);
        if (s != null) {
            this.dispose();

            new AdminDashboard().setVisible(true);
        } else {
            showError("Username atau password salah.");
            txtAdminPass.setText("");
        }
    }

    private void openRegister() {

        new RegisterFrame(this).setVisible(true);
    }



    private GridBagConstraints makeGBC() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill  = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets  = new Insets(4, 0, 4, 0);
        return gbc;
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(COLOR_SECONDARY);
        return lbl;
    }

    private JTextField makeTextField(String placeholder) {
        JTextField tf = new JTextField();
        styleTextField(tf);
        tf.setForeground(Color.GRAY);
        tf.setText(placeholder);
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) {
                    tf.setText("");
                    tf.setForeground(Color.BLACK);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) {
                    tf.setText(placeholder);
                    tf.setForeground(Color.GRAY);
                }
            }
        });
        return tf;
    }

    private void styleTextField(JComponent tf) {
        tf.setPreferredSize(new Dimension(0, 35));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
    }

    private JButton makePrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(COLOR_PRIMARY);
        btn.setForeground(COLOR_WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(0, 38));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(btn.getBackground().darker());
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(btn.getBackground().brighter());
            }
        });
        return btn;
    }

    private JButton makeSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(COLOR_BG);
        btn.setForeground(COLOR_PRIMARY);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void showWarning(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Login Gagal", JOptionPane.ERROR_MESSAGE);
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {

        }
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}