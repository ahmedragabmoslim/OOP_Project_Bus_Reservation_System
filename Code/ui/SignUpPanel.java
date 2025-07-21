package busbooking.ui;


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
import java.sql.Statement;
import busbooking.*;

public class SignUpPanel extends JPanel implements UIConstants {
    private BusBookingGUI mainApp;
    private JTextField fullNameField, emailField, phoneNumberField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton signUpButton;
    private JLabel errorLabel;

    private static final String PLACEHOLDER_FULL_NAME = "Enter your full name";
    private static final String PLACEHOLDER_EMAIL = "Enter your email";
    private static final String PLACEHOLDER_PHONE = "Enter phone number (optional)";

    public SignUpPanel(BusBookingGUI mainApp) {
        this.mainApp = mainApp;
        setName(BusBookingGUI.SIGNUP_PANEL);
        initComponents();
    }

    public void clearFormOnDisplay() {
        applyPlaceholder(fullNameField, PLACEHOLDER_FULL_NAME);
        applyPlaceholder(emailField, PLACEHOLDER_EMAIL);
        applyPlaceholder(phoneNumberField, PLACEHOLDER_PHONE);
        passwordField.setText("");
        confirmPasswordField.setText("");
        errorLabel.setText(" ");
        errorLabel.setForeground(COLOR_ERROR_RED);
        fullNameField.requestFocusInWindow();
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
        setBorder(BorderFactory.createEmptyBorder(PADDING_SCREEN_DEFAULT / 2, PADDING_SCREEN_DEFAULT, PADDING_SCREEN_DEFAULT / 2, PADDING_SCREEN_DEFAULT));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, PADDING_INTER_COMPONENT_VERTICAL / 3, 0);

        JLabel titleLabel = new JLabel("Sign Up", SwingConstants.CENTER);
        titleLabel.setFont(FONT_HEADING_H2);
        titleLabel.setForeground(COLOR_TEXT_DARK_PRIMARY);
        gbc.insets = new Insets(0, 0, PADDING_INTER_COMPONENT_VERTICAL, 0);
        add(titleLabel, gbc);
        gbc.insets = new Insets(0, 0, PADDING_INTER_COMPONENT_VERTICAL / 3, 0);

        fullNameField = new JTextField(20);
        addFormField(gbc, "Full Name", fullNameField, PLACEHOLDER_FULL_NAME);

        emailField = new JTextField(20);
        addFormField(gbc, "Email", emailField, PLACEHOLDER_EMAIL);

        phoneNumberField = new JTextField(20);
        addFormField(gbc, "Phone Number", phoneNumberField, PLACEHOLDER_PHONE);

        passwordField = new JPasswordField(20);
        addFormField(gbc, "Password", passwordField, null);

        confirmPasswordField = new JPasswordField(20);
        addFormField(gbc, "Confirm Password", confirmPasswordField, null);
        confirmPasswordField.addActionListener(e -> performSignUp());

        errorLabel = new JLabel(" ", SwingConstants.CENTER);
        errorLabel.setFont(FONT_LABEL_PLAIN);
        errorLabel.setForeground(COLOR_ERROR_RED);
        gbc.insets = new Insets(0, 0, PADDING_INTER_COMPONENT_VERTICAL / 2, 0);
        add(errorLabel, gbc);

        signUpButton = new JButton("Sign Up");
        signUpButton.setFont(FONT_BUTTON_PRIMARY);
        signUpButton.setBackground(COLOR_PRIMARY_ACTION);
        signUpButton.setForeground(COLOR_BUTTON_TEXT_WHITE);
        signUpButton.setFocusPainted(false);
        signUpButton.setBorder(BORDER_BUTTON_PRIMARY);
        signUpButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signUpButton.addActionListener(e -> performSignUp());
        gbc.insets = new Insets(PADDING_INTER_COMPONENT_VERTICAL / 2, 0, PADDING_INTER_COMPONENT_VERTICAL, 0);
        add(signUpButton, gbc);

        JPanel loginLinkPanel = createLinkPanel("Already have an account? ", "Login",
                e -> mainApp.showPanel(BusBookingGUI.LOGIN_PANEL));
        add(loginLinkPanel, gbc);

        gbc.weighty = 1.0;
        add(Box.createVerticalStrut(0), gbc);

        clearFormOnDisplay();
    }

    private void addFormField(GridBagConstraints gbcMaster, String labelText, JTextField field, String placeholder) {
        JLabel label = new JLabel(labelText);
        label.setFont(FONT_LABEL_BOLD);
        label.setForeground(COLOR_TEXT_DARK_SECONDARY);
        GridBagConstraints labelGbc = (GridBagConstraints) gbcMaster.clone();
        add(label, labelGbc);

        field.setFont(FONT_TEXTFIELD);
        field.setBorder(BORDER_TEXTFIELD_DEFAULT);
        addTextFieldFocusListener(field, placeholder);
        GridBagConstraints fieldGbc = (GridBagConstraints) gbcMaster.clone();
        fieldGbc.insets = new Insets(0,0,PADDING_INTER_COMPONENT_VERTICAL /2,0);
        add(field, fieldGbc);
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
            public void mouseClicked(MouseEvent evt) { actionListener.actionPerformed(null); }
            @Override public void mouseEntered(MouseEvent e) { linkLabel.setText("<html><u>" + linkText + "</u></html>"); }
            @Override public void mouseExited(MouseEvent e) { linkLabel.setText(linkText); }
        });
        panel.add(textLabel);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(linkLabel);
        return panel;
    }

    private void performSignUp() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim().toLowerCase();
        String phoneNumber = phoneNumberField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (fullName.equals(PLACEHOLDER_FULL_NAME)) fullName = "";
        if (email.equals(PLACEHOLDER_EMAIL)) email = "";
        if (phoneNumber.equals(PLACEHOLDER_PHONE)) phoneNumber = "";

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("Full Name, Email, Password, and Confirm Password are required."); return;
        }
        if (!password.equals(confirmPassword)) { errorLabel.setText("Passwords do not match."); return; }
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) { errorLabel.setText("Invalid email format."); return; }
        if (password.length() < 6) { errorLabel.setText("Password must be at least 6 characters long."); return; }

        String firstName = fullName; String lastName = "";
        if (fullName.contains(" ")) {
            int firstSpace = fullName.indexOf(" ");
            firstName = fullName.substring(0, firstSpace);
            lastName = fullName.substring(firstSpace + 1).trim();
        }
        String username = email;

        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            String checkEmailSql = "SELECT user_id FROM users WHERE email = ?";
            try (PreparedStatement pstmtCheck = conn.prepareStatement(checkEmailSql)) {
                pstmtCheck.setString(1, email);
                try (ResultSet rsCheck = pstmtCheck.executeQuery()) {
                    if (rsCheck.next()) {
                        errorLabel.setText("This email is already registered.");
                        conn.rollback();
                        return;
                    }
                }
            }

            String insertSql = "INSERT INTO users (username, password, email, first_name, last_name, phone_number, is_admin) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmtInsert = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmtInsert.setString(1, username); pstmtInsert.setString(2, password); pstmtInsert.setString(3, email);
                pstmtInsert.setString(4, firstName); pstmtInsert.setString(5, lastName);
                pstmtInsert.setString(6, phoneNumber.isEmpty() ? null : phoneNumber); pstmtInsert.setBoolean(7, false);
                int affectedRows = pstmtInsert.executeUpdate();
                if (affectedRows > 0) {
                    conn.commit();
                    errorLabel.setForeground(COLOR_SUCCESS_GREEN);
                    errorLabel.setText("Account created successfully! Please login.");
                    Timer timer = new Timer(2000, ae -> mainApp.showPanel(BusBookingGUI.LOGIN_PANEL));
                    timer.setRepeats(false); timer.start();
                } else {
                    conn.rollback();
                    errorLabel.setForeground(COLOR_ERROR_RED);
                    errorLabel.setText("Sign up failed. Please try again.");
                }
            }
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            errorLabel.setForeground(COLOR_ERROR_RED); errorLabel.setText("Database error during sign up.");
            System.err.println("SignUp SQL Error: " + e.getMessage()); e.printStackTrace();
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }
}


