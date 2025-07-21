package busbooking.ui;

import busbooking.Traveler;
import busbooking.ui.utils.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.net.URL;

public class ProfilePanel extends JPanel implements UIConstants, UpdatablePanel {
    private BusBookingGUI mainApp;
    private Traveler currentUser;

    private JLabel firstNameValueLabel, lastNameValueLabel, phoneValueLabel;
    private JLabel emailLabelValue, usernameLabelValue, adminStatusLabelValue;
    private ImageIcon returnIcon, profileLargeIcon;

    public ProfilePanel(BusBookingGUI mainApp) {
        this.mainApp = mainApp;
        setName(BusBookingGUI.PROFILE_PANEL);
        loadIcons();
        initComponents();
    }

    private void loadIcons() {
        try {
            URL returnIconUrl = getClass().getResource("/busbooking/ui/utils/icons/return.png");
            if (returnIconUrl != null) returnIcon = new ImageIcon(new ImageIcon(returnIconUrl).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
            URL profileIconUrl = getClass().getResource("/busbooking/ui/utils/icons/user.png");
            if (profileIconUrl != null) profileLargeIcon = new ImageIcon(new ImageIcon(profileIconUrl).getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));
        } catch (Exception e) { System.err.println("ProfilePanel: Error loading icons: " + e.getMessage()); }
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(0, 0, PADDING_COMPONENT_DEFAULT, 0));

        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(COLOR_HEADER_BACKGROUND);
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, PADDING_COMPONENT_DEFAULT, 8, PADDING_COMPONENT_DEFAULT));
        JButton backButton = new JButton(returnIcon != null ? "" : "< Back");
        if(returnIcon != null) backButton.setIcon(returnIcon);
        backButton.setToolTipText("Back to Home");
        backButton.setOpaque(false); backButton.setContentAreaFilled(false); backButton.setBorderPainted(false);
        backButton.setFont(FONT_BUTTON_SECONDARY);
        if(returnIcon == null) backButton.setForeground(COLOR_TEXT_ON_PRIMARY_ACTION);
        backButton.setFocusPainted(false); backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> mainApp.showPanel(BusBookingGUI.HOME_PANEL));
        topPanel.add(backButton, BorderLayout.WEST);
        JLabel headerLabel = new JLabel("My Profile", SwingConstants.CENTER);
        headerLabel.setFont(FONT_HEADING_H2); headerLabel.setForeground(COLOR_TEXT_ON_PRIMARY_ACTION);
        topPanel.add(headerLabel, BorderLayout.CENTER);
        topPanel.add(Box.createHorizontalStrut(backButton.getPreferredSize().width > 0 ? backButton.getPreferredSize().width : 50), BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JPanel detailsFormPanel = new JPanel(new GridBagLayout());
        detailsFormPanel.setOpaque(false); detailsFormPanel.setBorder(new EmptyBorder(20, PADDING_LARGE, 20, PADDING_LARGE));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER; gbc.weightx = 1.0; gbc.insets = new Insets(8, 0, 8, 0);

        if (profileLargeIcon != null) {
            JLabel iconDisplayLabel = new JLabel(profileLargeIcon);
            gbc.insets = new Insets(0, 0, 25, 0); detailsFormPanel.add(iconDisplayLabel, gbc);
            gbc.insets = new Insets(8, 0, 8, 0);
        }

        firstNameValueLabel = createProfileValueLabel(); lastNameValueLabel = createProfileValueLabel();
        phoneValueLabel = createProfileValueLabel(); emailLabelValue = createProfileValueLabel();
        usernameLabelValue = createProfileValueLabel(); adminStatusLabelValue = createProfileValueLabel();

        addProfileField(detailsFormPanel, gbc, "First Name:", firstNameValueLabel);
        addProfileField(detailsFormPanel, gbc, "Last Name:", lastNameValueLabel);
        addProfileField(detailsFormPanel, gbc, "Email:", emailLabelValue);
        addProfileField(detailsFormPanel, gbc, "Username:", usernameLabelValue);
        addProfileField(detailsFormPanel, gbc, "Phone Number:", phoneValueLabel);
        addProfileField(detailsFormPanel, gbc, "Account Type:", adminStatusLabelValue);

        GridBagConstraints gbcSpacer = new GridBagConstraints();
        gbcSpacer.gridwidth = GridBagConstraints.REMAINDER; gbcSpacer.weighty = 1.0;
        detailsFormPanel.add(Box.createGlue(), gbcSpacer);

        JScrollPane scrollPane = new JScrollPane(detailsFormPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); scrollPane.setOpaque(false); scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel createProfileValueLabel() {
        JLabel label = new JLabel("-");
        label.setFont(FONT_BODY_PLAIN.deriveFont(15f)); label.setForeground(COLOR_TEXT_DARK_PRIMARY);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,1,0, COLOR_BORDER_LIGHT), BorderFactory.createEmptyBorder(5,2,5,2)));
        return label;
    }

    private void addProfileField(JPanel parent, GridBagConstraints gbcTemplate, String labelText, Component valueComponent) {
        GridBagConstraints gbcLabel = (GridBagConstraints) gbcTemplate.clone();
        gbcLabel.gridwidth = 1; gbcLabel.weightx = 0.3; gbcLabel.fill = GridBagConstraints.NONE;
        gbcLabel.anchor = GridBagConstraints.EAST; gbcLabel.insets = new Insets(5,0,5,10);
        JLabel fieldLabel = new JLabel(labelText);
        fieldLabel.setFont(FONT_LABEL_BOLD); fieldLabel.setForeground(COLOR_TEXT_DARK_SECONDARY);
        parent.add(fieldLabel, gbcLabel);
        GridBagConstraints gbcValue = (GridBagConstraints) gbcTemplate.clone();
        gbcValue.gridx = 1; gbcValue.gridwidth = GridBagConstraints.REMAINDER;
        gbcValue.weightx = 0.7; gbcValue.fill = GridBagConstraints.HORIZONTAL;
        gbcValue.anchor = GridBagConstraints.WEST; gbcValue.insets = new Insets(5,0,5,0);
        parent.add(valueComponent, gbcValue);
    }

    @Override
    public void updatePanelData() {
        currentUser = mainApp.getCurrentTraveler();
        if (currentUser != null) {
            firstNameValueLabel.setText(currentUser.getFirstName());
            lastNameValueLabel.setText(currentUser.getLastName());
            emailLabelValue.setText(currentUser.getEmail());
            usernameLabelValue.setText(currentUser.getUsername());
            phoneValueLabel.setText(currentUser.getPhoneNumber() != null && !currentUser.getPhoneNumber().isEmpty() ? currentUser.getPhoneNumber() : "Not Provided");
            adminStatusLabelValue.setText(currentUser.isAdmin() ? "Administrator" : "Standard User");
        } else {
            firstNameValueLabel.setText("N/A"); lastNameValueLabel.setText("N/A");
            emailLabelValue.setText("N/A"); usernameLabelValue.setText("N/A");
            phoneValueLabel.setText("N/A"); adminStatusLabelValue.setText("N/A");
        }
    }

    @Override
    public void addNotify() { super.addNotify(); updatePanelData(); }
}