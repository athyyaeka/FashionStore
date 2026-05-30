






package gui.customer;

import dao.OrderDAO;
import model.Order;
import model.OrderItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderHistoryPanel extends JPanel {

    private final String customerId;
    private final OrderDAO orderDAO = new OrderDAO();

    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JButton btnRefresh, btnDetail;

    public OrderHistoryPanel(String customerId) {
        this.customerId = customerId;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        buildUI();
        loadHistoryData();
    }

    private void buildUI() {
        JLabel lblTitle = new JLabel("Riwayat Pesanan Saya");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(lblTitle, BorderLayout.NORTH);


        String[] columns = {"ID Order", "Tanggal", "Total (Rp)", "Status", "Promo", "No. Resi", "Status Kirim"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        historyTable = new JTable(tableModel);
        historyTable.setRowHeight(22);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(historyTable), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRefresh = new JButton("Refresh");
        btnDetail  = new JButton("Lihat Detail Item");
        btnDetail.setBackground(new Color(41, 128, 185));
        btnDetail.setForeground(Color.WHITE);
        actionPanel.add(btnRefresh);
        actionPanel.add(btnDetail);
        add(actionPanel, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> loadHistoryData());
        btnDetail.addActionListener(e -> showOrderDetail());
    }

    public void loadHistoryData() {
        tableModel.setRowCount(0);
        List<Order> orders = orderDAO.getByCustomer(customerId);
        for (Order o : orders) {
            tableModel.addRow(new Object[]{
                o.getOrderId(),
                o.getOrderDate(),
                String.format("%,.0f", o.getTotalPrice()),
                o.getStatus(),
                o.getPromoCode() != null ? o.getPromoCode() : "-",
                o.getTrackingNo() != null ? o.getTrackingNo() : "-",
                o.getShipStatus() != null ? o.getShipStatus() : "-"
            });
        }
    }

    private void showOrderDetail() {
        int row = historyTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih pesanan terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String orderId = (String) tableModel.getValueAt(row, 0);
        List<OrderItem> items = orderDAO.getItems(orderId);

        StringBuilder sb = new StringBuilder("Detail Pesanan: " + orderId + "\n\n");
        sb.append(String.format("%-30s %10s %6s %15s%n", "Produk", "Harga", "Qty", "Subtotal"));
        sb.append("-".repeat(65)).append("\n");
        for (OrderItem item : items) {
            sb.append(String.format("%-30s %10s %6d %15s%n",
                item.getProductName(),
                String.format("%,.0f", item.getUnitPrice()),
                item.getQuantity(),
                String.format("%,.0f", item.getUnitPrice().multiply(
                    java.math.BigDecimal.valueOf(item.getQuantity())))
            ));
        }
        JTextArea ta = new JTextArea(sb.toString());
        ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ta.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(ta),
                "Detail Pesanan " + orderId, JOptionPane.PLAIN_MESSAGE);
    }
}