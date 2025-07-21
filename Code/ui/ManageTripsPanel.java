package busbooking.ui;

import busbooking.Bus;
import busbooking.Traveler;
import busbooking.Trip;
import busbooking.service.BusService;
import busbooking.service.TripService;
import busbooking.ui.utils.UIConstants;
import busbooking.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Vector;

public class ManageTripsPanel extends JPanel implements UIConstants, UpdatablePanel {
    private BusBookingGUI mainApp;
    private TripService tripService;
    private BusService busService;

    private JTextField departureStationField, arrivalStationField, priceField;
    private JFormattedTextField departureDateField, departureTimeField, arrivalTimeField;
    private JComboBox<BusItem> busComboBox;
    private JTextField tripSeatsFieldReadOnly;

    private JButton addOrUpdateButton, clearFormButton, loadForEditButton, deleteTripButton;
    private JTable tripsTable;
    private DefaultTableModel tableModel;
    private ImageIcon returnIcon;

    private static final String PLACEHOLDER_STATION = "e.g., Cairo";
    private static final String PLACEHOLDER_PRICE = "e.g., 150.00";

    private boolean editMode = false;
    private int editingTripId = -1;

    private static class BusItem {
        String busNumber; String display; int totalSeats;
        BusItem(String busNumber, String model, int totalSeats) {
            this.busNumber = busNumber; this.totalSeats = totalSeats;
            this.display = busNumber + " (" + model + " - " + totalSeats + " seats)";
        }
        public String getBusNumber() { return busNumber; }
        public int getTotalSeats() { return totalSeats; }
        @Override public String toString() { return display; }
        @Override public boolean equals(Object obj) { if (this==obj) return true; if (obj==null || getClass()!=obj.getClass()) return false; return busNumber.equals(((BusItem)obj).busNumber); }
        @Override public int hashCode() { return busNumber.hashCode(); }
    }

    public ManageTripsPanel(BusBookingGUI mainApp) {
        this.mainApp = mainApp; setName(BusBookingGUI.MANAGE_TRIPS_PANEL);
        this.tripService = new TripService(); this.busService = new BusService();
        loadIcons(); initComponents();
    }

    private void loadIcons() { 
        try {
            URL returnIconUrl = getClass().getResource("/busbooking/ui/utils/icons/return.png");
            if (returnIconUrl != null) {
                returnIcon = new ImageIcon(new ImageIcon(returnIconUrl).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
            }
        } catch (Exception e) {
            System.err.println("ManageTripsPanel: Error loading icons: " + e.getMessage());
        }
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
    private void applyFormatSpecificPlaceholder(JFormattedTextField field, String placeholder) { 
        field.setText(placeholder);
        field.setForeground(COLOR_TEXT_LIGHT_SECONDARY);
        field.setFont(FONT_TEXTFIELD);
        field.setBorder(BORDER_TEXTFIELD_DEFAULT);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder) && field.getForeground().equals(COLOR_TEXT_LIGHT_SECONDARY)) {
                    field.setText("");
                    field.setForeground(COLOR_TEXT_DARK_PRIMARY);
                }
                field.setBorder(BORDER_TEXTFIELD_FOCUSED);
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(COLOR_TEXT_LIGHT_SECONDARY);
                }
                field.setBorder(BORDER_TEXTFIELD_DEFAULT);
            }
        });
    }
    private JLabel createSmallLabel(String text) { 
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL_BOLD);
        label.setForeground(COLOR_TEXT_DARK_SECONDARY);
        return label;
    }
    private JTextField createTextFieldWithPlaceholder(String placeholder, int columns) { 
        JTextField textField = new JTextField(columns);
        textField.setFont(FONT_TEXTFIELD);
        textField.setBorder(BORDER_TEXTFIELD_DEFAULT);

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                removePlaceholder(textField, placeholder);
                textField.setBorder(BORDER_TEXTFIELD_FOCUSED);
            }
            @Override
            public void focusLost(FocusEvent evt) {
                applyPlaceholder(textField, placeholder);
                textField.setBorder(BORDER_TEXTFIELD_DEFAULT);
            }
        });
        return textField;
    }
    private void setupTable() { /* ... (same as ManageBusesPanel, but for tripsTable) ... */
        tripsTable.setFont(FONT_BODY_PLAIN.deriveFont(12f)); tripsTable.getTableHeader().setFont(FONT_LABEL_BOLD.deriveFont(12f));
        tripsTable.setRowHeight(22); tripsTable.setFillsViewportHeight(true); tripsTable.setAutoCreateRowSorter(true);
        tripsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tripsTable.getSelectionModel().addListSelectionListener(e -> {
            boolean rowSelected = tripsTable.getSelectedRow() != -1;
            loadForEditButton.setEnabled(rowSelected);
            deleteTripButton.setEnabled(rowSelected);
        });
        tripsTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        tripsTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        tripsTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        tripsTable.getColumnModel().getColumn(5).setPreferredWidth(60);
        tripsTable.getColumnModel().getColumn(8).setPreferredWidth(60);
        tripsTable.getColumnModel().getColumn(9).setPreferredWidth(50);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10,10)); setBackground(COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(0,0,PADDING_COMPONENT_DEFAULT,0));
        JPanel topPanel = new JPanel(new BorderLayout(10,0));
        topPanel.setBackground(COLOR_HEADER_BACKGROUND); topPanel.setBorder(BorderFactory.createEmptyBorder(8,PADDING_COMPONENT_DEFAULT,8,PADDING_COMPONENT_DEFAULT));
        JButton backButton = new JButton(returnIcon != null ? "" : "< Dashboard");
        if(returnIcon != null) backButton.setIcon(returnIcon);
        backButton.setToolTipText("Back to Admin Dashboard");
        backButton.setOpaque(false); backButton.setContentAreaFilled(false); backButton.setBorderPainted(false);
        backButton.setFont(FONT_BUTTON_SECONDARY); if(returnIcon == null) backButton.setForeground(COLOR_TEXT_ON_PRIMARY_ACTION);
        backButton.setFocusPainted(false); backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> mainApp.showPanel(BusBookingGUI.ADMIN_DASHBOARD_PANEL));
        topPanel.add(backButton, BorderLayout.WEST);
        JLabel headerLabel = new JLabel("Manage Trips", SwingConstants.CENTER);
        headerLabel.setFont(FONT_HEADING_H2); headerLabel.setForeground(COLOR_TEXT_ON_PRIMARY_ACTION);
        topPanel.add(headerLabel, BorderLayout.CENTER);
        topPanel.add(Box.createHorizontalStrut(backButton.getPreferredSize().width > 0 ? backButton.getPreferredSize().width : 50), BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false); formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_BORDER_LIGHT), "Trip Details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, FONT_HEADING_H3, COLOR_TEXT_DARK_PRIMARY));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3,5,3,5); gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx=0; gbc.gridy=0; gbc.weightx=0.1; formPanel.add(createSmallLabel("Departure Station:"), gbc);
        gbc.gridx=1; gbc.weightx=0.4; departureStationField = createTextFieldWithPlaceholder(PLACEHOLDER_STATION,15); formPanel.add(departureStationField, gbc);
        gbc.gridx=2; gbc.weightx=0.1; formPanel.add(createSmallLabel("Arrival Station:"), gbc);
        gbc.gridx=3; gbc.weightx=0.4; arrivalStationField = createTextFieldWithPlaceholder(PLACEHOLDER_STATION,15); formPanel.add(arrivalStationField, gbc);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        departureDateField = new JFormattedTextField(new DefaultFormatterFactory(new DateFormatter(dateFormat)));
        departureDateField.setToolTipText("YYYY-MM-DD"); departureDateField.setColumns(10); applyFormatSpecificPlaceholder(departureDateField, LocalDate.now().plusDays(1).format(Utils.DATE_FORMATTER));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        departureTimeField = new JFormattedTextField(new DefaultFormatterFactory(new DateFormatter(timeFormat)));
        departureTimeField.setToolTipText("HH:MM"); departureTimeField.setColumns(5); applyFormatSpecificPlaceholder(departureTimeField, "10:00");
        arrivalTimeField = new JFormattedTextField(new DefaultFormatterFactory(new DateFormatter(timeFormat)));
        arrivalTimeField.setToolTipText("HH:MM"); arrivalTimeField.setColumns(5); applyFormatSpecificPlaceholder(arrivalTimeField, "12:00");
        gbc.gridx=0; gbc.gridy=1; gbc.weightx=0.1; formPanel.add(createSmallLabel("Departure Date:"), gbc);
        gbc.gridx=1; gbc.weightx=0.4; formPanel.add(departureDateField, gbc);
        gbc.gridx=2; gbc.weightx=0.1; formPanel.add(createSmallLabel("Departure Time:"), gbc);
        gbc.gridx=3; gbc.weightx=0.4; formPanel.add(departureTimeField, gbc);
        gbc.gridx=0; gbc.gridy=2; gbc.weightx=0.1; formPanel.add(createSmallLabel("Arrival Time (Opt.):"), gbc);
        gbc.gridx=1; gbc.weightx=0.4; formPanel.add(arrivalTimeField, gbc);
        gbc.gridx=2; gbc.weightx=0.1; formPanel.add(createSmallLabel("Bus:"), gbc);
        gbc.gridx=3; gbc.weightx=0.4; busComboBox = new JComboBox<>(); busComboBox.setFont(FONT_TEXTFIELD); busComboBox.addActionListener(e->updateTripSeatsField()); formPanel.add(busComboBox, gbc);
        gbc.gridx=0; gbc.gridy=3; gbc.weightx=0.1; formPanel.add(createSmallLabel("Price:"), gbc);
        gbc.gridx=1; gbc.weightx=0.4; priceField = createTextFieldWithPlaceholder(PLACEHOLDER_PRICE,8); formPanel.add(priceField, gbc);
        gbc.gridx=2; gbc.weightx=0.1; formPanel.add(createSmallLabel("Trip Seats:"), gbc);
        gbc.gridx=3; gbc.weightx=0.4; tripSeatsFieldReadOnly = new JTextField(5); tripSeatsFieldReadOnly.setFont(FONT_TEXTFIELD); tripSeatsFieldReadOnly.setEditable(false); tripSeatsFieldReadOnly.setBackground(new Color(0xF0F0F0)); tripSeatsFieldReadOnly.setBorder(BORDER_TEXTFIELD_DEFAULT); formPanel.add(tripSeatsFieldReadOnly, gbc);
        JPanel formButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,0)); formButtonsPanel.setOpaque(false);
        addOrUpdateButton = new JButton("Add Trip"); addOrUpdateButton.setFont(FONT_BUTTON_PRIMARY); addOrUpdateButton.setBackground(COLOR_PRIMARY_ACTION); addOrUpdateButton.setForeground(COLOR_BUTTON_TEXT_WHITE); addOrUpdateButton.addActionListener(e->addOrUpdateTripAction()); formButtonsPanel.add(addOrUpdateButton);
        clearFormButton = new JButton("Clear Form"); clearFormButton.setFont(FONT_BUTTON_SECONDARY); clearFormButton.addActionListener(e->switchToAddMode()); formButtonsPanel.add(clearFormButton);
        gbc.gridx=0; gbc.gridy=4; gbc.gridwidth=4; gbc.weightx=0; gbc.fill=GridBagConstraints.NONE; gbc.anchor=GridBagConstraints.CENTER; gbc.insets=new Insets(10,5,5,5); formPanel.add(formButtonsPanel, gbc);

        JPanel tableContainerPanel = new JPanel(new BorderLayout(0,5)); tableContainerPanel.setOpaque(false); tableContainerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_BORDER_LIGHT),"Existing Trips",javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,javax.swing.border.TitledBorder.DEFAULT_POSITION,FONT_HEADING_H3,COLOR_TEXT_DARK_PRIMARY));
        String[] columnNames = {"ID","From","To","Date","Dep. Time","Arr. Time","Bus No.","Bus Model","Price","Seats"};
        tableModel = new DefaultTableModel(columnNames,0){@Override public boolean isCellEditable(int r,int c){return false;}};
        tripsTable = new JTable(tableModel); setupTable(); JScrollPane scrollPane = new JScrollPane(tripsTable); tableContainerPanel.add(scrollPane,BorderLayout.CENTER);
        JPanel tableActionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); tableActionsPanel.setOpaque(false);
        loadForEditButton = new JButton("Load for Edit"); loadForEditButton.setFont(FONT_BUTTON_SECONDARY); loadForEditButton.setEnabled(false); loadForEditButton.addActionListener(e->prepareEditMode()); tableActionsPanel.add(loadForEditButton);
        deleteTripButton = new JButton("Delete Selected"); deleteTripButton.setFont(FONT_BUTTON_SECONDARY); deleteTripButton.setBackground(COLOR_ERROR_RED); deleteTripButton.setForeground(COLOR_BUTTON_TEXT_WHITE); deleteTripButton.setEnabled(false); deleteTripButton.addActionListener(e->deleteTripAction()); tableActionsPanel.add(deleteTripButton);
        tableContainerPanel.add(tableActionsPanel,BorderLayout.SOUTH);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, tableContainerPanel);
        splitPane.setDividerLocation(220); splitPane.setOpaque(false); splitPane.setBorder(null); add(splitPane,BorderLayout.CENTER);
        switchToAddMode();
    }

    private void updateTripSeatsField() {
        Object selectedItem = busComboBox.getSelectedItem();
        if (selectedItem instanceof BusItem) {
            BusItem busItem = (BusItem) selectedItem;
            tripSeatsFieldReadOnly.setText(String.valueOf(busItem.getTotalSeats()));
            tripSeatsFieldReadOnly.setForeground(COLOR_TEXT_DARK_PRIMARY);
        } else { tripSeatsFieldReadOnly.setText(""); tripSeatsFieldReadOnly.setForeground(COLOR_TEXT_LIGHT_SECONDARY); }
    }

    private void switchToAddMode() {
        editMode=false; editingTripId=-1; addOrUpdateButton.setText("Add Trip"); addOrUpdateButton.setBackground(COLOR_PRIMARY_ACTION);
        applyPlaceholder(departureStationField,PLACEHOLDER_STATION); applyPlaceholder(arrivalStationField,PLACEHOLDER_STATION);
        applyFormatSpecificPlaceholder(departureDateField,LocalDate.now().plusDays(1).format(Utils.DATE_FORMATTER));
        applyFormatSpecificPlaceholder(departureTimeField,"10:00"); applyFormatSpecificPlaceholder(arrivalTimeField,"12:00");
        if(busComboBox.getItemCount()>0)busComboBox.setSelectedIndex(0); updateTripSeatsField();
        applyPlaceholder(priceField,PLACEHOLDER_PRICE); tripsTable.clearSelection();
    }

    private void prepareEditMode() {
        int selectedRow = tripsTable.getSelectedRow();
        if(selectedRow==-1){JOptionPane.showMessageDialog(this,"Select trip to edit.","No Selection",JOptionPane.WARNING_MESSAGE);return;}
        int modelRow = tripsTable.convertRowIndexToModel(selectedRow); editingTripId=(Integer)tableModel.getValueAt(modelRow,0);
        removePlaceholder(departureStationField,PLACEHOLDER_STATION); departureStationField.setText((String)tableModel.getValueAt(modelRow,1));
        removePlaceholder(arrivalStationField,PLACEHOLDER_STATION); arrivalStationField.setText((String)tableModel.getValueAt(modelRow,2));
        departureDateField.setForeground(COLOR_TEXT_DARK_PRIMARY); departureDateField.setText((String)tableModel.getValueAt(modelRow,3));
        String depTimeStr=(String)tableModel.getValueAt(modelRow,4); departureTimeField.setForeground(COLOR_TEXT_DARK_PRIMARY); departureTimeField.setText(depTimeStr.equals("N/A")?"":depTimeStr);
        String arrTimeStr=(String)tableModel.getValueAt(modelRow,5); arrivalTimeField.setForeground(COLOR_TEXT_DARK_PRIMARY); arrivalTimeField.setText(arrTimeStr.equals("N/A")?"":arrTimeStr);
        String busNumInTable=(String)tableModel.getValueAt(modelRow,6);
        for(int i=0;i<busComboBox.getItemCount();i++){if(busComboBox.getItemAt(i).getBusNumber().equals(busNumInTable)){busComboBox.setSelectedIndex(i);break;}}
        updateTripSeatsField();
        removePlaceholder(priceField,PLACEHOLDER_PRICE); priceField.setText(tableModel.getValueAt(modelRow,8).toString());
        addOrUpdateButton.setText("Update Trip"); addOrUpdateButton.setBackground(COLOR_SUCCESS_GREEN); editMode=true;
    }

    private void addOrUpdateTripAction() {
        String depStation=departureStationField.getText().trim(), arrStation=arrivalStationField.getText().trim();
        String dateStr=departureDateField.getText().trim(), depTimeStr=departureTimeField.getText().trim(), arrTimeStr=arrivalTimeField.getText().trim();
        BusItem selectedBusItem=(BusItem)busComboBox.getSelectedItem(); String priceStr=priceField.getText().trim();
        int tripSeats=(selectedBusItem!=null && !selectedBusItem.getBusNumber().equals("No buses available"))?selectedBusItem.getTotalSeats():0;
        if(depStation.equals(PLACEHOLDER_STATION))depStation=""; if(arrStation.equals(PLACEHOLDER_STATION))arrStation="";
        if(priceStr.equals(PLACEHOLDER_PRICE))priceStr="";
        if(departureDateField.getForeground().equals(COLOR_TEXT_LIGHT_SECONDARY))dateStr=""; if(departureTimeField.getForeground().equals(COLOR_TEXT_LIGHT_SECONDARY))depTimeStr="";
        if(arrivalTimeField.getForeground().equals(COLOR_TEXT_LIGHT_SECONDARY))arrTimeStr="";
        if(depStation.isEmpty()||arrStation.isEmpty()||dateStr.isEmpty()||depTimeStr.isEmpty()||selectedBusItem==null||selectedBusItem.getBusNumber().equals("No buses available")||priceStr.isEmpty()||tripSeats<=0){JOptionPane.showMessageDialog(this,"All fields (except opt. Arrival Time) required. Bus must be selected. Seats auto-filled.","Input Error",JOptionPane.ERROR_MESSAGE);return;}
        LocalDate depDate;LocalTime depTime,arrTime=null;BigDecimal price;
        try{depDate=Utils.parseDate(dateStr);if(depDate==null)throw new Exception();if(!editMode&&depDate.isBefore(LocalDate.now())){JOptionPane.showMessageDialog(this,"Departure date cannot be past.","Date Error",JOptionPane.ERROR_MESSAGE);return;}}catch(Exception e){JOptionPane.showMessageDialog(this,"Invalid Departure Date (YYYY-MM-DD).","Input Error",JOptionPane.ERROR_MESSAGE);return;}
        try{depTime=Utils.parseTime(depTimeStr);if(depTime==null)throw new Exception();}catch(Exception e){JOptionPane.showMessageDialog(this,"Invalid Departure Time (HH:MM).","Input Error",JOptionPane.ERROR_MESSAGE);return;}
        if(!arrTimeStr.isEmpty()&&!arrivalTimeField.getForeground().equals(COLOR_TEXT_LIGHT_SECONDARY)){try{arrTime=Utils.parseTime(arrTimeStr);if(arrTime==null)throw new Exception();}catch(Exception e){JOptionPane.showMessageDialog(this,"Invalid Arrival Time (HH:MM).","Input Error",JOptionPane.ERROR_MESSAGE);return;}}
        try{price=new BigDecimal(priceStr);if(price.compareTo(BigDecimal.ZERO)<=0){JOptionPane.showMessageDialog(this,"Price must be positive.","Input Error",JOptionPane.ERROR_MESSAGE);return;}}catch(NumberFormatException e){JOptionPane.showMessageDialog(this,"Invalid Price.","Input Error",JOptionPane.ERROR_MESSAGE);return;}
        String busNum=selectedBusItem.getBusNumber();
        Trip trip=new Trip(depStation,arrStation,depDate,depTime,arrTime,busNum,price,tripSeats,null);
        boolean success;
        if(editMode){trip.setTripId(editingTripId);success=tripService.updateTrip(trip);if(success)JOptionPane.showMessageDialog(this,"Trip updated!","Success",JOptionPane.INFORMATION_MESSAGE);else JOptionPane.showMessageDialog(this,"Update failed.","Error",JOptionPane.ERROR_MESSAGE);}
        else{success=tripService.addTrip(trip);if(success)JOptionPane.showMessageDialog(this,"Trip added! ID: "+trip.getTripId(),"Success",JOptionPane.INFORMATION_MESSAGE);else JOptionPane.showMessageDialog(this,"Add failed.","Error",JOptionPane.ERROR_MESSAGE);}
        if(success){loadTripsData();switchToAddMode();}
    }

    private void deleteTripAction() {
        int selectedRow=tripsTable.getSelectedRow(); if(selectedRow==-1){JOptionPane.showMessageDialog(this,"Select trip to delete.","No Selection",JOptionPane.WARNING_MESSAGE);return;}
        int modelRow=tripsTable.convertRowIndexToModel(selectedRow); int tripIdToDelete=(Integer)tableModel.getValueAt(modelRow,0);
        int confirm=JOptionPane.showConfirmDialog(this,"Delete trip ID "+tripIdToDelete+"?","Confirm",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
        if(confirm==JOptionPane.YES_OPTION){try{boolean success=tripService.deleteTrip(tripIdToDelete);if(success){JOptionPane.showMessageDialog(this,"Trip deleted!","Success",JOptionPane.INFORMATION_MESSAGE);loadTripsData();switchToAddMode();}else JOptionPane.showMessageDialog(this,"Delete failed.","Error",JOptionPane.ERROR_MESSAGE);}catch(SQLException e){if(e.getSQLState().equals("23000")||e.getErrorCode()==1451){JOptionPane.showMessageDialog(this,"Cannot delete trip ID "+tripIdToDelete+". It has bookings.","Constraint Error",JOptionPane.ERROR_MESSAGE);}else JOptionPane.showMessageDialog(this,"DB error deleting trip: "+e.getMessage(),"DB Error",JOptionPane.ERROR_MESSAGE);e.printStackTrace();}}
    }

    private void loadTripsData() {
        tableModel.setRowCount(0); List<Trip> trips=tripService.getAllTripsWithDetails();
        for(Trip trip:trips){tableModel.addRow(new Object[]{trip.getTripId(),trip.getDepartureStation(),trip.getArrivalStation(),Utils.formatDate(trip.getDepartureDate()),trip.getDepartureTime()!=null?Utils.formatTime(trip.getDepartureTime()):"N/A",trip.getArrivalTime()!=null?Utils.formatTime(trip.getArrivalTime()):"N/A",trip.getBusNumber(),trip.getBusModel()!=null?trip.getBusModel():"N/A",String.format("%.2f",trip.getPrice()),trip.getTotalSeatsAvailable()});}
    }

    private void populateBusComboBox() {
        List<Bus> buses=busService.getAllBuses(); Vector<BusItem> busItems=new Vector<>();
        if(buses.isEmpty()){busItems.add(new BusItem("No buses available","",0));busComboBox.setEnabled(false);addOrUpdateButton.setEnabled(false);}
        else{busComboBox.setEnabled(true);addOrUpdateButton.setEnabled(true);for(Bus bus:buses){busItems.add(new BusItem(bus.getBusNumber(),bus.getModel(),bus.getTotalSeats()));}}
        busComboBox.setModel(new DefaultComboBoxModel<>(busItems)); updateTripSeatsField();
    }

    @Override
    public void updatePanelData() {
        Traveler currentUser=mainApp.getCurrentTraveler();
        if(currentUser==null||!currentUser.isAdmin()){JOptionPane.showMessageDialog(mainApp.getMainFrame(),"Access Denied.","Error",JOptionPane.ERROR_MESSAGE);mainApp.showPanel(currentUser==null?BusBookingGUI.LOGIN_PANEL:BusBookingGUI.HOME_PANEL);return;}
        populateBusComboBox(); loadTripsData(); switchToAddMode();
    }
    @Override public void addNotify(){super.addNotify();updatePanelData();}
}