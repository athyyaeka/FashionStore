package gui.customer;

import javax.swing.*;
import java.awt.*;


public class CustomerDashboard extends JFrame {

    private JPanel sidebarPanel;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private final String customerId;

    private CatalogPanel catalogPanel;
    private CartPanel cartPanel;
    private CheckoutPanel checkoutPanel;
    private OrderHistoryPanel historyPanel;
    private TrackingPanel trackingPanel;
    private ProfilePanel profilePanel;

    public CustomerDashboard(String customerId) {
        this.customerId = customerId;
        setTitle("FashionStore — Customer Dashboard");
        setSize(1100, 660);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        cardLayout        = new CardLayout();
        mainContentPanel  = new JPanel(cardLayout);


        cartPanel     = new CartPanel(this);
        checkoutPanel = new CheckoutPanel(this);
        catalogPanel  = new CatalogPanel(this);
        historyPanel  = new OrderHistoryPanel(customerId);
        trackingPanel = new TrackingPanel(this);
        profilePanel  = new ProfilePanel(customerId);

        mainContentPanel.add(catalogPanel,  "Catalog");
        mainContentPanel.add(cartPanel,     "Cart");
        mainContentPanel.add(checkoutPanel, "Checkout");
        mainContentPanel.add(historyPanel,  "History");
        mainContentPanel.add(trackingPanel, "Tracking");
        mainContentPanel.add(profilePanel,  "Profile");

        initSidebar();

        add(sidebarPanel, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);
    }

    private void initSidebar() {
        sidebarPanel = new JPanel(new GridLayout(7, 1, 0, 0));
        sidebarPanel.setPreferredSize(new Dimension(210, 660));
        sidebarPanel.setBackground(new Color(30, 39, 46));

        JLabel lblMenu = new JLabel("MENU CUSTOMER", SwingConstants.CENTER);
        lblMenu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMenu.setForeground(new Color(178, 190, 195));
        lblMenu.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        sidebarPanel.add(lblMenu);

        JButton btnCatalog  = sideBtn("🛍 Katalog Produk");
        JButton btnCart     = sideBtn("🛒 Keranjang");
        JButton btnHistory  = sideBtn("📋 Riwayat Pesanan");
        JButton btnTracking = sideBtn("📦 Lacak Pengiriman");
        JButton btnProfile  = sideBtn("👤 Profil Saya");
        JButton btnLogout   = sideBtn("🚪 Keluar");

        btnCatalog.addActionListener(e  -> cardLayout.show(mainContentPanel, "Catalog"));
        btnCart.addActionListener(e     -> cardLayout.show(mainContentPanel, "Cart"));
        btnHistory.addActionListener(e  -> { historyPanel.loadHistoryData(); cardLayout.show(mainContentPanel, "History"); });
        btnTracking.addActionListener(e -> cardLayout.show(mainContentPanel, "Tracking"));
        btnProfile.addActionListener(e  -> { profilePanel.loadProfileData(); cardLayout.show(mainContentPanel, "Profile"); });
        btnLogout.addActionListener(e   -> {
            dispose();
            new gui.LoginFrame().setVisible(true);
        });

        sidebarPanel.add(btnCatalog);
        sidebarPanel.add(btnCart);
        sidebarPanel.add(btnHistory);
        sidebarPanel.add(btnTracking);
        sidebarPanel.add(btnProfile);
        sidebarPanel.add(btnLogout);
    }

    private JButton sideBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(44, 62, 80));
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 10));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(52, 73, 94));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(44, 62, 80));
            }
        });
        return btn;
    }

    
    public void switchPanel(String panelName) {
        cardLayout.show(mainContentPanel, panelName);
    }


    public String getCustomerId()         { return customerId; }
    public CartPanel getCartPanel()       { return cartPanel; }
    public CheckoutPanel getCheckoutPanel() { return checkoutPanel; }
}