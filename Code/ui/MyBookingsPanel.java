package busbooking.ui;

import busbooking.Booking;
import busbooking.Traveler;
import busbooking.Trip;
import busbooking.service.BookingService;
import busbooking.ui.utils.UIConstants;
import busbooking.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MyBookingsPanel extends JPanel implements UIConstants, UpdatablePanel {
    private BusBookingGUI mainApp;
    private BookingService bookingService;
    private JPanel bookingsDisplayPanel;
    private JScrollPane scrollPane;
    private JLabel headerLabel;
    private ImageIcon returnIcon;
    private ImageIcon ticketIcon;

    public MyBookingsPanel(BusBookingGUI mainApp) {
        this.mainApp = mainApp;
        setName(BusBookingGUI.MY_BOOKINGS_PANEL);
        this.bookingService = new BookingService();
        loadIcons();
        initComponents();
    }

    private void loadIcons() {
        try {
            URL returnIconUrl = getClass().getResource("/busbooking/ui/utils/icons/return.png");
            if (returnIconUrl != null) returnIcon = new ImageIcon(new ImageIcon(returnIconUrl).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
            URL ticketIconUrl = getClass().getResource("/busbooking/ui/utils/icons/finish.png");
            if (ticketIconUrl != null) ticketIcon = new ImageIcon(new ImageIcon(ticketIconUrl).getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        } catch (Exception e) { System.err.println("MyBookingsPanel: Error loading icons: " + e.getMessage()); }
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, PADDING_INTER_COMPONENT_VERTICAL / 2));
        setBackground(COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(0, 0, PADDING_COMPONENT_DEFAULT,0));

        JPanel topPanel = new JPanel(new BorderLayout(10,0));
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
        headerLabel = new JLabel("My Bookings", SwingConstants.CENTER);
        headerLabel.setFont(FONT_HEADING_H2); headerLabel.setForeground(COLOR_TEXT_ON_PRIMARY_ACTION);
        topPanel.add(headerLabel, BorderLayout.CENTER);
        topPanel.add(Box.createHorizontalStrut(backButton.getPreferredSize().width > 0 ? backButton.getPreferredSize().width : 50), BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        bookingsDisplayPanel = new JPanel();
        bookingsDisplayPanel.setLayout(new BoxLayout(bookingsDisplayPanel, BoxLayout.Y_AXIS));
        bookingsDisplayPanel.setBackground(COLOR_BACKGROUND);
        bookingsDisplayPanel.setBorder(BorderFactory.createEmptyBorder(PADDING_COMPONENT_DEFAULT, PADDING_COMPONENT_DEFAULT, PADDING_COMPONENT_DEFAULT, PADDING_COMPONENT_DEFAULT));
        scrollPane = new JScrollPane(bookingsDisplayPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private Component createCenteredMessageLabel(String message) {
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(FONT_BODY_PLAIN); label.setForeground(COLOR_TEXT_LIGHT_PRIMARY);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false); wrapper.setBorder(new EmptyBorder(50,0,0,0));
        wrapper.add(label, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createBookingCard(Booking booking) {
        JPanel card = new JPanel(new BorderLayout(15, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        card.setMinimumSize(new Dimension(350, 130));
        card.setPreferredSize(new Dimension(450, 140));

        if (ticketIcon != null) {
            JLabel iconLabel = new JLabel(ticketIcon);
            iconLabel.setVerticalAlignment(SwingConstants.TOP); iconLabel.setBorder(new EmptyBorder(5,0,0,0));
            card.add(iconLabel, BorderLayout.WEST);
        }

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS)); detailsPanel.setOpaque(false);
        Trip trip = booking.getAssociatedTrip();
        if (trip != null) {
            JLabel tripRouteLabel = new JLabel(trip.getDepartureStation() + " → " + trip.getArrivalStation());
            tripRouteLabel.setFont(FONT_BODY_BOLD.deriveFont(18f)); tripRouteLabel.setForeground(COLOR_TEXT_DARK_PRIMARY);
            detailsPanel.add(tripRouteLabel);
            String depDateTimeStr = Utils.formatDate(trip.getDepartureDate()) + " at " + (trip.getDepartureTime() != null ? Utils.formatTime(trip.getDepartureTime()) : "N/A");
            JLabel tripDateTimeLabel = new JLabel("Departs: " + depDateTimeStr);
            tripDateTimeLabel.setFont(FONT_LABEL_PLAIN); tripDateTimeLabel.setForeground(COLOR_TEXT_LIGHT_PRIMARY);
            detailsPanel.add(tripDateTimeLabel);
            JLabel busInfoLabel = new JLabel("Bus: " + trip.getBusModel() + " (" + trip.getBusNumber() + ")");
            busInfoLabel.setFont(FONT_LABEL_PLAIN); busInfoLabel.setForeground(COLOR_TEXT_LIGHT_PRIMARY);
            detailsPanel.add(busInfoLabel);
        } else { detailsPanel.add(new JLabel("Trip details unavailable (ID: " + booking.getTripId() + ")")); }
        detailsPanel.add(Box.createRigidArea(new Dimension(0,5)));
        JLabel ticketsLabel = new JLabel("Tickets: " + booking.getNumberOfTickets());
        ticketsLabel.setFont(FONT_LABEL_BOLD); ticketsLabel.setForeground(COLOR_TEXT_DARK_SECONDARY);
        detailsPanel.add(ticketsLabel);
        DateTimeFormatter bookingDateFormatter = DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy 'at' hh:mm a");
        JLabel bookingDateLabel = new JLabel("Booked on: " + booking.getBookingDateTime().format(bookingDateFormatter));
        bookingDateLabel.setFont(FONT_LABEL_PLAIN.deriveFont(12f)); bookingDateLabel.setForeground(COLOR_TEXT_LIGHT_SECONDARY);
        detailsPanel.add(bookingDateLabel);
        card.add(detailsPanel, BorderLayout.CENTER);
        return card;
    }

    private void loadUserBookings() {
        bookingsDisplayPanel.removeAll();
        Traveler currentUser = mainApp.getCurrentTraveler();
        if (currentUser == null) {
            bookingsDisplayPanel.add(createCenteredMessageLabel("Please login to view your bookings."));
            bookingsDisplayPanel.revalidate(); bookingsDisplayPanel.repaint(); return;
        }
        List<Booking> userBookings = bookingService.getBookingsForUser(currentUser.getUserId());
        if (userBookings.isEmpty()) bookingsDisplayPanel.add(createCenteredMessageLabel("You have no bookings yet."));
        else {
            for (Booking booking : userBookings) {
                bookingsDisplayPanel.add(createBookingCard(booking));
                bookingsDisplayPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        bookingsDisplayPanel.revalidate(); bookingsDisplayPanel.repaint();
        SwingUtilities.invokeLater(() -> {
            if (scrollPane != null && scrollPane.getVerticalScrollBar() != null) {
                scrollPane.getVerticalScrollBar().setValue(0);
            }
        });
    }

    @Override public void updatePanelData() { loadUserBookings(); }
    @Override public void addNotify() { super.addNotify(); updatePanelData(); }
}