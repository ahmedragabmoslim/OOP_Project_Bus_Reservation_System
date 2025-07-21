package busbooking.ui;

import busbooking.Traveler;
import busbooking.service.UserService;
import busbooking.ui.utils.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.net.URL;
import java.util.List;

public class ManageUsersPanel extends JPanel implements UIConstants, UpdatablePanel {
    private BusBookingGUI mainApp;
    private UserService userService;
    private JTable usersTable;
    private DefaultTableModel tableModel;
    private JButton toggleAdminButton;
    private ImageIcon returnIcon;

    public ManageUsersPanel(BusBookingGUI mainApp) {
        this.mainApp = mainApp;
        setName(BusBookingGUI.MANAGE_USERS_PANEL);
        this.userService = new UserService();
        loadIcons();
        initComponents();
    }

    private void loadIcons() {
        try {
            URL returnIconUrl = getClass().getResource("/busbooking/ui/utils/icons/return.png");
            if (returnIconUrl != null) returnIcon = new ImageIcon(new ImageIcon(returnIconUrl).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        } catch (Exception e) { System.err.println("ManageUsersPanel: Error loading icons: " + e.getMessage()); }
    }
    private void setColumnWidths(JTable table) {
        TableColumn column;
        column = table.getColumnModel().getColumn(0); column.setPreferredWidth(40);
        column = table.getColumnModel().getColumn(1); column.setPreferredWidth(120);
        column = table.getColumnModel().getColumn(2); column.setPreferredWidth(180);
        column = table.getColumnModel().getColumn(3); column.setPreferredWidth(100);
        column = table.getColumnModel().getColumn(4); column.setPreferredWidth(100);
        column = table.getColumnModel().getColumn(5); column.setPreferredWidth(100);
        column = table.getColumnModel().getColumn(6); column.setPreferredWidth(70);
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
        JLabel headerLabel = new JLabel("Manage Users", SwingConstants.CENTER);
        headerLabel.setFont(FONT_HEADING_H2); headerLabel.setForeground(COLOR_TEXT_ON_PRIMARY_ACTION);
        topPanel.add(headerLabel, BorderLayout.CENTER);
        topPanel.add(Box.createHorizontalStrut(backButton.getPreferredSize().width > 0 ? backButton.getPreferredSize().width : 50), BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout()); tablePanel.setOpaque(false);
        String[] columnNames = {"ID", "Username", "Email", "First Name", "Last Name", "Phone", "Is Admin"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int colIdx) { if (colIdx == 6) return Boolean.class; return super.getColumnClass(colIdx); }
        };
        usersTable = new JTable(tableModel);
        usersTable.setFont(FONT_BODY_PLAIN.deriveFont(12f)); usersTable.getTableHeader().setFont(FONT_LABEL_BOLD.deriveFont(12f));
        usersTable.setRowHeight(25); usersTable.setFillsViewportHeight(true); usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersTable.setAutoCreateRowSorter(true); setColumnWidths(usersTable);
        JScrollPane scrollPane = new JScrollPane(usersTable); tablePanel.add(scrollPane, BorderLayout.CENTER);
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); actionPanel.setOpaque(false);
        toggleAdminButton = new JButton("Toggle Admin Status");
        toggleAdminButton.setFont(FONT_BUTTON_SECONDARY); toggleAdminButton.setEnabled(false);
        toggleAdminButton.addActionListener(e -> toggleAdminStatusAction()); actionPanel.add(toggleAdminButton);
        usersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && usersTable.getSelectedRow() != -1) toggleAdminButton.setEnabled(true);
            else if (usersTable.getSelectedRow() == -1) toggleAdminButton.setEnabled(false);
        });
        tablePanel.add(actionPanel, BorderLayout.SOUTH); add(tablePanel, BorderLayout.CENTER);
    }

    private void toggleAdminStatusAction() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) { JOptionPane.showMessageDialog(this, "Please select a user.", "No Selection", JOptionPane.WARNING_MESSAGE); return; }
        int modelRow = usersTable.convertRowIndexToModel(selectedRow);
        int userId = (Integer) tableModel.getValueAt(modelRow, 0);
        boolean currentAdminStatus = (Boolean) tableModel.getValueAt(modelRow, 6);
        Traveler currentUserLoggedIn = mainApp.getCurrentTraveler();
        if (currentUserLoggedIn != null && currentUserLoggedIn.getUserId() == userId) {
            JOptionPane.showMessageDialog(this, "You cannot change your own admin status.", "Action Denied", JOptionPane.WARNING_MESSAGE); return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Change admin status for user ID " + userId + "?\nNew status: " + (!currentAdminStatus ? "Admin" : "User"), "Confirm Change", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = userService.updateUserAdminStatus(userId, !currentAdminStatus);
            if (success) { JOptionPane.showMessageDialog(this, "Admin status updated.", "Success", JOptionPane.INFORMATION_MESSAGE); loadUsersData(); }
            else JOptionPane.showMessageDialog(this, "Failed to update status.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        usersTable.clearSelection(); toggleAdminButton.setEnabled(false);
    }

    private void loadUsersData() {
        tableModel.setRowCount(0); List<Traveler> users = userService.getAllUsers();
        for (Traveler user : users) {
            tableModel.addRow(new Object[]{
                    user.getUserId(), user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName(),
                    user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty() ? user.getPhoneNumber() : "N/A",
                    user.isAdmin()
            });
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
        loadUsersData(); toggleAdminButton.setEnabled(false);
    }
    @Override public void addNotify() { super.addNotify(); updatePanelData(); }
}