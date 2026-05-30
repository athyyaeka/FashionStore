package gui.admin;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {
    private JPanel sidebarPanel;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;


    private ProductManagePanel productManagePanel;
    private OrderManagePanel orderManagePanel;
    private PromoManagePanel promoManagePanel;
    private CustomerManagePanel customerManagePanel;
    private ReportPanel reportPanel;

    public AdminDashboard() {
        setTitle("FashionStore - Admin Management Dashboard");
        setSize(1200, 700); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);


        productManagePanel = new ProductManagePanel();
        orderManagePanel = new OrderManagePanel();
        promoManagePanel = new PromoManagePanel();
        customerManagePanel = new CustomerManagePanel();
        reportPanel = new ReportPanel();


        mainContentPanel.add(productManagePanel, "ManageProduct");
        mainContentPanel.add(orderManagePanel, "ManageOrder");
        mainContentPanel.add(promoManagePanel, "ManagePromo");
        mainContentPanel.add(customerManagePanel, "ManageCustomer");
        mainContentPanel.add(reportPanel, "Report");


        initSidebar();


        add(sidebarPanel, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);
    }

    private void initSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(240, 700));
        sidebarPanel.setBackground(new Color(21, 34, 56)); 
        sidebarPanel.setLayout(new GridLayout(7, 1, 10, 10));

        JLabel lblAdmin = new JLabel("PANEL KENDALI ADMIN", SwingConstants.CENTER);
        lblAdmin.setForeground(Color.WHITE);
        lblAdmin.setFont(new Font("Arial", Font.BOLD, 14));
        sidebarPanel.add(lblAdmin);


        JButton btnProduct = createAdminButton("Kelola Produk");
        JButton btnOrder = createAdminButton("Kelola Pesanan");
        JButton btnPromo = createAdminButton("Kelola Promo");
        JButton btnCustomer = createAdminButton("Kelola Customer");
        JButton btnReport = createAdminButton("Laporan Penjualan");
        JButton btnLogout = createAdminButton("Keluar Sistem");


        btnProduct.addActionListener(e -> cardLayout.show(mainContentPanel, "ManageProduct"));
        btnOrder.addActionListener(e -> cardLayout.show(mainContentPanel, "ManageOrder"));
        btnPromo.addActionListener(e -> cardLayout.show(mainContentPanel, "ManagePromo"));
        btnCustomer.addActionListener(e -> cardLayout.show(mainContentPanel, "ManageCustomer"));
        btnReport.addActionListener(e -> cardLayout.show(mainContentPanel, "Report"));
        btnLogout.addActionListener(e -> {
            this.dispose();

        });

        sidebarPanel.add(btnProduct);
        sidebarPanel.add(btnOrder);
        sidebarPanel.add(btnPromo);
        sidebarPanel.add(btnCustomer);
        sidebarPanel.add(btnReport);
        sidebarPanel.add(btnLogout);
    }

    private JButton createAdminButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(33, 49, 77));
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        return btn;
    }
}