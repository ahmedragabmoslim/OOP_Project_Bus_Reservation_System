package busbooking.ui;

import busbooking.Bus;
import busbooking.Booking;
import busbooking.Trip;
import busbooking.Traveler;
import busbooking.service.BusService;
import busbooking.service.BookingService;
import busbooking.ui.utils.UIConstants;
import busbooking.Utils;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SeatSelectionPanel extends JPanel implements UIConstants, UpdatablePanel {
    private BusBookingGUI mainApp;
    private Trip currentTrip;
    private Bus currentBus;
    private BusService busService;
    private BookingService bookingService;

    private JLabel tripInfoLabel;
    private JPanel seatGridPanel;
    private JLabel selectedSeatsLabel;
    private JLabel totalPriceLabel;
    private JButton proceedButton;
    private ImageIcon returnIcon;
    private List<JToggleButton> seatButtons;
    private List<String> currentSelectedSeatNumbers;

    private static final int SEATS_PER_ROW = 4;
    private static final int AISLE_AFTER_COLUMN = 2;
    private static final int MAX_ROWS_DISPLAY = 15;

    public SeatSelectionPanel(BusBookingGUI mainApp) {
        this.mainApp = mainApp;
        setName(BusBookingGUI.SEAT_SELECTION_PANEL);
        this.busService = new BusService();
        this.bookingService = new BookingService();
        this.seatButtons = new ArrayList<>();
        this.currentSelectedSeatNumbers = new ArrayList<>();
        loadIcons();
        initComponents();
    }

    private void loadIcons() {
        try {
            URL returnIconUrl = getClass().getResource("/busbooking/ui/utils/icons/return.png");
            if (returnIconUrl != null) {
                returnIcon = new ImageIcon(new ImageIcon(returnIconUrl).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
            }
        } catch (Exception e) {
            System.err.println("SeatSelectionPanel: Error loading icons: " + e.getMessage());
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(0,0,PADDING_COMPONENT_DEFAULT,0));

        JPanel topPanel = new JPanel(new BorderLayout(10,0));
        topPanel.setBackground(COLOR_HEADER_BACKGROUND);
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, PADDING_COMPONENT_DEFAULT, 8, PADDING_COMPONENT_DEFAULT));

        JButton backButton = new JButton(returnIcon != null ? "" : "< Back");
        if(returnIcon != null) backButton.setIcon(returnIcon);
        backButton.setToolTipText("Back to Search Results");
        backButton.setOpaque(false); backButton.setContentAreaFilled(false); backButton.setBorderPainted(false);
        backButton.setFont(FONT_BUTTON_SECONDARY);
        if(returnIcon == null) backButton.setForeground(COLOR_TEXT_ON_PRIMARY_ACTION);
        backButton.setFocusPainted(false); backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> mainApp.showPanel(BusBookingGUI.SEARCH_RESULTS_PANEL));
        topPanel.add(backButton, BorderLayout.WEST);

        tripInfoLabel = new JLabel("Select Your Seats", SwingConstants.CENTER);
        tripInfoLabel.setFont(FONT_HEADING_H3); tripInfoLabel.setForeground(COLOR_TEXT_ON_PRIMARY_ACTION);
        topPanel.add(tripInfoLabel, BorderLayout.CENTER);
        topPanel.add(Box.createHorizontalStrut(backButton.getPreferredSize().width > 0 ? backButton.getPreferredSize().width : 50), BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        seatGridPanel = new JPanel();
        seatGridPanel.setBackground(Color.WHITE);
        seatGridPanel.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(COLOR_BORDER_LIGHT, 1), "Bus Layout",
                javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP,
                FONT_LABEL_BOLD, COLOR_TEXT_DARK_SECONDARY
        ));
        JScrollPane seatScrollPane = new JScrollPane(seatGridPanel);
        seatScrollPane.setBorder(BorderFactory.createEmptyBorder(PADDING_COMPONENT_DEFAULT, PADDING_COMPONENT_DEFAULT, PADDING_COMPONENT_DEFAULT, PADDING_COMPONENT_DEFAULT));
        seatScrollPane.getVerticalScrollBar().setUnitIncrement(10);
        add(seatScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false); bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, PADDING_COMPONENT_DEFAULT, 10, PADDING_COMPONENT_DEFAULT));
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS)); infoPanel.setOpaque(false);
        selectedSeatsLabel = new JLabel("Selected Seats: None");
        selectedSeatsLabel.setFont(FONT_BODY_PLAIN); selectedSeatsLabel.setForeground(COLOR_TEXT_DARK_SECONDARY);
        infoPanel.add(selectedSeatsLabel);
        totalPriceLabel = new JLabel("Total Price: $0.00");
        totalPriceLabel.setFont(FONT_BODY_BOLD); totalPriceLabel.setForeground(COLOR_PRIMARY_ACTION);
        infoPanel.add(totalPriceLabel);
        bottomPanel.add(infoPanel, BorderLayout.CENTER);
        proceedButton = new JButton("Proceed to Book");
        proceedButton.setFont(FONT_BUTTON_PRIMARY); proceedButton.setBackground(COLOR_PRIMARY_ACTION); proceedButton.setForeground(COLOR_BUTTON_TEXT_WHITE);
        proceedButton.setBorder(BORDER_BUTTON_PRIMARY); proceedButton.setEnabled(false);
        proceedButton.addActionListener(e -> processBooking());
        bottomPanel.add(proceedButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void createSeatGrid() {
        seatGridPanel.removeAll(); seatButtons.clear();

        if (currentTrip == null || currentBus == null) {
            seatGridPanel.add(new JLabel("Error: Trip or Bus details not available."));
            updateSelectionInfoDisplay(); return;
        }

        int totalSeatsOnBus = currentBus.getTotalSeats();
        int alreadyBookedCount = bookingService.getTotalTicketsBookedForTrip(currentTrip.getTripId());
        int numRows = (int) Math.ceil((double) totalSeatsOnBus / SEATS_PER_ROW);
        if (numRows > MAX_ROWS_DISPLAY) numRows = MAX_ROWS_DISPLAY;

        seatGridPanel.setLayout(new GridLayout(numRows, SEATS_PER_ROW + 1, 5, 5));
        List<String> simulatedBookedSeatIds = new ArrayList<>();
        for (int i = 0; i < alreadyBookedCount && i < totalSeatsOnBus; i++) {
            int r = i / SEATS_PER_ROW; int c = i % SEATS_PER_ROW;
            simulatedBookedSeatIds.add(generateSeatId(r, c));
        }

        int seatCounter = 0;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < SEATS_PER_ROW + 1; j++) {
                if (j == AISLE_AFTER_COLUMN) { seatGridPanel.add(new JLabel("")); }
                else {
                    if (seatCounter < totalSeatsOnBus) {
                        String seatId = generateSeatId(i, j < AISLE_AFTER_COLUMN ? j : j - 1);
                        JToggleButton seatButton = new JToggleButton(seatId);
                        seatButton.setFocusPainted(false); seatButton.setMargin(new Insets(5,5,5,5));
                        seatButton.setFont(FONT_LABEL_PLAIN.deriveFont(11f));
                        if (simulatedBookedSeatIds.contains(seatId)) {
                            seatButton.setBackground(Color.DARK_GRAY); seatButton.setForeground(Color.LIGHT_GRAY);
                            seatButton.setText("X"); seatButton.setEnabled(false);
                        } else {
                            seatButton.setBackground(new Color(0x90EE90)); seatButton.setForeground(Color.BLACK);
                            seatButton.addItemListener(new SeatSelectionListener(seatId));
                        }
                        seatButtons.add(seatButton); seatGridPanel.add(seatButton);
                        seatCounter++;
                    } else { seatGridPanel.add(new JLabel("")); }
                }
            }
        }
        updateSelectionInfoDisplay();
        seatGridPanel.revalidate(); seatGridPanel.repaint();
    }

    private String generateSeatId(int rowIndex, int colIndexInRow) {
        char rowChar = (char) ('A' + rowIndex);
        return String.valueOf(rowChar) + (colIndexInRow + 1);
    }

    private class SeatSelectionListener implements ItemListener {
        private String seatId;
        public SeatSelectionListener(String seatId) { this.seatId = seatId; }
        @Override
        public void itemStateChanged(ItemEvent e) {
            JToggleButton source = (JToggleButton) e.getSource();
            if (source.isSelected()) {
                source.setBackground(COLOR_PRIMARY_ACTION); source.setForeground(COLOR_BUTTON_TEXT_WHITE);
                if (!currentSelectedSeatNumbers.contains(seatId)) currentSelectedSeatNumbers.add(seatId);
            } else {
                source.setBackground(new Color(0x90EE90)); source.setForeground(Color.BLACK);
                currentSelectedSeatNumbers.remove(seatId);
            }
            updateSelectionInfoDisplay();
        }
    }

    private void updateSelectionInfoDisplay() {
        if (currentSelectedSeatNumbers.isEmpty()) {
            selectedSeatsLabel.setText("Selected Seats: None");
            totalPriceLabel.setText("Total Price: $0.00");
            proceedButton.setEnabled(false);
        } else {
            Collections.sort(currentSelectedSeatNumbers);
            selectedSeatsLabel.setText("Selected Seats: " + String.join(", ", currentSelectedSeatNumbers));
            if (currentTrip != null && currentTrip.getPrice() != null) {
                BigDecimal totalPrice = currentTrip.getPrice().multiply(BigDecimal.valueOf(currentSelectedSeatNumbers.size()));
                totalPriceLabel.setText(String.format("Total Price: $%.2f", totalPrice));
            }
            proceedButton.setEnabled(true);
        }
    }

    private void processBooking() {
        if (currentSelectedSeatNumbers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one seat.", "No Seats Selected", JOptionPane.WARNING_MESSAGE); return;
        }
        Traveler currentUser = mainApp.getCurrentTraveler();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "User not logged in. Please login again.", "Error", JOptionPane.ERROR_MESSAGE);
            mainApp.showPanel(BusBookingGUI.LOGIN_PANEL); return;
        }

        Booking bookingMade = bookingService.createBooking(currentUser, currentTrip, currentSelectedSeatNumbers);
        if (bookingMade != null) {
            mainApp.setLastConfirmedBookingDetails(bookingMade, new ArrayList<>(currentSelectedSeatNumbers));
            mainApp.showPanel(BusBookingGUI.BOOKING_CONFIRMATION_PANEL);
        } else {
            JOptionPane.showMessageDialog(this, "Booking failed. Please try again or contact support.", "Booking Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void updatePanelData() {
        this.currentTrip = mainApp.getSelectedTripForBooking();
        // Clear previous selections when new trip data is loaded
        if (currentSelectedSeatNumbers != null) currentSelectedSeatNumbers.clear();

        if (this.currentTrip != null) {
            this.currentBus = busService.getBusByNumber(this.currentTrip.getBusNumber());
            if (this.currentBus != null) {
                tripInfoLabel.setText("Select Seats for: " + currentBus.getModel() + " (" + currentTrip.getDepartureStation() + " → " + currentTrip.getArrivalStation() + ")");
            } else { tripInfoLabel.setText("Select Seats (Bus details unavailable)"); }
            createSeatGrid();
        } else {
            tripInfoLabel.setText("No Trip Selected");
            seatGridPanel.removeAll();
            seatGridPanel.add(new JLabel("Please select a trip first."));
            seatGridPanel.revalidate(); seatGridPanel.repaint();
            updateSelectionInfoDisplay();
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        updatePanelData();
    }
}