package gui.customer;

import dao.OrderDAO;
import dao.PromoDAO;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;


public class CheckoutPanel extends JPanel {

    private final CustomerDashboard dashboard;
    private final OrderDAO  orderDAO  = new OrderDAO();
    private final PromoDAO  promoDAO  = new PromoDAO();

    private JTextArea txtAddress;
    private JComboBox<String> cmbPayment;
    private JTextField txtPromoCode;
    private JLabel lblTotal, lblFinalTotal;
    private JButton btnApplyPromo, btnPlaceOrder;


    private List<Object[]> cartItems;
    private BigDecimal cartTotal = BigDecimal.ZERO;

    public CheckoutPanel(CustomerDashboard dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        buildUI();
    }

    private void buildUI() {
        JLabel lblTitle = new JLabel("Form Checkout Pesanan");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.weightx = 1.0;


        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.weightx = 0;
        formPanel.add(boldLabel("Alamat Pengiriman:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtAddress = new JTextArea(3, 30);
        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(txtAddress), gbc);


        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(boldLabel("Metode Pembayaran:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        cmbPayment = new JComboBox<>(new String[]{"Transfer Bank", "E-Wallet (GoPay/OVO/Dana)"});
        formPanel.add(cmbPayment, gbc);


        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(boldLabel("Kode Promo:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JPanel promoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        txtPromoCode = new JTextField(10);
        btnApplyPromo = new JButton("Pakai");
        promoPanel.add(txtPromoCode);
        promoPanel.add(btnApplyPromo);
        formPanel.add(promoPanel, gbc);


        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(boldLabel("Subtotal:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        lblTotal = new JLabel("Rp 0");
        formPanel.add(lblTotal, gbc);


        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        formPanel.add(boldLabel("Total Bayar:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        lblFinalTotal = new JLabel("Rp 0");
        lblFinalTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFinalTotal.setForeground(new Color(192, 57, 43));
        formPanel.add(lblFinalTotal, gbc);

        add(formPanel, BorderLayout.CENTER);


        btnPlaceOrder = new JButton("✓ Buat Pesanan & Bayar");
        btnPlaceOrder.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPlaceOrder.setBackground(new Color(41, 128, 185));
        btnPlaceOrder.setForeground(Color.WHITE);
        btnPlaceOrder.setFocusPainted(false);
        btnPlaceOrder.setPreferredSize(new Dimension(0, 42));
        add(btnPlaceOrder, BorderLayout.SOUTH);


        btnApplyPromo.addActionListener(e -> applyPromo());
        btnPlaceOrder.addActionListener(e -> placeOrder());
    }

    
    public void loadFromCart(List<Object[]> items) {
        this.cartItems = items;
        cartTotal = BigDecimal.ZERO;
        for (Object[] item : items) {
            cartTotal = cartTotal.add((BigDecimal) item[4]);
        }
        lblTotal.setText("Rp " + String.format("%,.0f", cartTotal));
        lblFinalTotal.setText("Rp " + String.format("%,.0f", cartTotal));
        txtPromoCode.setText("");
    }

    private void applyPromo() {
        String code = txtPromoCode.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan kode promo terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        BigDecimal finalTotal = promoDAO.applyPromo(code, cartTotal);
        lblFinalTotal.setText("Rp " + String.format("%,.0f", finalTotal));
        if (finalTotal.compareTo(cartTotal) < 0) {
            JOptionPane.showMessageDialog(this,
                    "Promo berhasil! Hemat Rp " + String.format("%,.0f", cartTotal.subtract(finalTotal)),
                    "Promo Aktif", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Kode promo tidak valid atau tidak memenuhi syarat.",
                    "Promo Tidak Berlaku", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void placeOrder() {

        if (cartItems == null || cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang kosong!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        String address = txtAddress.getText().trim();
        if (address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Alamat pengiriman wajib diisi!",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String customerId = dashboard.getCustomerId();
        String promoCode  = txtPromoCode.getText().trim().isEmpty() ? null
                            : txtPromoCode.getText().trim();


        String orderId     = orderDAO.generateOrderId();
        String referenceNo = orderDAO.generatePaymentRef();


        boolean orderOk = orderDAO.createOrder(orderId, customerId, promoCode);
        if (!orderOk) {
            JOptionPane.showMessageDialog(this, "Gagal membuat pesanan. Coba lagi.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        boolean allItemsOk = true;
        for (Object[] item : cartItems) {
            String productId = (String) item[0];
            int qty          = (int) item[3];
            boolean itemOk   = orderDAO.addOrderItem(orderId, productId, qty);
            if (!itemOk) {
                allItemsOk = false;
                JOptionPane.showMessageDialog(this,
                        "Gagal menambah item: " + item[1] + ". Mungkin stok habis.",
                        "Error Item", JOptionPane.ERROR_MESSAGE);
                break;
            }
        }
        if (!allItemsOk) return;


        String payMethod = (String) cmbPayment.getSelectedItem();
        boolean payOk;
        if (payMethod != null && payMethod.startsWith("Transfer")) {
            String bank = JOptionPane.showInputDialog(this, "Nama Bank (BCA/BNI/BRI/Mandiri):", "BCA");
            String acc  = JOptionPane.showInputDialog(this, "Nomor Rekening Pengirim:");
            if (bank == null || acc == null) return;
            payOk = orderDAO.payWithBank(referenceNo, orderId, bank.trim(), acc.trim());
        } else {
            String provider = JOptionPane.showInputDialog(this,
                    "Provider E-Wallet (GoPay/OVO/Dana/ShopeePay):", "GoPay");
            String phone    = JOptionPane.showInputDialog(this, "Nomor HP terdaftar:");
            if (provider == null || phone == null) return;
            payOk = orderDAO.payWithWallet(referenceNo, orderId, provider.trim(), phone.trim());
        }

        if (payOk) {
            JOptionPane.showMessageDialog(this,
                    "Pesanan " + orderId + " berhasil dibuat!\nNo. Referensi: " + referenceNo,
                    "Pesanan Berhasil", JOptionPane.INFORMATION_MESSAGE);
            dashboard.getCartPanel().clearAfterCheckout();
            dashboard.switchPanel("History");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Pembayaran gagal. Pesanan tetap dibuat dengan status 'Menunggu Pembayaran'.\n"
                    + "ID Pesanan: " + orderId,
                    "Pembayaran Gagal", JOptionPane.WARNING_MESSAGE);
        }
    }

    private JLabel boldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return lbl;
    }
}