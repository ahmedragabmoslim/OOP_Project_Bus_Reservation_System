package busbooking.ui;

import busbooking.Booking;
import busbooking.Traveler;
import busbooking.Trip;
import busbooking.ui.utils.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class BusBookingGUI implements UIConstants {

    private JFrame mainFrame;
    private JPanel mainPanelContainer;
    private CardLayout cardLayout;

    private Traveler currentTraveler;

    private String lastSearchFrom;
    private String lastSearchTo;
    private LocalDate lastSearchDate;

    private Trip selectedTripForBooking;
    private Booking lastConfirmedBooking;
    private List<String> lastBookedSeats;

    public static final String WELCOME_PANEL = "WelcomePanel";
    public static final String AUTH_CHOICE_PANEL = "AuthChoicePanel";
    public static final String LOGIN_PANEL = "LoginPanel";
    public static final String SIGNUP_PANEL = "SignUpPanel";
    public static final String HOME_PANEL = "HomePanel";
    public static final String SEARCH_RESULTS_PANEL = "SearchResultsPanel";
    public static final String SEAT_SELECTION_PANEL = "SeatSelectionPanel";
    public static final String MY_BOOKINGS_PANEL = "MyBookingsPanel";
    public static final String BOOKING_CONFIRMATION_PANEL = "BookingConfirmationPanel";
    public static final String PROFILE_PANEL = "ProfilePanel";
    public static final String ADMIN_DASHBOARD_PANEL = "AdminDashboardPanel";
    public static final String MANAGE_BUSES_PANEL = "ManageBusesPanel";
    public static final String MANAGE_TRIPS_PANEL = "ManageTripsPanel";
    public static final String MANAGE_USERS_PANEL = "ManageUsersPanel";
    public static final String VIEW_ALL_BOOKINGS_PANEL = "ViewAllBookingsPanel";


    public BusBookingGUI() {
        mainFrame = new JFrame("Bus Booking System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setMinimumSize(new Dimension(600, 780));
        mainFrame.setSize(750, 820);
        mainFrame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanelContainer = new JPanel(cardLayout);
        mainPanelContainer.setBackground(COLOR_BACKGROUND);

        WelcomePanel welcomePanel = new WelcomePanel(this);
        AuthChoicePanel authChoicePanel = new AuthChoicePanel(this);
        LoginPanel loginPanel = new LoginPanel(this);
        SignUpPanel signUpPanel = new SignUpPanel(this);
        HomePanel homePanel = new HomePanel(this);
        SearchResultsPanel searchResultsPanel = new SearchResultsPanel(this);
        SeatSelectionPanel seatSelectionPanel = new SeatSelectionPanel(this);
        MyBookingsPanel myBookingsPanel = new MyBookingsPanel(this);
        ProfilePanel profilePanel = new ProfilePanel(this); // Using the display-only version
        BookingConfirmationPanel bookingConfirmationPanel = new BookingConfirmationPanel(this);

        AdminDashboardPanel adminDashboardPanel = new AdminDashboardPanel(this);
        ManageBusesPanel manageBusesPanel = new ManageBusesPanel(this);
        ManageTripsPanel manageTripsPanel = new ManageTripsPanel(this);
        ManageUsersPanel manageUsersPanel = new ManageUsersPanel(this);
        ViewAllBookingsPanel viewAllBookingsPanel = new ViewAllBookingsPanel(this);

        mainPanelContainer.add(welcomePanel, WELCOME_PANEL);
        mainPanelContainer.add(authChoicePanel, AUTH_CHOICE_PANEL);
        mainPanelContainer.add(loginPanel, LOGIN_PANEL);
        mainPanelContainer.add(signUpPanel, SIGNUP_PANEL);
        mainPanelContainer.add(homePanel, HOME_PANEL);
        mainPanelContainer.add(searchResultsPanel, SEARCH_RESULTS_PANEL);
        mainPanelContainer.add(seatSelectionPanel, SEAT_SELECTION_PANEL);
        mainPanelContainer.add(myBookingsPanel, MY_BOOKINGS_PANEL);
        mainPanelContainer.add(profilePanel, PROFILE_PANEL);
        mainPanelContainer.add(bookingConfirmationPanel, BOOKING_CONFIRMATION_PANEL);
        mainPanelContainer.add(adminDashboardPanel, ADMIN_DASHBOARD_PANEL);
        mainPanelContainer.add(manageBusesPanel, MANAGE_BUSES_PANEL);
        mainPanelContainer.add(manageTripsPanel, MANAGE_TRIPS_PANEL);
        mainPanelContainer.add(manageUsersPanel, MANAGE_USERS_PANEL);
        mainPanelContainer.add(viewAllBookingsPanel, VIEW_ALL_BOOKINGS_PANEL);

        mainFrame.add(mainPanelContainer);
        showPanel(WELCOME_PANEL);
        mainFrame.setVisible(true);
    }

    public void showPanel(String panelName) {
        Component[] components = mainPanelContainer.getComponents();
        for (Component component : components) {
            if (component.getName() != null && component.getName().equals(panelName)) {
                if (component instanceof LoginPanel) {
                    ((LoginPanel) component).clearFormOnDisplay();
                } else if (component instanceof SignUpPanel) {
                    ((SignUpPanel) component).clearFormOnDisplay();
                } else if (component instanceof UpdatablePanel) {
                    ((UpdatablePanel) component).updatePanelData();
                }
                break;
            }
        }
        cardLayout.show(mainPanelContainer, panelName);
    }

    public JFrame getMainFrame() { return mainFrame; }
    public Traveler getCurrentTraveler() { return currentTraveler; }
    public void setCurrentTraveler(Traveler traveler) { this.currentTraveler = traveler; }
    public void setLastSearchCriteria(String from, String to, LocalDate date) {
        this.lastSearchFrom = from; this.lastSearchTo = to; this.lastSearchDate = date;
    }
    public String getLastSearchFrom() { return lastSearchFrom; }
    public String getLastSearchTo() { return lastSearchTo; }
    public LocalDate getLastSearchDate() { return lastSearchDate; }
    public void setSelectedTripForBooking(Trip trip) { this.selectedTripForBooking = trip; }
    public Trip getSelectedTripForBooking() { return selectedTripForBooking; }
    public void setLastConfirmedBookingDetails(Booking booking, List<String> seats) {
        this.lastConfirmedBooking = booking; this.lastBookedSeats = seats;
    }
    public Booking getLastConfirmedBooking() { return lastConfirmedBooking; }
    public List<String> getLastBookedSeats() { return lastBookedSeats; }
    public JPanel getPanelByName(String panelName) {
        for (Component comp : mainPanelContainer.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(panelName)) {
                return (JPanel) comp;
            }
        }
        System.err.println("Warning: Panel with name '" + panelName + "' not found.");
        return null;
    }
    public JPanel getCurrentVisiblePanel() {
        for (Component comp : mainPanelContainer.getComponents()) {
            if (comp.isVisible() && comp instanceof JPanel) { return (JPanel) comp; }
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            boolean foundModernLaf = false;
            try {
                Class.forName("com.formdev.flatlaf.FlatLightLaf");
                UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
                foundModernLaf = true;
            } catch (Exception e) { /* FlatLaf not available */ }

            if (!foundModernLaf) {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        foundModernLaf = true;
                        break;
                    }
                }
            }
            if (!foundModernLaf) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (Exception e) {
            System.err.println("Failed to set a preferred Look and Feel: " + e.getMessage());
        }
        SwingUtilities.invokeLater(() -> new BusBookingGUI());
    }
}