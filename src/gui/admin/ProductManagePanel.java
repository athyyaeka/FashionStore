



package gui.admin;

import dao.CategoryDAO;
import dao.ProductDAO;
import dao.SellerDAO;
import model.Category;
import model.Product;
import model.Seller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class ProductManagePanel extends JPanel {

    private final ProductDAO  productDAO  = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final SellerDAO   sellerDAO   = new SellerDAO();

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtId, txtNama, txtHarga, txtStok, txtBerat, txtDesc;
    private JComboBox<String> cmbCategory, cmbSeller;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnRefresh;
    private JTextField txtSearch;

    public ProductManagePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        buildUI();
        loadData();
        loadComboBoxes();
    }

    private void buildUI() {
        JLabel title = new JLabel("Manajemen Data Produk");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);


        String[] cols = {"ID", "Nama Produk", "Harga (Rp)", "Stok", "Kategori", "Toko", "Jenis"};
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
        formPanel.setBorder(BorderFactory.createTitledBorder("Form Produk"));
        formPanel.setPreferredSize(new Dimension(280, 0));
        GridBagConstraints gbc = formGbc();

        txtId     = addRow(formPanel, gbc, "ID Produk:", 0);   txtId.setEditable(false);
        txtNama   = addRow(formPanel, gbc, "Nama:", 1);
        txtHarga  = addRow(formPanel, gbc, "Harga (Rp):", 2);
        txtStok   = addRow(formPanel, gbc, "Stok:", 3);
        txtBerat  = addRow(formPanel, gbc, "Berat (gram):", 4);

        gbc.gridy = 5; gbc.gridx = 0;
        formPanel.add(lbl("Kategori:"), gbc);
        gbc.gridx = 1;
        cmbCategory = new JComboBox<>();
        formPanel.add(cmbCategory, gbc);

        gbc.gridy = 6; gbc.gridx = 0;
        formPanel.add(lbl("Seller:"), gbc);
        gbc.gridx = 1;
        cmbSeller = new JComboBox<>();
        formPanel.add(cmbSeller, gbc);

        gbc.gridy = 7; gbc.gridx = 0;
        formPanel.add(lbl("Deskripsi:"), gbc);
        gbc.gridx = 1;
        txtDesc = new JTextField();
        formPanel.add(txtDesc, gbc);


        gbc.gridy = 8; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 4, 4, 4);
        JPanel btnF = new JPanel(new GridLayout(2, 2, 6, 6));
        btnAdd    = btn("Tambah",          new Color(39, 174, 96));
        btnUpdate = btn("Update",          new Color(41, 128, 185));
        btnDelete = btn("Hapus",           new Color(192, 57, 43));
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


        btnAdd.addActionListener(e    -> insertProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnClear.addActionListener(e  -> clearForm());
        btnRefresh.addActionListener(e-> loadData());
        btnSearch.addActionListener(e -> {
            model.setRowCount(0);
            productDAO.search(txtSearch.getText().trim(), null, null, null)
                .forEach(p -> model.addRow(toRow(p)));
        });
        txtSearch.addActionListener(e -> btnSearch.doClick());
    }

    private void loadData() {
        model.setRowCount(0);
        productDAO.getAll().forEach(p -> model.addRow(toRow(p)));
    }

    private void loadComboBoxes() {
        cmbCategory.removeAllItems();
        categoryDAO.getAll().forEach(c -> cmbCategory.addItem(c.getCategoryId() + " - " + c.getCName()));
        cmbSeller.removeAllItems();
        sellerDAO.getAll().forEach(s -> cmbSeller.addItem(s.getSellerId() + " - " + s.getStoreName()));
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        txtId.setText((String) model.getValueAt(row, 0));
        txtNama.setText((String) model.getValueAt(row, 1));

        Product p = productDAO.getById(txtId.getText());
        if (p == null) return;
        txtHarga.setText(p.getPrice().toPlainString());
        txtStok.setText(String.valueOf(p.getStock()));
        txtBerat.setText(String.valueOf(p.getWeightGram()));
        txtDesc.setText(p.getDescription());

        for (int i = 0; i < cmbCategory.getItemCount(); i++) {
            if (cmbCategory.getItemAt(i).startsWith(p.getCategoryId())) {
                cmbCategory.setSelectedIndex(i); break;
            }
        }
        for (int i = 0; i < cmbSeller.getItemCount(); i++) {
            if (cmbSeller.getItemAt(i).startsWith(p.getSellerId())) {
                cmbSeller.setSelectedIndex(i); break;
            }
        }
    }

    private void insertProduct() {
        Product p = buildFromForm(true);
        if (p == null) return;
        if (productDAO.insert(p)) {
            JOptionPane.showMessageDialog(this, "Produk berhasil ditambahkan!", "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
            loadData(); clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambah produk. ID mungkin sudah ada.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProduct() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih produk dari tabel untuk diupdate.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Product p = buildFromForm(false);
        if (p == null) return;
        if (productDAO.update(p)) {
            JOptionPane.showMessageDialog(this, "Produk berhasil diupdate!", "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal update produk.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih produk dari tabel untuk dihapus.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus produk " + id + "? (Data CLOTHING/ACCESSORY juga akan dihapus)",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (productDAO.delete(id)) {
                JOptionPane.showMessageDialog(this, "Produk berhasil dihapus!", "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);
                loadData(); clearForm();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Gagal menghapus. Produk mungkin direferensi di ORDER_ITEM atau REVIEW.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Product buildFromForm(boolean isInsert) {
        try {
            Product p = new Product();
            if (isInsert) p.setProductId(productDAO.generateId());
            else          p.setProductId(txtId.getText().trim());
            p.setPName(txtNama.getText().trim());
            p.setPrice(new BigDecimal(txtHarga.getText().trim().replace(",", "")));
            p.setStock(Integer.parseInt(txtStok.getText().trim()));
            p.setWeightGram(Integer.parseInt(txtBerat.getText().trim()));
            p.setDescription(txtDesc.getText().trim());

            p.setCategoryId(((String) cmbCategory.getSelectedItem()).split(" - ")[0]);
            p.setSellerId(((String) cmbSeller.getSelectedItem()).split(" - ")[0]);

            if (p.getPName().isEmpty()) throw new IllegalArgumentException("Nama produk wajib diisi.");
            return p;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga, Stok, dan Berat harus berupa angka.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return null;
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private void clearForm() {
        txtId.setText(""); txtNama.setText(""); txtHarga.setText("");
        txtStok.setText(""); txtBerat.setText(""); txtDesc.setText("");
        table.clearSelection();
    }

    private Object[] toRow(Product p) {
        return new Object[]{
            p.getProductId(), p.getPName(),
            String.format("%,.0f", p.getPrice()), p.getStock(),
            p.getCategoryName(), p.getStoreName(), p.getJenisProduk()
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
    private JLabel lbl(String text) { JLabel l = new JLabel(text); l.setFont(new Font("Segoe UI", Font.BOLD, 11)); return l; }
    private JButton btn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        if (bg != null) { b.setBackground(bg); b.setForeground(Color.WHITE); }
        return b;
    }
}