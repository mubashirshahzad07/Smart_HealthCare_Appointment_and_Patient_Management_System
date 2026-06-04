package patient.management.system.ui;

import patient.management.system.dto.ReceptionistDTO;
import patient.management.system.model.Receptionist;
import patient.management.system.service.AdminService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

public class AdminReceptionistsPanel extends JPanel {
    private final AdminService adminService = new AdminService();
    private final DefaultTableModel receptionistModel;

   public AdminReceptionistsPanel() {
    setLayout(new BorderLayout());
    JPanel panel = AdminUI.basePanel(
            "Manage Receptionists",
            "Add, update shift, and remove receptionist records"
    );

    String[] columns = {
            "Receptionist ID",
            "Name",
            "Username",
            "Shift",
            "Status"
    };

    receptionistModel = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    loadReceptionists();

    JTable table = AdminUI.table(receptionistModel);
    table.getColumnModel().getColumn(4).setCellRenderer(new AdminUI.StatusRenderer());
        JButton addButton = AdminUI.actionButton("Add Receptionist", AdminUI.GREEN);
        JButton updateButton = AdminUI.actionButton("Update Shift", AdminUI.ORANGE);
        JButton activateButton = AdminUI.actionButton("Activate Receptionist", AdminUI.GREEN);
        JButton deactivateButton = AdminUI.actionButton("Deactivate Receptionist", AdminUI.RED);
        JButton clearButton = AdminUI.actionButton("Clear", AdminUI.BLUE);

        addButton.addActionListener(event -> openAddReceptionistForm());
        updateButton.addActionListener(event -> updateSelectedReceptionist(table));
        activateButton.addActionListener(event -> activateSelectedReceptionist(table));
        deactivateButton.addActionListener(event -> deactivateSelectedReceptionist(table));
        clearButton.addActionListener(event -> table.clearSelection());

        panel.add(AdminUI.searchBar("Search receptionist by name or ID", table, 0, 1), BorderLayout.BEFORE_FIRST_LINE);
        panel.add(AdminUI.tableCard(table), BorderLayout.CENTER);
        panel.add(AdminUI.buttonBar(addButton, updateButton, activateButton, deactivateButton, clearButton), BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);
    }

    private void loadReceptionists() {
        receptionistModel.setRowCount(0);

        try {
            List<ReceptionistDTO> receptionists = adminService.getAllReceptionists();

            for (ReceptionistDTO receptionist : receptionists) {
                receptionistModel.addRow(new Object[]{
                        receptionist.getReceptionistId(),
                        receptionist.getName(),
                        receptionist.getUsername(),
                        displayShift(receptionist.getShift()),
                        receptionist.getIsActive() ? "Active" : "Inactive"
                });
            }

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Load Receptionists Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openAddReceptionistForm() {
        JDialog dialog = new JDialog((java.awt.Frame) null, "Add Receptionist", true);
        dialog.setSize(430, 340);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 12));
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        form.setBackground(AdminUI.LIGHT);

        JTextField nameField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        String[] shifts = {
                "Morning (08:00-16:00)",
                "Evening (16:00-00:00)",
                "Night (00:00-08:00)"
        };

        JComboBox<String> shiftBox = new JComboBox<>(shifts);

        form.add(AdminUI.label("Full Name:"));
        form.add(nameField);
        form.add(AdminUI.label("Username:"));
        form.add(usernameField);
        form.add(AdminUI.label("Password:"));
        form.add(passwordField);
        form.add(AdminUI.label("Shift:"));
        form.add(shiftBox);

        JButton submitButton = AdminUI.actionButton("Add Receptionist", AdminUI.GREEN);
        submitButton.addActionListener(event -> {
            String name = nameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "All fields are required.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Receptionist.Shift shift = toReceptionistShift(shiftBox.getSelectedItem().toString());

                adminService.addReceptionist(username, name, password, shift);

                loadReceptionists();
                JOptionPane.showMessageDialog(this, "Receptionist added successfully.");
                dialog.dispose();

            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(dialog,
                        e.getMessage(),
                        "Add Receptionist Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(AdminUI.LIGHT);
        buttonPanel.add(submitButton);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void updateSelectedReceptionist(JTable table) {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a receptionist row to update.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        openUpdateShiftForm(table.convertRowIndexToModel(row));
    }

    private void openUpdateShiftForm(int modelRow) {
        JOptionPane.showMessageDialog(this,
                "Backend inconsistency: AdminService has no updateReceptionistShift(...) method, so this UI action cannot be integrated with backend yet.",
                "Cannot Update Shift",
                JOptionPane.WARNING_MESSAGE);
    }

    private void activateSelectedReceptionist(JTable table) {
    int row = table.getSelectedRow();

    if (row == -1) {
        JOptionPane.showMessageDialog(this,
                "Please select a receptionist row to activate.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    int modelRow = table.convertRowIndexToModel(row);
    String receptionistId = receptionistModel.getValueAt(modelRow, 0).toString();
    String name = receptionistModel.getValueAt(modelRow, 1).toString();

    int confirm = JOptionPane.showConfirmDialog(this,
            "Activate " + name + "?",
            "Confirm Activation",
            JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        try {
            adminService.activateReceptionist(receptionistId);
            receptionistModel.setValueAt("Active", modelRow, 4);
            JOptionPane.showMessageDialog(this, "Receptionist activated successfully.");

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Activate Receptionist Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

    private void deactivateSelectedReceptionist(JTable table) {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a receptionist row to deactivate.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        String receptionistId = receptionistModel.getValueAt(modelRow, 0).toString();
        String name = receptionistModel.getValueAt(modelRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Deactivate " + name + "?",
                "Confirm Deactivation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                adminService.inactivateReceptionist(receptionistId);
                receptionistModel.setValueAt("Inactive", modelRow, 4);
                JOptionPane.showMessageDialog(this, "Receptionist deactivated successfully.");

            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(this,
                        e.getMessage(),
                        "Deactivate Receptionist Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Receptionist.Shift toReceptionistShift(String shiftText) {
        String upper = shiftText.toUpperCase();

        if (upper.startsWith("MORNING")) {
            return Receptionist.Shift.MORNING;
        }

        if (upper.startsWith("EVENING")) {
            return Receptionist.Shift.EVENING;
        }

        if (upper.startsWith("NIGHT")) {
            return Receptionist.Shift.NIGHT;
        }

        return Receptionist.Shift.MORNING;
    }

    private String displayShift(String shift) {
        if (shift == null) {
            return "";
        }

        switch (shift.toUpperCase()) {
            case "MORNING":
                return "Morning (08:00-16:00)";
            case "EVENING":
                return "Evening (16:00-00:00)";
            case "NIGHT":
                return "Night (00:00-08:00)";
            default:
                return shift;
        }
    }
}