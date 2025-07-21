package busbooking.ui;

import busbooking.Booking;
import busbooking.Trip;
import busbooking.ui.utils.UIConstants;
import busbooking.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookingConfirmationPanel extends JPanel implements UIConstants, UpdatablePanel {
    private BusBookingGUI mainApp;
    private Booking confirmedBooking;
    private List<String> bookedSeats;

    private JLabel bookingIdLabel, tripInfoLabel, dateTimeLabel, busInfoLabel;
    private JLabel seatsLabel, numTicketsLabel, totalPriceLabelDisplay, bookedOnLabel;
    private ImageIcon successIcon;

    public BookingConfirmationPanel(BusBookingGUI mainApp) {
        this.mainApp = mainApp;
        setName(BusBookingGUI.BOOKING_CONFIRMATION_PANEL);
        loadIcons();
        initComponents();
    }

    private void loadIcons() {
        try {
            URL successIconUrl = getClass().getResource("/busbooking/ui/utils/icons/finish.png");
            if (successIconUrl != null) {
                successIcon = new ImageIcon(new ImageIcon(successIconUrl).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
            }
        } catch (Exception e) { System.err.println("BookingConfirmationPanel: Error loading finish.png: " + e.getMessage()); }
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 15));
        setBackground(COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(PADDING_COMPONENT_DEFAULT, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));

        JPanel successMessagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        successMessagePanel.setBackground(new Color(0xE6FFFA));
        successMessagePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,1,0, COLOR_BORDER_LIGHT),
                BorderFactory.createEmptyBorder(20,10,20,10)
        ));
        if (successIcon != null) successMessagePanel.add(new JLabel(successIcon));
        JLabel successLabel = new JLabel("Booking Successful!");
        successLabel.setFont(FONT_HEADING_H1.deriveFont(28f)); successLabel.setForeground(COLOR_SUCCESS_GREEN);
        successMessagePanel.add(successLabel);
        add(successMessagePanel, BorderLayout.NORTH);

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_BORDER_LIGHT), "Booking Summary",
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
                        FONT_HEADING_H3, COLOR_TEXT_DARK_PRIMARY),
                new EmptyBorder(20, 20, 20, 20)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = GridBagConstraints.RELATIVE; gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.3; gbc.insets = new Insets(8, 0, 8, 10);

        bookingIdLabel = addDetailRow(detailsPanel, gbc, "Booking ID:");
        tripInfoLabel = addDetailRow(detailsPanel, gbc, "Trip:");
        dateTimeLabel = addDetailRow(detailsPanel, gbc, "Departure:");
        busInfoLabel = addDetailRow(detailsPanel, gbc, "Bus:");
        seatsLabel = addDetailRow(detailsPanel, gbc, "Seats:");
        numTicketsLabel = addDetailRow(detailsPanel, gbc, "Tickets:");
        totalPriceLabelDisplay = addDetailRow(detailsPanel, gbc, "Total Price:");
        bookedOnLabel = addDetailRow(detailsPanel, gbc, "Booked On:");
        GridBagConstraints gbcSpacer = new GridBagConstraints();
        gbcSpacer.gridx = 0; gbcSpacer.gridy = GridBagConstraints.RELATIVE; gbcSpacer.gridwidth = 2;
        gbcSpacer.weighty = 1.0; detailsPanel.add(Box.createGlue(), gbcSpacer);
        add(new JScrollPane(detailsPanel), BorderLayout.CENTER);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        actionsPanel.setOpaque(false); actionsPanel.setBorder(new EmptyBorder(25, 0, 10, 0));
        JButton viewMyBookingsButton = new JButton("View My Bookings");
        viewMyBookingsButton.setFont(FONT_BUTTON_PRIMARY); viewMyBookingsButton.setBackground(COLOR_PRIMARY_ACTION);
        viewMyBookingsButton.setForeground(COLOR_BUTTON_TEXT_WHITE); viewMyBookingsButton.setBorder(BORDER_BUTTON_PRIMARY);
        viewMyBookingsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewMyBookingsButton.addActionListener(e -> mainApp.showPanel(BusBookingGUI.MY_BOOKINGS_PANEL));
        actionsPanel.add(viewMyBookingsButton);
        JButton bookAnotherButton = new JButton("Book Another Trip");
        bookAnotherButton.setFont(FONT_BUTTON_SECONDARY); bookAnotherButton.setBackground(COLOR_BACKGROUND);
        bookAnotherButton.setForeground(COLOR_PRIMARY_ACTION);
        bookAnotherButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARY_ACTION, 2),
                BorderFactory.createEmptyBorder(PADDING_COMPONENT_DEFAULT-2, PADDING_COMPONENT_DEFAULT*2-2, PADDING_COMPONENT_DEFAULT-2, PADDING_COMPONENT_DEFAULT*2-2)));
        bookAnotherButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bookAnotherButton.addActionListener(e -> mainApp.showPanel(BusBookingGUI.HOME_PANEL));
        actionsPanel.add(bookAnotherButton);
        add(actionsPanel, BorderLayout.SOUTH);
    }

    private JLabel addDetailRow(JPanel parent, GridBagConstraints gbcTemplate, String labelText) {
        JLabel fieldLabel = new JLabel(labelText);
        fieldLabel.setFont(FONT_LABEL_BOLD); fieldLabel.setForeground(COLOR_TEXT_DARK_SECONDARY);
        parent.add(fieldLabel, gbcTemplate);
        GridBagConstraints gbcValue = (GridBagConstraints) gbcTemplate.clone();
        gbcValue.gridx = 1; gbcValue.weightx = 0.7; gbcValue.anchor = GridBagConstraints.WEST;
        gbcValue.fill = GridBagConstraints.HORIZONTAL; gbcValue.insets = new Insets(8, 0, 8, 0);
        JLabel valueLabel = new JLabel("-");
        valueLabel.setFont(FONT_BODY_PLAIN.deriveFont(15f)); valueLabel.setForeground(COLOR_TEXT_DARK_PRIMARY);
        parent.add(valueLabel, gbcValue);
        return valueLabel;
    }

    @Override
    public void updatePanelData() {
        this.confirmedBooking = mainApp.getLastConfirmedBooking();
        this.bookedSeats = mainApp.getLastBookedSeats();
        if (confirmedBooking != null && confirmedBooking.getAssociatedTrip() != null) {
            Trip trip = confirmedBooking.getAssociatedTrip();
            DateTimeFormatter bookingDateFormatter = DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy 'at' hh:mm a");
            bookingIdLabel.setText(String.valueOf(confirmedBooking.getBookingId()));
            tripInfoLabel.setText(trip.getDepartureStation() + " → " + trip.getArrivalStation());
            String depDateTimeStr = Utils.formatDate(trip.getDepartureDate()) + " at " + (trip.getDepartureTime() != null ? Utils.formatTime(trip.getDepartureTime()) : "N/A");
            dateTimeLabel.setText(depDateTimeStr);
            busInfoLabel.setText(trip.getBusModel() + " (" + trip.getBusNumber() + ")");
            if (bookedSeats != null && !bookedSeats.isEmpty()) seatsLabel.setText(String.join(", ", bookedSeats));
            else seatsLabel.setText("N/A");
            numTicketsLabel.setText(String.valueOf(confirmedBooking.getNumberOfTickets()));
            if (trip.getPrice() != null) totalPriceLabelDisplay.setText(String.format("$%.2f", trip.getPrice().multiply(new java.math.BigDecimal(confirmedBooking.getNumberOfTickets()))));
            else totalPriceLabelDisplay.setText("$N/A");
            bookedOnLabel.setText(confirmedBooking.getBookingDateTime().format(bookingDateFormatter));
        } else {
            bookingIdLabel.setText("Error: No booking data."); tripInfoLabel.setText("-"); dateTimeLabel.setText("-"); busInfoLabel.setText("-");
            seatsLabel.setText("-"); numTicketsLabel.setText("-"); totalPriceLabelDisplay.setText("-"); bookedOnLabel.setText("-");
        }
    }

    @Override
    public void addNotify() { super.addNotify(); updatePanelData(); }
}