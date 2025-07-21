package busbooking.ui;

import busbooking.Trip;
import busbooking.service.TripService;
import busbooking.ui.utils.UIConstants;
import busbooking.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class SearchResultsPanel extends JPanel implements UIConstants, UpdatablePanel {
    private BusBookingGUI mainApp;
    private TripService tripService;
    private JPanel resultsDisplayPanel;
    private JScrollPane scrollPane;
    private JLabel headerLabel;
    private ImageIcon returnIcon;

    public SearchResultsPanel(BusBookingGUI mainApp) {
        this.mainApp = mainApp;
        setName(BusBookingGUI.SEARCH_RESULTS_PANEL);
        this.tripService = new TripService();
        loadIcons();
        initComponents();
    }

    private void loadIcons() {
        try {
            URL returnIconUrl = getClass().getResource("/busbooking/ui/utils/icons/return.png");
            if (returnIconUrl != null) {
                returnIcon = new ImageIcon(new ImageIcon(returnIconUrl).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
            } else {
                System.err.println("SearchResultsPanel: Couldn't find return.png");
            }
        } catch (Exception e) {
            System.err.println("SearchResultsPanel: Error loading icons: " + e.getMessage());
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, PADDING_INTER_COMPONENT_VERTICAL / 2));
        setBackground(COLOR_BACKGROUND);

        JPanel topPanel = new JPanel(new BorderLayout(10,0));
        topPanel.setBackground(COLOR_HEADER_BACKGROUND);
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, PADDING_COMPONENT_DEFAULT, 8, PADDING_COMPONENT_DEFAULT));

        JButton backButton = new JButton();
        if (returnIcon != null) {
            backButton.setIcon(returnIcon);
        } else {
            backButton.setText("< Back");
            backButton.setForeground(COLOR_TEXT_ON_PRIMARY_ACTION);
        }
        backButton.setFont(FONT_BUTTON_SECONDARY);
        backButton.setOpaque(false); backButton.setContentAreaFilled(false); backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> mainApp.showPanel(BusBookingGUI.HOME_PANEL));
        topPanel.add(backButton, BorderLayout.WEST);

        headerLabel = new JLabel("Search Results", SwingConstants.CENTER);
        headerLabel.setFont(FONT_HEADING_H3);
        headerLabel.setForeground(COLOR_TEXT_ON_PRIMARY_ACTION);
        topPanel.add(headerLabel, BorderLayout.CENTER);
        topPanel.add(Box.createHorizontalStrut(backButton.getPreferredSize().width > 0 ? backButton.getPreferredSize().width : 50), BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        resultsDisplayPanel = new JPanel();
        resultsDisplayPanel.setLayout(new BoxLayout(resultsDisplayPanel, BoxLayout.Y_AXIS));
        resultsDisplayPanel.setBackground(COLOR_BACKGROUND);
        resultsDisplayPanel.setBorder(BorderFactory.createEmptyBorder(PADDING_COMPONENT_DEFAULT, PADDING_COMPONENT_DEFAULT, PADDING_COMPONENT_DEFAULT, PADDING_COMPONENT_DEFAULT));

        scrollPane = new JScrollPane(resultsDisplayPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadSearchResults() {
        String from = mainApp.getLastSearchFrom();
        String to = mainApp.getLastSearchTo();
        LocalDate date = mainApp.getLastSearchDate();

        resultsDisplayPanel.removeAll();

        if (from == null || to == null || date == null) {
            headerLabel.setText("Search Results");
            resultsDisplayPanel.add(createCenteredMessageLabel("Please perform a search from the Home screen."));
            resultsDisplayPanel.revalidate(); resultsDisplayPanel.repaint();
            return;
        }

        headerLabel.setText("Results: " + from + " → " + to + " on " + Utils.formatDate(date));
        List<Trip> trips = tripService.searchTrips(from, to, date);

        if (trips.isEmpty()) {
            resultsDisplayPanel.add(createCenteredMessageLabel("No trips found for your criteria."));
        } else {
            for (Trip trip : trips) {
                resultsDisplayPanel.add(createTripCard(trip));
                resultsDisplayPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        resultsDisplayPanel.revalidate(); resultsDisplayPanel.repaint();
        SwingUtilities.invokeLater(() -> {
            if (scrollPane != null && scrollPane.getVerticalScrollBar() != null) {
                scrollPane.getVerticalScrollBar().setValue(0);
            }
        });
    }

    private Component createCenteredMessageLabel(String message) { // Changed return type
        JLabel noResultsLabel = new JLabel(message, SwingConstants.CENTER);
        noResultsLabel.setFont(FONT_BODY_PLAIN);
        noResultsLabel.setForeground(COLOR_TEXT_LIGHT_PRIMARY);
        noResultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(50,0,0,0));
        wrapper.add(noResultsLabel, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createTripCard(Trip trip) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 135));
        card.setMinimumSize(new Dimension(300, 125));
        card.setPreferredSize(new Dimension(400, 130));

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);

        JLabel busNameLabel = new JLabel((trip.getBusModel() != null ? trip.getBusModel() : "Bus") + " (" + trip.getBusNumber() + ")");
        busNameLabel.setFont(FONT_BODY_BOLD); busNameLabel.setForeground(COLOR_TEXT_DARK_PRIMARY);
        detailsPanel.add(busNameLabel);

        String depTimeStr = (trip.getDepartureTime() != null) ? Utils.formatTime(trip.getDepartureTime()) : "N/A";
        String arrTimeStr = (trip.getArrivalTime() != null) ? Utils.formatTime(trip.getArrivalTime()) : "N/A";
        JLabel timeLabel = new JLabel("Departs: " + depTimeStr + " - Arrives: " + arrTimeStr);
        timeLabel.setFont(FONT_LABEL_PLAIN); timeLabel.setForeground(COLOR_TEXT_LIGHT_PRIMARY);
        detailsPanel.add(timeLabel);

        if (trip.getDepartureTime() != null && trip.getArrivalTime() != null) {
            long durationMinutes = ChronoUnit.MINUTES.between(trip.getDepartureTime(), trip.getArrivalTime());
            if (durationMinutes < 0) durationMinutes += 24 * 60;
            long hours = durationMinutes / 60; long minutes = durationMinutes % 60;
            JLabel durationLabel = new JLabel("Duration: " + hours + "h " + minutes + "m");
            durationLabel.setFont(FONT_LABEL_PLAIN); durationLabel.setForeground(COLOR_TEXT_LIGHT_PRIMARY);
            detailsPanel.add(durationLabel);
        }

        JLabel seatsLabel = new JLabel("Seats Available: " + trip.getTotalSeatsAvailable());
        seatsLabel.setFont(FONT_LABEL_PLAIN); seatsLabel.setForeground(COLOR_TEXT_LIGHT_PRIMARY);
        detailsPanel.add(seatsLabel);
        card.add(detailsPanel, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setOpaque(false); actionPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

        JLabel priceLabel = new JLabel(String.format("$%.2f", trip.getPrice()));
        priceLabel.setFont(FONT_HEADING_H3); priceLabel.setForeground(COLOR_PRIMARY_ACTION);
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        actionPanel.add(priceLabel); actionPanel.add(Box.createRigidArea(new Dimension(0,10)));

        JButton bookButton = new JButton("Book Now");
        bookButton.setFont(FONT_BUTTON_SECONDARY);
        bookButton.setBackground(COLOR_PRIMARY_ACTION);
        bookButton.setForeground(COLOR_BUTTON_TEXT_WHITE);
        bookButton.setFocusPainted(false); bookButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bookButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        Dimension buttonSize = new Dimension(120, 35);
        bookButton.setPreferredSize(buttonSize); bookButton.setMinimumSize(buttonSize); bookButton.setMaximumSize(buttonSize);
        bookButton.addActionListener(e -> {
            mainApp.setSelectedTripForBooking(trip);
            mainApp.showPanel(BusBookingGUI.SEAT_SELECTION_PANEL);
        });
        actionPanel.add(bookButton);
        card.add(actionPanel, BorderLayout.EAST);
        return card;
    }

    @Override
    public void updatePanelData() { loadSearchResults(); }
    @Override
    public void addNotify() { super.addNotify(); updatePanelData(); }
}