package gui.customer;

import model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class CartPanel extends JPanel {

    private final CustomerDashboard dashboard;


    private final List<Object[]> cartItems = new ArrayList<>();

    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel lblTotal;
    private JButton btnCheckout, btnRemove, btnClear;

    public CartPanel(CustomerDashboard dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        buildUI();
    }

    private void buildUI() {
        JLabel lblTitle = new JLabel("Keranjang Belanja Kamu");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(lblTitle, BorderLayout.NORTH);

        String[] columns = {"ID Produk", "Nama Produk", "Harga (Rp)", "Jumlah", "Subtotal (Rp)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        cartTable = new JTable(tableModel);
        cartTable.setRowHeight(22);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        lblTotal = new JLabel("Total: Rp 0", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        bottomPanel.add(lblTotal, BorderLayout.NORTH);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRemove = new JButton("Hapus Item");
        btnRemove.setBackground(new Color(192, 57, 43));
        btnRemove.setForeground(Color.WHITE);
        btnRemove.setFocusPainted(false);

        btnClear = new JButton("Kosongkan");
        btnClear.setFocusPainted(false);

        btnCheckout = new JButton("Lanjut ke Checkout →");
        btnCheckout.setBackground(new Color(39, 174, 96));
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCheckout.setFocusPainted(false);

        actionPanel.add(btnRemove);
        actionPanel.add(btnClear);
        actionPanel.add(btnCheckout);
        bottomPanel.add(actionPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);


        btnRemove.addActionListener(e -> removeSelected());
        btnClear.addActionListener(e -> clearCart());
        btnCheckout.addActionListener(e -> goToCheckout());
    }

    
    public void addItem(Product p, int quantity) {

        for (Object[] item : cartItems) {
            if (item[0].equals(p.getProductId())) {
                int newQty = (int) item[3] + quantity;
                item[3] = newQty;
                item[4] = p.getPrice().multiply(BigDecimal.valueOf(newQty));
                refreshTable();
                return;
            }
        }

        BigDecimal subtotal = p.getPrice().multiply(BigDecimal.valueOf(quantity));
        cartItems.add(new Object[]{
            p.getProductId(), p.getPName(), p.getPrice(), quantity, subtotal
        });
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        BigDecimal total = BigDecimal.ZERO;
        for (Object[] item : cartItems) {
            BigDecimal subtotal = (BigDecimal) item[4];
            total = total.add(subtotal);
            tableModel.addRow(new Object[]{
                item[0], item[1],
                String.format("%,.0f", item[2]),
                item[3],
                String.format("%,.0f", subtotal)
            });
        }
        lblTotal.setText("Total: Rp " + String.format("%,.0f", total));
    }

    private void removeSelected() {
        int row = cartTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih item yang ingin dihapus.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cartItems.remove(row);
        refreshTable();
    }

    private void clearCart() {
        if (cartItems.isEmpty()) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Kosongkan seluruh keranjang?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            cartItems.clear();
            refreshTable();
        }
    }

    private void goToCheckout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang masih kosong!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        dashboard.getCheckoutPanel().loadFromCart(cartItems);
        dashboard.switchPanel("Checkout");
    }

    
    public void clearAfterCheckout() {
        cartItems.clear();
        refreshTable();
    }

    public List<Object[]> getCartItems() { return cartItems; }
}