package busbooking.ui;

import busbooking.Traveler;
import busbooking.dto.BookingDetailView;
import busbooking.service.BookingService;
import busbooking.ui.utils.UIConstants;
import busbooking.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ViewAllBookingsPanel extends JPanel implements UIConstants, UpdatablePanel {
    private BusBookingGUI mainApp;
    private BookingService bookingService;
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private ImageIcon returnIcon;

    public ViewAllBookingsPanel(BusBookingGUI mainApp) {
        this.mainApp = mainApp;
        setName(BusBookingGUI.VIEW_ALL_BOOKINGS_PANEL);
        this.bookingService = new BookingService();
        loadIcons();
        initComponents();
    }

    private void loadIcons() {
        try {
            URL returnIconUrl = getClass().getResource("/busbooking/ui/utils/icons/return.png");
            if (returnIconUrl != null) returnIcon = new ImageIcon(new ImageIcon(returnIconUrl).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        } catch (Exception e) { System.err.println("ViewAllBookingsPanel: Error loading icons: " + e.getMessage()); }
    }
    private void setColumnWidths(JTable table) {
        TableColumn column;
        column = table.getColumnModel().getColumn(0); column.setPreferredWidth(80);
        column = table.getColumnModel().getColumn(1); column.setPreferredWidth(150);
        column = table.getColumnModel().getColumn(2); column.setPreferredWidth(180);
        column = table.getColumnModel().getColumn(3); column.setPreferredWidth(200);
        column = table.getColumnModel().getColumn(4); column.setPreferredWidth(100);
        column = table.getColumnModel().getColumn(5); column.setPreferredWidth(120); // Bus Info width increased slightly
        column = table.getColumnModel().getColumn(6); column.setPreferredWidth(60);
        column = table.getColumnModel().getColumn(7); column.setPreferredWidth(150);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10)); setBackground(COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(0, 0, PADDING_COMPONENT_DEFAULT, 0));
        JPanel topPanel = new JPanel(new BorderLayout(10,0));
        topPanel.setBackground(COLOR_HEADER_BACKGROUND);
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, PADDING_COMPONENT_DEFAULT, 8, PADDING_COMPONENT_DEFAULT));
        JButton backButton = new JButton(returnIcon != null ? "" : "< Dashboard");
        if(returnIcon != null) backButton.setIcon(returnIcon);
        backButton.setToolTipText("Back to Admin Dashboard");
        backButton.setOpaque(false); backButton.setContentAreaFilled(false); backButton.setBorderPainted(false);
        backButton.setFont(FONT_BUTTON_SECONDARY);
        if(returnIcon == null) backButton.setForeground(COLOR_TEXT_ON_PRIMARY_ACTION);
        backButton.setFocusPainted(false); backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> mainApp.showPanel(BusBookingGUI.ADMIN_DASHBOARD_PANEL));
        topPanel.add(backButton, BorderLayout.WEST);
        JLabel headerLabel = new JLabel("All System Bookings", SwingConstants.CENTER);
        headerLabel.setFont(FONT_HEADING_H2); headerLabel.setForeground(COLOR_TEXT_ON_PRIMARY_ACTION);
        topPanel.add(headerLabel, BorderLayout.CENTER);
        topPanel.add(Box.createHorizontalStrut(backButton.getPreferredSize().width > 0 ? backButton.getPreferredSize().width : 50), BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"Booking ID", "User Name", "User Email", "Trip (From-To)", "Trip Date", "Bus Info", "Tickets", "Booked At"};
        tableModel = new DefaultTableModel(columnNames, 0) { @Override public boolean isCellEditable(int r, int c) { return false; }};
        bookingsTable = new JTable(tableModel);
        bookingsTable.setFont(FONT_BODY_PLAIN.deriveFont(12f)); bookingsTable.getTableHeader().setFont(FONT_LABEL_BOLD.deriveFont(12f));
        bookingsTable.setRowHeight(25); bookingsTable.setFillsViewportHeight(true); bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingsTable.setAutoCreateRowSorter(true); setColumnWidths(bookingsTable);
        JScrollPane scrollPane = new JScrollPane(bookingsTable); add(scrollPane, BorderLayout.CENTER);
    }

    private void loadAllBookingsData() {
        tableModel.setRowCount(0); List<BookingDetailView> bookings = bookingService.getAllBookingsWithDetails();
        DateTimeFormatter bookingDateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"); // Simpler format
        if (bookings.isEmpty()) { System.out.println("ViewAllBookingsPanel: No bookings found."); }
        else {
            for (BookingDetailView bdv : bookings) {
                tableModel.addRow(new Object[]{
                        bdv.getBookingId(), bdv.getUserFullName(), bdv.getUserEmail(),
                        bdv.getTripDepartureStation() + " → " + bdv.getTripArrivalStation(),
                        Utils.formatDate(bdv.getTripDepartureDate()),
                        bdv.getTripBusNumber() + " (" + (bdv.getTripBusModel() != null ? bdv.getTripBusModel() : "N/A") + ")",
                        bdv.getNumberOfTickets(), bdv.getBookingDateTime().format(bookingDateFormatter)
                });
            }
        }
    }
    @Override
    public void updatePanelData() {
        Traveler currentUser = mainApp.getCurrentTraveler();
        if (currentUser == null || !currentUser.isAdmin()) {
            JOptionPane.showMessageDialog(mainApp.getMainFrame(), "Access Denied.", "Error", JOptionPane.ERROR_MESSAGE);
            mainApp.showPanel(currentUser == null ? BusBookingGUI.LOGIN_PANEL : BusBookingGUI.HOME_PANEL);
            return;
        }
        loadAllBookingsData();
    }
    @Override public void addNotify() { super.addNotify(); updatePanelData(); }
}