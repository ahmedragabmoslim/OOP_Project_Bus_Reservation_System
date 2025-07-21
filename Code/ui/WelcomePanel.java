package busbooking.ui;

import busbooking.ui.utils.UIConstants;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class WelcomePanel extends JPanel implements UIConstants {
    private BusBookingGUI mainApp;
    private ImageIcon backgroundImage;

    public WelcomePanel(BusBookingGUI mainApp) {
        this.mainApp = mainApp;
        setName(BusBookingGUI.WELCOME_PANEL);
        loadResources();
        initComponents();
    }

    private void loadResources() {
        try {
            URL imgUrl = getClass().getResource("/busbooking/ui/utils/icons/background.png");
            if (imgUrl != null) {
                backgroundImage = new ImageIcon(imgUrl);
            } else {
                System.err.println("WelcomePanel: Couldn't find background image: /busbooking/ui/utils/icons/background.png");
            }
        } catch (Exception e) {
            System.err.println("WelcomePanel: Error loading background image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(COLOR_BACKGROUND);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(PADDING_SCREEN_DEFAULT, PADDING_SCREEN_DEFAULT, PADDING_SCREEN_DEFAULT, PADDING_SCREEN_DEFAULT));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Book Your Bus With Us!", SwingConstants.CENTER);
        titleLabel.setFont(FONT_HEADING_H1);
        titleLabel.setForeground(COLOR_TEXT_ON_PRIMARY_ACTION);
        GridBagConstraints titleGbc = new GridBagConstraints();
        titleGbc.gridwidth = GridBagConstraints.REMAINDER;
        titleGbc.anchor = GridBagConstraints.CENTER;
        titleGbc.insets = new Insets(0, 0, PADDING_INTER_COMPONENT_VERTICAL * 3, 0);
        contentPanel.add(titleLabel, titleGbc);

        JButton getStartedButton = new JButton("Get Started");
        getStartedButton.setFont(FONT_BUTTON_PRIMARY);
        getStartedButton.setBackground(COLOR_PRIMARY_ACTION);
        getStartedButton.setForeground(COLOR_BUTTON_TEXT_WHITE);
        getStartedButton.setFocusPainted(false);
        getStartedButton.setBorder(BORDER_BUTTON_PRIMARY);
        getStartedButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        getStartedButton.addActionListener(e -> mainApp.showPanel(BusBookingGUI.AUTH_CHOICE_PANEL));

        GridBagConstraints buttonGbc = new GridBagConstraints();
        buttonGbc.gridwidth = GridBagConstraints.REMAINDER;
        buttonGbc.anchor = GridBagConstraints.CENTER;
        buttonGbc.insets = new Insets(PADDING_INTER_COMPONENT_VERTICAL * 2, 0, 0, 0);
        contentPanel.add(getStartedButton, buttonGbc);

        add(contentPanel, new GridBagConstraints());
    }
}