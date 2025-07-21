package busbooking.ui;

import busbooking.Bus;
import busbooking.Traveler;
import busbooking.service.BusService;
import busbooking.ui.utils.UIConstants;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class ManageBusesPanel extends JPanel implements UIConstants, UpdatablePanel {
    private BusBookingGUI mainApp;
    private BusService busService;

    private JTextField busNumberField, modelField, totalSeatsField;
    private JButton addButton, updateButton, deleteButton, clearButton;
    private JTable busesTable;
    private DefaultTableModel tableModel;
    private ImageIcon returnIcon;

    private static final String PLACEHOLDER_BUS_NUMBER = "e.g., B101";
    private static final String PLACEHOLDER_MODEL = "e.g., Volvo B9R";
    private static final String PLACEHOLDER_SEATS = "e.g., 45";

    private boolean editMode = false;
    private String originalBusNumberForEdit = null;

    public ManageBusesPanel(BusBookingGUI mainApp) {
        this.mainApp = mainApp;
        setName(BusBookingGUI.MANAGE_BUSES_PANEL);
        this.busService = new BusService();
        loadIcons();
        initComponents();
    }

    private void loadIcons() {
        try {
            URL returnIconUrl = getClass().getResource("/busbooking/ui/utils/icons/return.png");
            if (returnIconUrl != null) {
                returnIcon = new ImageIcon(new ImageIcon(returnIconUrl).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
            }
        } catch (Exception e) { System.err.println("ManageBusesPanel: Error loading icons: " + e.getMessage()); }
    }

    private void applyPlaceholder(JTextField field, String placeholder) {
        if (field.getText().isEmpty() || field.getText().equals(placeholder)) {
            field.setText(placeholder); field.setForeground(COLOR_TEXT_LIGHT_SECONDARY);
        }
    }
    private void removePlaceholder(JTextField field, String placeholder) {
        if (field.getText().equals(placeholder)) {
            field.setText(""); field.setForeground(COLOR_TEXT_DARK_PRIMARY);
        }
    }
    private JLabel createSmallLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL_BOLD); label.setForeground(COLOR_TEXT_DARK_SECONDARY);
        return label;
    }
    private JTextField createTextFieldWithPlaceholder(String placeholder, int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(FONT_TEXTFIELD); textField.setBorder(BORDER_TEXTFIELD_DEFAULT);
        textField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent evt) { removePlaceholder(textField, placeholder); textField.setBorder(BORDER_TEXTFIELD_FOCUSED); }
            @Override public void focusLost(FocusEvent evt) { applyPlaceholder(textField, placeholder); textField.setBorder(BORDER_TEXTFIELD_DEFAULT); }
        });
        return textField;
    }
    private void resetFormAndPlaceholders() {
        busNumberField.setText(""); modelField.setText(""); totalSeatsField.setText("");
        applyPlaceholder(busNumberField, PLACEHOLDER_BUS_NUMBER);
        applyPlaceholder(modelField, PLACEHOLDER_MODEL);
        applyPlaceholder(totalSeatsField, PLACEHOLDER_SEATS);
    }
    private void setupTable() {
        busesTable.setFont(FONT_BODY_PLAIN); busesTable.getTableHeader().setFont(FONT_LABEL_BOLD);
        busesTable.setRowHeight(25); busesTable.setFillsViewportHeight(true);
        busesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); busesTable.setAutoCreateRowSorter(true);
        busesTable.getSelectionModel().addListSelectionListener(e -> {
            boolean rowSelected = !e.getValueIsAdjusting() && busesTable.getSelectedRow() != -1;
            updateButton.setEnabled(rowSelected); deleteButton.setEnabled(rowSelected);
        });
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
        JLabel headerLabel = new JLabel("Manage Buses", SwingConstants.CENTER);
        headerLabel.setFont(FONT_HEADING_H2); headerLabel.setForeground(COLOR_TEXT_ON_PRIMARY_ACTION);
        topPanel.add(headerLabel, BorderLayout.CENTER);
        topPanel.add(Box.createHorizontalStrut(backButton.getPreferredSize().width > 0 ? backButton.getPreferredSize().width : 50), BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_BORDER_LIGHT), "Bus Details",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
                FONT_HEADING_H3, COLOR_TEXT_DARK_PRIMARY));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(createSmallLabel("Bus Number:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        busNumberField = createTextFieldWithPlaceholder(PLACEHOLDER_BUS_NUMBER, 15); formPanel.add(busNumberField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createSmallLabel("Model:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        modelField = createTextFieldWithPlaceholder(PLACEHOLDER_MODEL, 15); formPanel.add(modelField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createSmallLabel("Total Seats:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        totalSeatsField = createTextFieldWithPlaceholder(PLACEHOLDER_SEATS, 5); formPanel.add(totalSeatsField, gbc);

        JPanel formButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        formButtonsPanel.setOpaque(false);
        addButton = new JButton("Add Bus");
        addButton.setFont(FONT_BUTTON_PRIMARY); addButton.setBackground(COLOR_PRIMARY_ACTION); addButton.setForeground(COLOR_BUTTON_TEXT_WHITE);
        addButton.addActionListener(e -> addOrUpdateBusAction()); formButtonsPanel.add(addButton);
        clearButton = new JButton("Clear Form");
        clearButton.setFont(FONT_BUTTON_SECONDARY); clearButton.addActionListener(e -> switchToAddMode());
        formButtonsPanel.add(clearButton);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 5, 5, 5); formPanel.add(formButtonsPanel, gbc);

        JPanel tableContainerPanel = new JPanel(new BorderLayout(0,5));
        tableContainerPanel.setOpaque(false);
        tableContainerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_BORDER_LIGHT),"Existing Buses",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
                FONT_HEADING_H3, COLOR_TEXT_DARK_PRIMARY));
        String[] columnNames = {"Bus Number", "Model", "Total Seats"};
        tableModel = new DefaultTableModel(columnNames, 0) { @Override public boolean isCellEditable(int r, int c) { return false; }};
        busesTable = new JTable(tableModel); setupTable();
        JScrollPane scrollPane = new JScrollPane(busesTable); tableContainerPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel tableActionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); tableActionsPanel.setOpaque(false);
        updateButton = new JButton("Load for Edit");
        updateButton.setFont(FONT_BUTTON_SECONDARY); updateButton.setEnabled(false);
        updateButton.addActionListener(e -> prepareEditMode()); tableActionsPanel.add(updateButton);
        deleteButton = new JButton("Delete Selected");
        deleteButton.setFont(FONT_BUTTON_SECONDARY); deleteButton.setBackground(COLOR_ERROR_RED); deleteButton.setForeground(COLOR_BUTTON_TEXT_WHITE);
        deleteButton.setEnabled(false); deleteButton.addActionListener(e -> deleteBusAction());
        tableActionsPanel.add(deleteButton);
        tableContainerPanel.add(tableActionsPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, tableContainerPanel);
        splitPane.setDividerLocation(200); splitPane.setOpaque(false); splitPane.setBorder(null);
        add(splitPane, BorderLayout.CENTER);
        resetFormAndPlaceholders();
    }

    private void switchToAddMode() {
        editMode = false; originalBusNumberForEdit = null;
        busNumberField.setEditable(true); busNumberField.setBackground(UIManager.getColor("TextField.background"));
        addButton.setText("Add Bus"); addButton.setBackground(COLOR_PRIMARY_ACTION);
        resetFormAndPlaceholders(); busesTable.clearSelection();
    }

    private void prepareEditMode() {
        int selectedRow = busesTable.getSelectedRow();
        if (selectedRow == -1) { JOptionPane.showMessageDialog(this, "Please select a bus to edit.", "No Selection", JOptionPane.WARNING_MESSAGE); return; }
        int modelRow = busesTable.convertRowIndexToModel(selectedRow);
        originalBusNumberForEdit = (String) tableModel.getValueAt(modelRow, 0);
        String model = (String) tableModel.getValueAt(modelRow, 1);
        int seats = (Integer) tableModel.getValueAt(modelRow, 2);
        removePlaceholder(busNumberField, PLACEHOLDER_BUS_NUMBER); busNumberField.setText(originalBusNumberForEdit);
        removePlaceholder(modelField, PLACEHOLDER_MODEL); modelField.setText(model);
        removePlaceholder(totalSeatsField, PLACEHOLDER_SEATS); totalSeatsField.setText(String.valueOf(seats));
        busNumberField.setEditable(false); busNumberField.setBackground(new Color(0xF0F0F0));
        addButton.setText("Update Bus"); addButton.setBackground(COLOR_SUCCESS_GREEN);
        editMode = true;
    }

    private void addOrUpdateBusAction() {
        String busNumber = busNumberField.getText().trim(); String model = modelField.getText().trim(); String seatsStr = totalSeatsField.getText().trim();
        if ((busNumber.equals(PLACEHOLDER_BUS_NUMBER) && !editMode) || busNumber.isEmpty()) { JOptionPane.showMessageDialog(this, "Bus Number required.", "Input Error", JOptionPane.ERROR_MESSAGE); return; }
        if (model.equals(PLACEHOLDER_MODEL) || model.isEmpty()) { JOptionPane.showMessageDialog(this, "Model required.", "Input Error", JOptionPane.ERROR_MESSAGE); return; }
        if (seatsStr.equals(PLACEHOLDER_SEATS) || seatsStr.isEmpty()) { JOptionPane.showMessageDialog(this, "Total Seats required.", "Input Error", JOptionPane.ERROR_MESSAGE); return; }
        int totalSeats;
        try { totalSeats = Integer.parseInt(seatsStr); if (totalSeats <= 0) { JOptionPane.showMessageDialog(this, "Seats must be positive.", "Input Error", JOptionPane.ERROR_MESSAGE); return; }
        } catch (NumberFormatException e) { JOptionPane.showMessageDialog(this, "Seats must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE); return; }
        Bus bus = new Bus(editMode ? originalBusNumberForEdit : busNumber, model, totalSeats);
        boolean success;
        if (editMode) {
            success = busService.updateBus(bus);
            if (success) JOptionPane.showMessageDialog(this, "Bus updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            else JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            success = busService.addBus(bus);
            if (success) JOptionPane.showMessageDialog(this, "Bus added!", "Success", JOptionPane.INFORMATION_MESSAGE);
            else JOptionPane.showMessageDialog(this, "Add failed. Bus number might exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        if (success) { loadBusesData(); switchToAddMode(); }
    }

    private void deleteBusAction() {
        int selectedRow = busesTable.getSelectedRow();
        if (selectedRow == -1) { JOptionPane.showMessageDialog(this, "Select bus to delete.", "No Selection", JOptionPane.WARNING_MESSAGE); return; }
        int modelRow = busesTable.convertRowIndexToModel(selectedRow);
        String busNumDel = (String) tableModel.getValueAt(modelRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete bus '" + busNumDel + "'?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = busService.deleteBus(busNumDel);
                if (success) { JOptionPane.showMessageDialog(this, "Bus deleted!", "Success", JOptionPane.INFORMATION_MESSAGE); loadBusesData(); switchToAddMode(); }
                else JOptionPane.showMessageDialog(this, "Delete failed for bus '" + busNumDel + "'.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                if (e.getSQLState().equals("23000") || e.getErrorCode() == 1451) {
                    JOptionPane.showMessageDialog(this, "Cannot delete bus '" + busNumDel + "'. It has associated trips.", "Constraint Error", JOptionPane.ERROR_MESSAGE);
                } else JOptionPane.showMessageDialog(this, "DB error deleting bus: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void loadBusesData() {
        tableModel.setRowCount(0); List<Bus> buses = busService.getAllBuses();
        for (Bus bus : buses) { tableModel.addRow(new Object[]{bus.getBusNumber(), bus.getModel(), bus.getTotalSeats()}); }
    }

    @Override
    public void updatePanelData() {
        Traveler currentUser = mainApp.getCurrentTraveler();
        if (currentUser == null || !currentUser.isAdmin()) {
            JOptionPane.showMessageDialog(mainApp.getMainFrame(), "Access Denied.", "Error", JOptionPane.ERROR_MESSAGE);
            mainApp.showPanel(currentUser == null ? BusBookingGUI.LOGIN_PANEL : BusBookingGUI.HOME_PANEL);
            return;
        }
        loadBusesData(); switchToAddMode();
    }

    @Override public void addNotify() { super.addNotify(); updatePanelData(); }
}