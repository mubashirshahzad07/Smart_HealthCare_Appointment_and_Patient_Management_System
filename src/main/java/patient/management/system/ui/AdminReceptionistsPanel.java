package patient.management.system.ui;

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

public class AdminReceptionistsPanel extends JPanel {
    private final DefaultTableModel receptionistModel;

    public AdminReceptionistsPanel() {
        setLayout(new BorderLayout());
        JPanel panel = AdminUI.basePanel("Manage Receptionists", "Add, update shift, and remove receptionist records");

        String[] columns = {"Receptionist ID", "Name", "Username", "Shift", "Status"};
        Object[][] data = {
                {"R001", "Ayesha Khan", "ayesha.k", "Morning (08:00-16:00)", "Active"},
                {"R002", "Sara Malik", "sara.m", "Evening (16:00-00:00)", "Active"},
                {"R003", "Usman Tariq", "usman.t", "Night (00:00-08:00)", "Inactive"},
                {"R004", "Nadia Iqbal", "nadia.i", "Morning (08:00-16:00)", "Active"}
        };

        receptionistModel = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = AdminUI.table(receptionistModel);
        table.getColumnModel().getColumn(4).setCellRenderer(new AdminUI.StatusRenderer());

        JButton addButton = AdminUI.actionButton("Add Receptionist", AdminUI.GREEN);
        JButton updateButton = AdminUI.actionButton("Update Shift", AdminUI.ORANGE);
        JButton deactivateButton = AdminUI.actionButton("Deactivate Receptionist", AdminUI.RED);
        JButton clearButton = AdminUI.actionButton("Clear", AdminUI.BLUE);

        addButton.addActionListener(event -> openAddReceptionistForm());
        updateButton.addActionListener(event -> updateSelectedReceptionist(table));
        deactivateButton.addActionListener(event -> deactivateSelectedReceptionist(table));
        clearButton.addActionListener(event -> table.clearSelection());

        panel.add(AdminUI.searchBar("Search receptionist by name or ID", table, 0, 1), BorderLayout.BEFORE_FIRST_LINE);
        panel.add(AdminUI.tableCard(table), BorderLayout.CENTER);
        panel.add(AdminUI.buttonBar(addButton, updateButton, deactivateButton, clearButton), BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);
    }

    private void openAddReceptionistForm() {
        JDialog dialog = new JDialog((java.awt.Frame) null, "Add Receptionist", true);
        dialog.setSize(430, 340);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 12));
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        form.setBackground(AdminUI.LIGHT);

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        String[] shifts = {"Morning (08:00-16:00)", "Evening (16:00-00:00)", "Night (00:00-08:00)"};
        JComboBox<String> shiftBox = new JComboBox<>(shifts);

        form.add(AdminUI.label("Receptionist ID:"));
        form.add(idField);
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
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String shift = shiftBox.getSelectedItem().toString();

            if (id.isEmpty() || name.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            receptionistModel.addRow(new Object[]{id, name, username, shift, "Active"});
            JOptionPane.showMessageDialog(this, "Receptionist added successfully.");
            dialog.dispose();
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
            JOptionPane.showMessageDialog(this, "Please select a receptionist row to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        openUpdateShiftForm(table.convertRowIndexToModel(row));
    }

    private void openUpdateShiftForm(int modelRow) {
        JDialog dialog = new JDialog((java.awt.Frame) null, "Update Shift", true);
        dialog.setSize(390, 210);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 12));
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        form.setBackground(AdminUI.LIGHT);

        JLabel nameLabel = new JLabel(receptionistModel.getValueAt(modelRow, 1).toString());
        nameLabel.setFont(UITheme.BUTTON_FONT);
        nameLabel.setForeground(AdminUI.NAVY);

        String[] shifts = {"Morning (08:00-16:00)", "Evening (16:00-00:00)", "Night (00:00-08:00)"};
        JComboBox<String> shiftBox = new JComboBox<>(shifts);
        shiftBox.setSelectedItem(receptionistModel.getValueAt(modelRow, 3).toString());

        form.add(AdminUI.label("Receptionist:"));
        form.add(nameLabel);
        form.add(AdminUI.label("New Shift:"));
        form.add(shiftBox);

        JButton submitButton = AdminUI.actionButton("Update Shift", AdminUI.ORANGE);
        submitButton.addActionListener(event -> {
            receptionistModel.setValueAt(shiftBox.getSelectedItem().toString(), modelRow, 3);
            JOptionPane.showMessageDialog(this, "Shift updated successfully.");
            dialog.dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(AdminUI.LIGHT);
        buttonPanel.add(submitButton);
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deactivateSelectedReceptionist(JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a receptionist row to deactivate.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        String name = receptionistModel.getValueAt(modelRow, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Deactivate " + name + "?", "Confirm Deactivation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            receptionistModel.setValueAt("Inactive", modelRow, 4);
            JOptionPane.showMessageDialog(this, "Receptionist deactivated successfully.");
        }
    }
}