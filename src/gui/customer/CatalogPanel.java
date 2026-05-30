package gui.customer;

import dao.CategoryDAO;
import dao.ProductDAO;
import model.Category;
import model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;


public class CatalogPanel extends JPanel {

    private final CustomerDashboard dashboard;
    private final ProductDAO productDAO  = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch, txtMinPrice, txtMaxPrice;
    private JComboBox<String> cmbCategory;
    private JButton btnSearch, btnRefresh, btnAddToCart;


    private String selectedProductId;

    public CatalogPanel(CustomerDashboard dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        buildUI();
        loadCategories();
        loadProductData();
    }

    private void buildUI() {

        JLabel lblTitle = new JLabel("Katalog Produk Fashion");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(lblTitle, BorderLayout.NORTH);


        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter & Pencarian"));

        txtSearch = new JTextField(15);
        txtSearch.setToolTipText("Nama produk...");

        cmbCategory = new JComboBox<>();
        cmbCategory.setPreferredSize(new Dimension(150, 26));

        txtMinPrice = new JTextField("0", 7);
        txtMaxPrice = new JTextField("", 7);

        btnSearch  = new JButton("Cari");
        btnRefresh = new JButton("Tampilkan Semua");

        filterPanel.add(new JLabel("Kata Kunci:"));
        filterPanel.add(txtSearch);
        filterPanel.add(new JLabel("Kategori:"));
        filterPanel.add(cmbCategory);
        filterPanel.add(new JLabel("Harga Min:"));
        filterPanel.add(txtMinPrice);
        filterPanel.add(new JLabel("Harga Max:"));
        filterPanel.add(txtMaxPrice);
        filterPanel.add(btnSearch);
        filterPanel.add(btnRefresh);


        String[] columns = {"ID", "Nama Produk", "Harga (Rp)", "Stok", "Kategori", "Toko", "Jenis"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.setRowHeight(22);
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && productTable.getSelectedRow() >= 0) {
                selectedProductId = (String) tableModel.getValueAt(
                        productTable.getSelectedRow(), 0);
            }
        });


        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAddToCart = new JButton("+ Tambah ke Keranjang");
        btnAddToCart.setBackground(new Color(41, 128, 185));
        btnAddToCart.setForeground(Color.WHITE);
        btnAddToCart.setFocusPainted(false);
        actionPanel.add(btnAddToCart);


        btnSearch.addActionListener(e -> searchProducts());
        btnRefresh.addActionListener(e -> loadProductData());
        btnAddToCart.addActionListener(e -> addToCart());
        txtSearch.addActionListener(e -> searchProducts());


        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(filterPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private void loadCategories() {
        cmbCategory.removeAllItems();
        cmbCategory.addItem("Semua Kategori");
        List<Category> cats = categoryDAO.getAll();
        for (Category c : cats) {
            cmbCategory.addItem(c.getCategoryId() + " - " + c.getCName());
        }
    }

    
    public void loadProductData() {
        tableModel.setRowCount(0);
        List<Product> products = productDAO.getAll();
        for (Product p : products) {
            tableModel.addRow(new Object[]{
                p.getProductId(),
                p.getPName(),
                String.format("%,.0f", p.getPrice()),
                p.getStock(),
                p.getCategoryName(),
                p.getStoreName(),
                p.getJenisProduk()
            });
        }
    }

    private void searchProducts() {
        String keyword  = txtSearch.getText().trim();
        String catSel   = (String) cmbCategory.getSelectedItem();
        String catId    = (catSel == null || catSel.startsWith("Semua"))
                          ? null : catSel.split(" - ")[0];
        Double minPrice = null, maxPrice = null;
        try { minPrice = Double.parseDouble(txtMinPrice.getText().trim()); } catch (NumberFormatException ignored) {}
        try { maxPrice = Double.parseDouble(txtMaxPrice.getText().trim()); } catch (NumberFormatException ignored) {}

        tableModel.setRowCount(0);
        List<Product> products = productDAO.search(keyword, catId, minPrice, maxPrice);
        for (Product p : products) {
            tableModel.addRow(new Object[]{
                p.getProductId(),
                p.getPName(),
                String.format("%,.0f", p.getPrice()),
                p.getStock(),
                p.getCategoryName(),
                p.getStoreName(),
                p.getJenisProduk()
            });
        }
        if (products.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada produk yang cocok.", "Hasil Kosong",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void addToCart() {
        if (selectedProductId == null) {
            JOptionPane.showMessageDialog(this, "Pilih produk terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Product p = productDAO.getById(selectedProductId);
        if (p == null) return;
        if (p.getStock() <= 0) {
            JOptionPane.showMessageDialog(this, "Stok produk habis!", "Stok Habis",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String qtyStr = JOptionPane.showInputDialog(this,
                "Masukkan jumlah untuk: " + p.getPName() + "\n(Stok tersedia: " + p.getStock() + ")",
                "Jumlah", JOptionPane.QUESTION_MESSAGE);
        if (qtyStr == null) return;
        try {
            int qty = Integer.parseInt(qtyStr.trim());
            if (qty <= 0 || qty > p.getStock()) {
                JOptionPane.showMessageDialog(this, "Jumlah tidak valid.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            CartPanel cartPanel = dashboard.getCartPanel();
            cartPanel.addItem(p, qty);
            JOptionPane.showMessageDialog(this,
                    p.getPName() + " (" + qty + " pcs) ditambahkan ke keranjang!",
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Masukkan angka yang valid.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}