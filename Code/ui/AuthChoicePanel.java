package busbooking.ui;

import busbooking.ui.utils.UIConstants;
import javax.swing.*;
import java.awt.*;

public class AuthChoicePanel extends JPanel implements UIConstants {
    private BusBookingGUI mainApp;

    public AuthChoicePanel(BusBookingGUI mainApp) {
        this.mainApp = mainApp;
        setName(BusBookingGUI.AUTH_CHOICE_PANEL);
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBackground(COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(PADDING_SCREEN_DEFAULT * 2, PADDING_SCREEN_DEFAULT, PADDING_SCREEN_DEFAULT * 2, PADDING_SCREEN_DEFAULT));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, PADDING_INTER_COMPONENT_VERTICAL, 0);

        JLabel appNameLabel = new JLabel("BusBooker", SwingConstants.CENTER);
        appNameLabel.setFont(FONT_HEADING_H1.deriveFont(Font.BOLD, 36f));
        appNameLabel.setForeground(COLOR_TEXT_DARK_PRIMARY);
        GridBagConstraints logoGbc = (GridBagConstraints) gbc.clone();
        logoGbc.insets = new Insets(0, 0, PADDING_INTER_COMPONENT_VERTICAL * 3, 0);
        add(appNameLabel, logoGbc);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(FONT_BUTTON_PRIMARY);
        loginButton.setBackground(COLOR_PRIMARY_ACTION);
        loginButton.setForeground(COLOR_BUTTON_TEXT_WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BORDER_BUTTON_PRIMARY);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> mainApp.showPanel(BusBookingGUI.LOGIN_PANEL));
        add(loginButton, gbc);

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setFont(FONT_BUTTON_PRIMARY);
        signUpButton.setBackground(COLOR_BACKGROUND);
        signUpButton.setForeground(COLOR_PRIMARY_ACTION);
        signUpButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARY_ACTION, 2),
                BorderFactory.createEmptyBorder(PADDING_COMPONENT_DEFAULT - 2, PADDING_COMPONENT_DEFAULT * 2 - 2, PADDING_COMPONENT_DEFAULT - 2, PADDING_COMPONENT_DEFAULT * 2 - 2)
        ));
        signUpButton.setFocusPainted(false);
        signUpButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signUpButton.addActionListener(e -> mainApp.showPanel(BusBookingGUI.SIGNUP_PANEL));
        add(signUpButton, gbc);

        gbc.weighty = 1.0;
        add(Box.createVerticalStrut(0), gbc);
    }
}