package busbooking.ui;


import busbooking.Traveler;
import busbooking.ui.utils.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import busbooking.*;

public class LoginPanel extends JPanel implements UIConstants {
    private BusBookingGUI mainApp;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel errorLabel;

    private static final String PLACEHOLDER_EMAIL = "Enter your email";

    public LoginPanel(BusBookingGUI mainApp) {
        this.mainApp = mainApp;
        setName(BusBookingGUI.LOGIN_PANEL);
        initComponents();
    }

    public void clearFormOnDisplay() {
        applyPlaceholder(emailField, PLACEHOLDER_EMAIL);
        passwordField.setText("");
        errorLabel.setText(" ");
        emailField.requestFocusInWindow();
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
        setLayout(new GridBagLayout());
        setBackground(COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(PADDING_SCREEN_DEFAULT, PADDING_SCREEN_DEFAULT, PADDING_SCREEN_DEFAULT, PADDING_SCREEN_DEFAULT));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, PADDING_INTER_COMPONENT_VERTICAL / 2, 0);

        JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
        titleLabel.setFont(FONT_HEADING_H2);
        titleLabel.setForeground(COLOR_TEXT_DARK_PRIMARY);
        gbc.insets = new Insets(0, 0, PADDING_INTER_COMPONENT_VERTICAL * 2, 0);
        add(titleLabel, gbc);
        gbc.insets = new Insets(0, 0, PADDING_INTER_COMPONENT_VERTICAL / 2, 0);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(FONT_LABEL_BOLD);
        emailLabel.setForeground(COLOR_TEXT_DARK_SECONDARY);
        add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setFont(FONT_TEXTFIELD);
        emailField.setBorder(BORDER_TEXTFIELD_DEFAULT);
        addTextFieldFocusListener(emailField, PLACEHOLDER_EMAIL);
        gbc.insets = new Insets(0, 0, PADDING_INTER_COMPONENT_VERTICAL, 0);
        add(emailField, gbc);
        gbc.insets = new Insets(0, 0, PADDING_INTER_COMPONENT_VERTICAL / 2, 0);

    
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(FONT_LABEL_BOLD);
        passwordLabel.setForeground(COLOR_TEXT_DARK_SECONDARY);
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(FONT_TEXTFIELD);
        passwordField.setBorder(BORDER_TEXTFIELD_DEFAULT);
        addTextFieldFocusListener(passwordField, null);
        passwordField.addActionListener(e -> performLogin());
        gbc.insets = new Insets(0, 0, PADDING_INTER_COMPONENT_VERTICAL, 0);
        add(passwordField, gbc);

        errorLabel = new JLabel(" ", SwingConstants.CENTER);
        errorLabel.setFont(FONT_LABEL_PLAIN);
        errorLabel.setForeground(COLOR_ERROR_RED);
        gbc.insets = new Insets(0, 0, PADDING_INTER_COMPONENT_VERTICAL / 2, 0);
        add(errorLabel, gbc);

        loginButton = new JButton("Login");
        loginButton.setFont(FONT_BUTTON_PRIMARY);
        loginButton.setBackground(COLOR_PRIMARY_ACTION);
        loginButton.setForeground(COLOR_BUTTON_TEXT_WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BORDER_BUTTON_PRIMARY);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> performLogin());
        gbc.insets = new Insets(PADDING_INTER_COMPONENT_VERTICAL / 2, 0, PADDING_INTER_COMPONENT_VERTICAL, 0);
        add(loginButton, gbc);

        JLabel forgotPasswordLabel = new JLabel("Forgot Password?", SwingConstants.CENTER);
        forgotPasswordLabel.setFont(FONT_LINK);
        forgotPasswordLabel.setForeground(COLOR_PRIMARY_ACTION);
        forgotPasswordLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        gbc.insets = new Insets(0, 0, PADDING_INTER_COMPONENT_VERTICAL, 0);
        add(forgotPasswordLabel, gbc);

        JPanel signUpLinkPanel = createLinkPanel("Don't have an account? ", "Sign Up",
                e -> mainApp.showPanel(BusBookingGUI.SIGNUP_PANEL));
        add(signUpLinkPanel, gbc);

        gbc.weighty = 1.0;
        add(Box.createVerticalStrut(0), gbc);

        clearFormOnDisplay();
    }

    private void addTextFieldFocusListener(JTextField textField, String placeholder) {
        if (placeholder != null) {
            applyPlaceholder(textField, placeholder);
        }
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (placeholder != null) removePlaceholder(textField, placeholder);
                textField.setBorder(BORDER_TEXTFIELD_FOCUSED);
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (placeholder != null) applyPlaceholder(textField, placeholder);
                textField.setBorder(BORDER_TEXTFIELD_DEFAULT);
            }
        });
    }

    private JPanel createLinkPanel(String text, String linkText, java.awt.event.ActionListener actionListener) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.setOpaque(false);
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(FONT_LABEL_PLAIN);
        textLabel.setForeground(COLOR_TEXT_LIGHT_PRIMARY);

        JLabel linkLabel = new JLabel(linkText);
        linkLabel.setFont(FONT_LINK);
        linkLabel.setForeground(COLOR_PRIMARY_ACTION);
        linkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                actionListener.actionPerformed(null);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                linkLabel.setText("<html><u>" + linkText + "</u></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                linkLabel.setText(linkText);
            }
        });

        panel.add(textLabel);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(linkLabel);
        return panel;
    }

    private void performLogin() {
        String email = emailField.getText().trim();
        if (email.equals(PLACEHOLDER_EMAIL)) email = "";
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Email and password cannot be empty.");
            return;
        }

        Traveler loggedInTraveler = null;
        String sql = "SELECT user_id, username, password, email, first_name, last_name, phone_number, is_admin FROM users WHERE email = ? AND password = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password); // Plain text password check

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    loggedInTraveler = new Traveler(
                            rs.getInt("user_id"), rs.getString("username"), rs.getString("password"),
                            rs.getString("email"), rs.getString("first_name"), rs.getString("last_name"),
                            rs.getString("phone_number"), rs.getBoolean("is_admin")
                    );
                }
            }
        } catch (SQLException e) {
            errorLabel.setText("Database error during login. Please try again.");
            System.err.println("Login SQL Error: " + e.getMessage());
            e.printStackTrace();
        }

        if (loggedInTraveler != null) {
            mainApp.setCurrentTraveler(loggedInTraveler);
            errorLabel.setText(" ");
            mainApp.showPanel(BusBookingGUI.HOME_PANEL);
        } else {
            errorLabel.setText("Invalid email or password. Please try again.");
            mainApp.setCurrentTraveler(null);
        }
    }
}