package busbooking.ui;

import busbooking.Traveler;
import busbooking.ui.utils.UIConstants;
import busbooking.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class HomePanel extends JPanel implements UIConstants, UpdatablePanel {
    private BusBookingGUI mainApp;
    private JLabel greetingLabel;
    private JTextField fromField, toField, dateField;
    private JButton searchButton;
    private JLabel userIconLabel;
    private JPopupMenu userPopupMenu;

    private ImageIcon userIcon;
    private static final String PLACEHOLDER_FROM = "Enter departure station";
    private static final String PLACEHOLDER_TO = "Enter arrival station";
    private static final String PLACEHOLDER_DATE = LocalDate.now().plusDays(1).format(Utils.DATE_FORMATTER);

    public HomePanel(BusBookingGUI mainApp) {
        this.mainApp = mainApp;
        setName(BusBookingGUI.HOME_PANEL);
        loadIcons();
        initComponents();
    }

    private void loadIcons() {
        try {
            URL userIconUrl = getClass().getResource("/busbooking/ui/utils/icons/user.png");
            if (userIconUrl != null) {
                userIcon = new ImageIcon(new ImageIcon(userIconUrl).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
            } else {
                System.err.println("HomePanel: Couldn't find user.png");
            }
        } catch (Exception e) {
            System.err.println("HomePanel: Error loading icons: " + e.getMessage());
        }
    }

    private void applyPlaceholder(JTextField field, String placeholder) {
        if (field.getText().isEmpty() || field.getText().equals(placeholder)) {
            field.setText(placeholder);
            field.setForeground(COLOR_TEXT_LIGHT_SECONDARY);
        }
    }

    private void removePlaceholder(JTextField field, String placeholder) {
        if (field.getText().equals(placeholder)) {
            field.setText("");
            field.setForeground(COLOR_TEXT_DARK_PRIMARY);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, PADDING_INTER_COMPONENT_VERTICAL));
        setBackground(COLOR_BACKGROUND);

        JPanel topPanel = new JPanel(new BorderLayout(10,0));
        topPanel.setBackground(COLOR_HEADER_BACKGROUND);
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, PADDING_COMPONENT_DEFAULT, 8, PADDING_COMPONENT_DEFAULT));

        greetingLabel = new JLabel("Hi there!", SwingConstants.LEFT);
        greetingLabel.setFont(FONT_HEADING_H3);
        greetingLabel.setForeground(COLOR_TEXT_ON_PRIMARY_ACTION);
        topPanel.add(greetingLabel, BorderLayout.CENTER);

        if (userIcon != null) {
            userIconLabel = new JLabel(userIcon);
        } else {
            userIconLabel = new JLabel("U");
            userIconLabel.setFont(FONT_BODY_BOLD);
            userIconLabel.setForeground(COLOR_TEXT_ON_PRIMARY_ACTION);
        }
        userIconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        userIconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                createUserPopupMenu();
                userPopupMenu.show(userIconLabel, e.getX() - userPopupMenu.getPreferredSize().width + userIconLabel.getWidth() , e.getY() + userIconLabel.getHeight()/2);
            }
        });
        topPanel.add(userIconLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JPanel searchFormOuterPanel = new JPanel(new BorderLayout());
        searchFormOuterPanel.setOpaque(false);
        searchFormOuterPanel.setBorder(BorderFactory.createEmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));

        JPanel searchFormPanel = new JPanel(new GridBagLayout());
        searchFormPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; gbc.insets = new Insets(5, 0, 5, 0);

        gbc.gridy = 0; searchFormPanel.add(createLabel("From:"), gbc);
        gbc.gridy++; fromField = createTextFieldWithPlaceholder(PLACEHOLDER_FROM); searchFormPanel.add(fromField, gbc);
        gbc.gridy++; gbc.insets = new Insets(15, 0, 5, 0); searchFormPanel.add(createLabel("To:"), gbc);
        gbc.gridy++; gbc.insets = new Insets(5, 0, 5, 0); toField = createTextFieldWithPlaceholder(PLACEHOLDER_TO); searchFormPanel.add(toField, gbc);
        gbc.gridy++; gbc.insets = new Insets(15, 0, 5, 0); searchFormPanel.add(createLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridy++; gbc.insets = new Insets(5, 0, 5, 0); dateField = createTextFieldWithPlaceholder(PLACEHOLDER_DATE);
        dateField.addActionListener(e -> performSearch()); searchFormPanel.add(dateField, gbc);

        gbc.gridy++; gbc.insets = new Insets(25, 0, 5, 0); gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        searchButton = new JButton("Search Buses");
        searchButton.setFont(FONT_BUTTON_PRIMARY); searchButton.setBackground(COLOR_PRIMARY_ACTION); searchButton.setForeground(COLOR_BUTTON_TEXT_WHITE);
        searchButton.setBorder(BORDER_BUTTON_PRIMARY); searchButton.addActionListener(e -> performSearch());
        searchFormPanel.add(searchButton, gbc);

        gbc.gridy++; gbc.weighty = 1.0; searchFormPanel.add(Box.createGlue(), gbc);
        searchFormOuterPanel.add(searchFormPanel, BorderLayout.CENTER);
        add(new JScrollPane(searchFormOuterPanel), BorderLayout.CENTER);

        JPanel bottomNavPanel = new JPanel(new GridLayout(1, 3, 0, 0));
        bottomNavPanel.setOpaque(false);
        JButton homeButton = createNavButton("Home");
        homeButton.addActionListener(e -> mainApp.showPanel(BusBookingGUI.HOME_PANEL));
        JButton bookingsButton = createNavButton("My Bookings");
        bookingsButton.addActionListener(e -> {
            if (mainApp.getCurrentTraveler() != null) mainApp.showPanel(BusBookingGUI.MY_BOOKINGS_PANEL);
            else { JOptionPane.showMessageDialog(mainApp.getMainFrame(), "Please login to view bookings.", "Login Required", JOptionPane.INFORMATION_MESSAGE); mainApp.showPanel(BusBookingGUI.LOGIN_PANEL); }
        });
        JButton profileButton = createNavButton("Profile");
        profileButton.addActionListener(e -> {
            if (mainApp.getCurrentTraveler() != null) mainApp.showPanel(BusBookingGUI.PROFILE_PANEL);
            else { JOptionPane.showMessageDialog(mainApp.getMainFrame(), "Please login to view your profile.", "Login Required", JOptionPane.INFORMATION_MESSAGE); mainApp.showPanel(BusBookingGUI.LOGIN_PANEL); }
        });
        bottomNavPanel.add(homeButton); bottomNavPanel.add(bookingsButton); bottomNavPanel.add(profileButton);
        add(bottomNavPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL_BOLD);
        label.setForeground(COLOR_TEXT_DARK_SECONDARY);
        return label;
    }

    private JTextField createTextFieldWithPlaceholder(String placeholder) {
        JTextField textField = new JTextField(20);
        textField.setFont(FONT_TEXTFIELD);
        textField.setBorder(BORDER_TEXTFIELD_DEFAULT);
        applyPlaceholder(textField, placeholder);

        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                removePlaceholder(textField, placeholder);
                textField.setBorder(BORDER_TEXTFIELD_FOCUSED);
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                applyPlaceholder(textField, placeholder);
                textField.setBorder(BORDER_TEXTFIELD_DEFAULT);
            }
        });
        return textField;
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON_SECONDARY.deriveFont(14f));
        button.setForeground(COLOR_TEXT_LIGHT_PRIMARY);
        button.setBackground(new Color(0xF8F9FA));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 0, COLOR_BORDER_LIGHT),
                BorderFactory.createEmptyBorder(12,10,12,10))
        );
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void createUserPopupMenu() {
        userPopupMenu = new JPopupMenu();
        JMenuItem accountItem = new JMenuItem("My Account");
        accountItem.setFont(FONT_LABEL_PLAIN);
        accountItem.addActionListener(e -> {
            if (mainApp.getCurrentTraveler() != null) mainApp.showPanel(BusBookingGUI.PROFILE_PANEL);
            else { JOptionPane.showMessageDialog(mainApp.getMainFrame(), "Please login to view your account.", "Login Required", JOptionPane.INFORMATION_MESSAGE); mainApp.showPanel(BusBookingGUI.LOGIN_PANEL); }
        });
        userPopupMenu.add(accountItem);

        Traveler currentUser = mainApp.getCurrentTraveler();
        if (currentUser != null && currentUser.isAdmin()) {
            JMenuItem adminConsoleItem = new JMenuItem("Admin Console");
            adminConsoleItem.setFont(FONT_LABEL_PLAIN);
            adminConsoleItem.addActionListener(e -> mainApp.showPanel(BusBookingGUI.ADMIN_DASHBOARD_PANEL));
            userPopupMenu.add(adminConsoleItem);
        }

        userPopupMenu.addSeparator();
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.setFont(FONT_LABEL_PLAIN); logoutItem.setForeground(COLOR_ERROR_RED);
        logoutItem.addActionListener(e -> { mainApp.setCurrentTraveler(null); mainApp.showPanel(BusBookingGUI.LOGIN_PANEL); });
        userPopupMenu.add(logoutItem);
    }

    private void performSearch() {
        String from = fromField.getText().trim();
        String to = toField.getText().trim();
        String dateStr = dateField.getText().trim();

        if (from.equals(PLACEHOLDER_FROM)) from = "";
        if (to.equals(PLACEHOLDER_TO)) to = "";

        boolean isDateDefaultPlaceholder = dateStr.equals(LocalDate.now().plusDays(1).format(Utils.DATE_FORMATTER));
        if (isDateDefaultPlaceholder && (from.isEmpty() || to.isEmpty())) {
            dateStr = "";
        }

        if (from.isEmpty()) { JOptionPane.showMessageDialog(this, "Please enter a departure station.", "Input Error", JOptionPane.ERROR_MESSAGE); fromField.requestFocusInWindow(); return; }
        if (to.isEmpty()) { JOptionPane.showMessageDialog(this, "Please enter an arrival station.", "Input Error", JOptionPane.ERROR_MESSAGE); toField.requestFocusInWindow(); return; }
        if (dateStr.isEmpty()) { JOptionPane.showMessageDialog(this, "Please enter a valid date.", "Input Error", JOptionPane.ERROR_MESSAGE); dateField.requestFocusInWindow(); return; }

        LocalDate date;
        try {
            date = Utils.parseDate(dateStr);
            if (date == null) throw new DateTimeParseException("Parsed date is null", dateStr, 0);
            if (date.isBefore(LocalDate.now())) { JOptionPane.showMessageDialog(this, "Cannot search for trips in a past date.", "Date Error", JOptionPane.ERROR_MESSAGE); return; }
        } catch (DateTimeParseException ex) { JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE); return; }

        SearchResultsPanel srp = (SearchResultsPanel) mainApp.getPanelByName(BusBookingGUI.SEARCH_RESULTS_PANEL);
        if (srp != null) {
            mainApp.setLastSearchCriteria(from, to, date);
            mainApp.showPanel(BusBookingGUI.SEARCH_RESULTS_PANEL);
        } else { JOptionPane.showMessageDialog(this, "Error: Search Results panel not found.", "Error", JOptionPane.ERROR_MESSAGE); }
    }

    @Override
    public void updatePanelData() {
        Traveler currentUser = mainApp.getCurrentTraveler();
        if (currentUser != null) { greetingLabel.setText("Hi, " + currentUser.getFirstName() + "!"); }
        else { greetingLabel.setText("Hi there!"); }
        applyPlaceholder(fromField, PLACEHOLDER_FROM);
        applyPlaceholder(toField, PLACEHOLDER_TO);
        applyPlaceholder(dateField, PLACEHOLDER_DATE);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        updatePanelData();
    }
}