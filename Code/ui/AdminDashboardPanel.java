package busbooking.ui;

import busbooking.Traveler;
import busbooking.ui.utils.UIConstants;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class AdminDashboardPanel extends JPanel implements UIConstants, UpdatablePanel {
    private BusBookingGUI mainApp;
    private ImageIcon returnIcon;

    public AdminDashboardPanel(BusBookingGUI mainApp) {
        this.mainApp = mainApp;
        setName(BusBookingGUI.ADMIN_DASHBOARD_PANEL);
        loadIcons();
        initComponents();
    }

    private void loadIcons() {
        try {
            URL returnIconUrl = getClass().getResource("/busbooking/ui/utils/icons/return.png");
            if (returnIconUrl != null) {
                returnIcon = new ImageIcon(new ImageIcon(returnIconUrl).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
            } else {
                System.err.println("AdminDashboardPanel: Couldn't find return.png");
            }
        } catch (Exception e) {
            System.err.println("AdminDashboardPanel: Error loading icons: " + e.getMessage());
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(0, 0, PADDING_COMPONENT_DEFAULT, 0));

        JPanel topPanel = new JPanel(new BorderLayout(10,0));
        topPanel.setBackground(COLOR_HEADER_BACKGROUND);
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, PADDING_COMPONENT_DEFAULT, 8, PADDING_COMPONENT_DEFAULT));

        JButton backButton = new JButton();
        if (returnIcon != null) {
            backButton.setIcon(returnIcon);
            backButton.setToolTipText("Back to Home");
        } else {
            backButton.setText("< Home");
            backButton.setForeground(COLOR_TEXT_ON_PRIMARY_ACTION);
        }
        backButton.setOpaque(false);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setFont(FONT_BUTTON_SECONDARY);
        backButton.setFocusPainted(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> mainApp.showPanel(BusBookingGUI.HOME_PANEL));
        topPanel.add(backButton, BorderLayout.WEST);

        JLabel headerLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(FONT_HEADING_H1);
        headerLabel.setForeground(COLOR_TEXT_ON_PRIMARY_ACTION);
        topPanel.add(headerLabel, BorderLayout.CENTER);

        topPanel.add(Box.createHorizontalStrut(backButton.getPreferredSize().width > 0 ? backButton.getPreferredSize().width : 50), BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridBagLayout());
        menuPanel.setBackground(COLOR_BACKGROUND);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 0, 10, 0);

        JButton manageBusesButton = createMenuButton("Manage Buses", COLOR_PRIMARY_ACTION, COLOR_BUTTON_TEXT_WHITE);
        manageBusesButton.addActionListener(e -> mainApp.showPanel(BusBookingGUI.MANAGE_BUSES_PANEL));
        menuPanel.add(manageBusesButton, gbc);

        JButton manageTripsButton = createMenuButton("Manage Trips", COLOR_PRIMARY_ACTION, COLOR_BUTTON_TEXT_WHITE);
        manageTripsButton.addActionListener(e -> mainApp.showPanel(BusBookingGUI.MANAGE_TRIPS_PANEL));
        menuPanel.add(manageTripsButton, gbc);

        JButton manageUsersButton = createMenuButton("Manage Users", COLOR_PRIMARY_ACTION, COLOR_BUTTON_TEXT_WHITE);
        manageUsersButton.addActionListener(e -> mainApp.showPanel(BusBookingGUI.MANAGE_USERS_PANEL));
        menuPanel.add(manageUsersButton, gbc);

        JButton viewBookingsButton = createMenuButton("View All Bookings", COLOR_PRIMARY_ACTION, COLOR_BUTTON_TEXT_WHITE);
        viewBookingsButton.addActionListener(e -> mainApp.showPanel(BusBookingGUI.VIEW_ALL_BOOKINGS_PANEL));
        menuPanel.add(viewBookingsButton, gbc);

        gbc.weighty = 1.0;
        menuPanel.add(Box.createGlue(), gbc);

        add(menuPanel, BorderLayout.CENTER);
    }

    private JButton createMenuButton(String text, Color backgroundColor, Color foregroundColor) {
        JButton button = new JButton(text);
        button.setFont(FONT_HEADING_H3.deriveFont(18f));
        button.setBackground(backgroundColor);
        button.setForeground(foregroundColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(PADDING_COMPONENT_DEFAULT + 8, PADDING_LARGE, PADDING_COMPONENT_DEFAULT + 8, PADDING_LARGE));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    @Override
    public void updatePanelData() {
        Traveler currentUser = mainApp.getCurrentTraveler();
        if (currentUser == null || !currentUser.isAdmin()) {
            JOptionPane.showMessageDialog(mainApp.getMainFrame(),
                    "Access Denied. Admin privileges required.",
                    "Access Denied", JOptionPane.ERROR_MESSAGE);
            mainApp.showPanel(currentUser == null ? BusBookingGUI.LOGIN_PANEL : BusBookingGUI.HOME_PANEL);
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        updatePanelData();
    }
}