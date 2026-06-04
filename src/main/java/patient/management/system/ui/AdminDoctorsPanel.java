package patient.management.system.ui;

import patient.management.system.dto.DoctorDTO;
import patient.management.system.model.Doctor;
import patient.management.system.model.DoctorSchedule;
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

public class AdminDoctorsPanel extends JPanel {
    private final AdminService adminService = new AdminService();
    private final DefaultTableModel doctorModel;

    private static final String[] FULL_DAYS = {
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    };

    private static final String[] SHIFTS = {"MORNING", "EVENING"};

    public AdminDoctorsPanel() {
        setLayout(new BorderLayout());
        JPanel panel = AdminUI.basePanel("Manage Doctors", "Add, update schedule/fee, and remove doctor records");

        String[] columns = {"Doctor ID", "Name", "Specialization", "Schedule", "Fee (PKR)", "Status"};

        doctorModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        loadDoctors();

        JTable table = AdminUI.table(doctorModel);
        table.getColumnModel().getColumn(5).setCellRenderer(new AdminUI.StatusRenderer());

    JButton addButton = AdminUI.actionButton("Add Doctor", AdminUI.GREEN);
    JButton updateButton = AdminUI.actionButton("Update Schedule/Fee", AdminUI.ORANGE);
    JButton activateButton = AdminUI.actionButton("Activate Doctor", AdminUI.GREEN);
    JButton deactivateButton = AdminUI.actionButton("Deactivate Doctor", AdminUI.RED);
    JButton clearButton = AdminUI.actionButton("Clear", AdminUI.BLUE);

            addButton.addActionListener(event -> openAddDoctorForm());
            updateButton.addActionListener(event -> updateSelectedDoctor(table));
            activateButton.addActionListener(event -> activateSelectedDoctor(table));
            deactivateButton.addActionListener(event -> deactivateSelectedDoctor(table));
            clearButton.addActionListener(event -> table.clearSelection());

        panel.add(AdminUI.searchBar("Search doctor by name or ID", table, 0, 1), BorderLayout.BEFORE_FIRST_LINE);
        panel.add(AdminUI.tableCard(table), BorderLayout.CENTER);
        panel.add(AdminUI.buttonBar(addButton, updateButton, activateButton, deactivateButton, clearButton), BorderLayout.SOUTH);
         add(panel, BorderLayout.CENTER);
    }

    private void loadDoctors() {
        doctorModel.setRowCount(0);

        try {
            List<DoctorDTO> doctors = adminService.getAllDoctors();

            for (DoctorDTO doctor : doctors) {
                doctorModel.addRow(new Object[]{
                        doctor.getDoctorId(),
                        doctor.getName(),
                        doctor.getSpecialization(),
                        "Schedule not available in DoctorDTO",
                        String.valueOf(doctor.getAppointmentFee()),
                        doctor.getIsActive() ? "Available" : "Inactive"
                });
            }

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Load Doctors Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openAddDoctorForm() {
        JDialog dialog = new JDialog((java.awt.Frame) null, "Add Doctor", true);
        dialog.setSize(500, 440);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 12));
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        form.setBackground(AdminUI.LIGHT);

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField nameField = new JTextField();

        JComboBox<String> specializationBox = new JComboBox<>(new String[]{
                "GENERAL_PHYSICIAN",
                "CARDIOLOGIST",
                "DERMATOLOGIST",
                "ORTHOPEDIC",
                "PEDIATRICIAN",
                "NEUROLOGIST",
                "ENT",
                "GYNECOLOGIST",
                "PSYCHIATRIST"
        });

        JTextField feeField = new JTextField();

        JComboBox<String> fromDayBox = new JComboBox<>(FULL_DAYS);
        JComboBox<String> toDayBox = new JComboBox<>(FULL_DAYS);
        toDayBox.setSelectedIndex(4);

        JPanel dayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        dayPanel.setBackground(AdminUI.LIGHT);
        dayPanel.add(fromDayBox);
        dayPanel.add(new JLabel("to"));
        dayPanel.add(toDayBox);

        JComboBox<String> shiftBox = new JComboBox<>(SHIFTS);

        form.add(AdminUI.label("Username:"));
        form.add(usernameField);
        form.add(AdminUI.label("Password:"));
        form.add(passwordField);
        form.add(AdminUI.label("Full Name:"));
        form.add(nameField);
        form.add(AdminUI.label("Specialization:"));
        form.add(specializationBox);
        form.add(AdminUI.label("Appointment Fee:"));
        form.add(feeField);
        form.add(AdminUI.label("Days:"));
        form.add(dayPanel);
        form.add(AdminUI.label("Shift:"));
        form.add(shiftBox);

        JButton submitButton = AdminUI.actionButton("Add Doctor", AdminUI.GREEN);
        submitButton.addActionListener(event -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String name = nameField.getText().trim();
            String feeText = feeField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || name.isEmpty() || feeText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "All fields are required.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double fee = Double.parseDouble(feeText);

                Doctor.Specialization specialization =
                        Doctor.Specialization.valueOf(specializationBox.getSelectedItem().toString());

                DoctorSchedule.Day startDay = toDoctorDay(fromDayBox.getSelectedItem().toString());
                DoctorSchedule.Day endDay = toDoctorDay(toDayBox.getSelectedItem().toString());
                DoctorSchedule.Shift shift =
                        DoctorSchedule.Shift.valueOf(shiftBox.getSelectedItem().toString());

                adminService.addDoctor(username, password, name, fee, specialization, startDay, endDay, shift);

                loadDoctors();
                JOptionPane.showMessageDialog(this, "Doctor added successfully.");
                dialog.dispose();

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(dialog,
                        "Fee must be a valid number.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);

            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(dialog,
                        e.getMessage(),
                        "Add Doctor Error",
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

    private void updateSelectedDoctor(JTable table) {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a doctor row to update.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        openUpdateScheduleFeeForm(table.convertRowIndexToModel(row));
    }

    private void openUpdateScheduleFeeForm(int modelRow) {
        JDialog dialog = new JDialog((java.awt.Frame) null, "Update Schedule / Fee", true);
        dialog.setSize(460, 310);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 12));
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        form.setBackground(AdminUI.LIGHT);

        JLabel doctorName = new JLabel(doctorModel.getValueAt(modelRow, 1).toString());
        doctorName.setFont(UITheme.BUTTON_FONT);
        doctorName.setForeground(AdminUI.NAVY);

        JComboBox<String> fromDayBox = new JComboBox<>(FULL_DAYS);
        JComboBox<String> toDayBox = new JComboBox<>(FULL_DAYS);
        toDayBox.setSelectedIndex(4);

        String existingSchedule = doctorModel.getValueAt(modelRow, 3).toString();
        preselectDayBoxes(existingSchedule, fromDayBox, toDayBox);

        JPanel dayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        dayPanel.setBackground(AdminUI.LIGHT);
        dayPanel.add(fromDayBox);
        dayPanel.add(new JLabel("to"));
        dayPanel.add(toDayBox);

        JComboBox<String> shiftBox = new JComboBox<>(SHIFTS);
        preselectShift(existingSchedule, shiftBox);

        JTextField feeField = new JTextField(doctorModel.getValueAt(modelRow, 4).toString());

        form.add(AdminUI.label("Doctor:"));
        form.add(doctorName);
        form.add(AdminUI.label("Days:"));
        form.add(dayPanel);
        form.add(AdminUI.label("Shift:"));
        form.add(shiftBox);
        form.add(AdminUI.label("Fee (PKR):"));
        form.add(feeField);

        JButton submitButton = AdminUI.actionButton("Update", AdminUI.ORANGE);
        submitButton.addActionListener(event -> {
            String feeText = feeField.getText().trim();

            if (feeText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Fee is required.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String doctorId = doctorModel.getValueAt(modelRow, 0).toString();
                double fee = Double.parseDouble(feeText);

                DoctorSchedule.Day startDay = toDoctorDay(fromDayBox.getSelectedItem().toString());
                DoctorSchedule.Day endDay = toDoctorDay(toDayBox.getSelectedItem().toString());
                DoctorSchedule.Shift shift =
                        DoctorSchedule.Shift.valueOf(shiftBox.getSelectedItem().toString());

                adminService.updateDoctorSchedule(doctorId, startDay, endDay, shift, fee);

                String schedule = fromDayBox.getSelectedItem() + " - " + toDayBox.getSelectedItem()
                        + " | " + shiftBox.getSelectedItem();

                doctorModel.setValueAt(schedule, modelRow, 3);
                doctorModel.setValueAt(feeText, modelRow, 4);

                JOptionPane.showMessageDialog(this, "Schedule and fee updated successfully.");
                dialog.dispose();

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(dialog,
                        "Fee must be a valid number.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);

            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(dialog,
                        e.getMessage(),
                        "Update Doctor Error",
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

    private void activateSelectedDoctor(JTable table) {
    int row = table.getSelectedRow();

    if (row == -1) {
        JOptionPane.showMessageDialog(this,
                "Please select a doctor row to activate.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    int modelRow = table.convertRowIndexToModel(row);
    String doctorId = doctorModel.getValueAt(modelRow, 0).toString();
    String name = doctorModel.getValueAt(modelRow, 1).toString();

    int confirm = JOptionPane.showConfirmDialog(this,
            "Activate " + name + "?",
            "Confirm Activation",
            JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        try {
            adminService.activateDoctor(doctorId);
            doctorModel.setValueAt("Available", modelRow, 5);
            JOptionPane.showMessageDialog(this, "Doctor activated successfully.");

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Activate Doctor Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

    private void deactivateSelectedDoctor(JTable table) {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a doctor row to deactivate.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        String doctorId = doctorModel.getValueAt(modelRow, 0).toString();
        String name = doctorModel.getValueAt(modelRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Deactivate " + name + "?",
                "Confirm Deactivation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                adminService.inactivateDoctor(doctorId);
                doctorModel.setValueAt("Inactive", modelRow, 5);
                JOptionPane.showMessageDialog(this, "Doctor deactivated successfully.");

            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(this,
                        e.getMessage(),
                        "Deactivate Doctor Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private DoctorSchedule.Day toDoctorDay(String day) {
        return DoctorSchedule.Day.valueOf(day.trim().toUpperCase());
    }

    private void preselectDayBoxes(String schedule, JComboBox<String> fromBox, JComboBox<String> toBox) {
        try {
            String daysPart = schedule.split("\\|")[0].trim();
            String[] days = daysPart.split("-");

            if (days.length >= 2) {
                String from = days[0].trim();
                String to = days[1].trim();

                for (int i = 0; i < FULL_DAYS.length; i++) {
                    if (FULL_DAYS[i].equalsIgnoreCase(from)) {
                        fromBox.setSelectedIndex(i);
                    }

                    if (FULL_DAYS[i].equalsIgnoreCase(to)) {
                        toBox.setSelectedIndex(i);
                    }
                }
            }

        } catch (Exception ignored) {
        }
    }

    private void preselectShift(String schedule, JComboBox<String> shiftBox) {
        try {
            String shiftPart = schedule.split("\\|")[1].trim();

            for (int i = 0; i < SHIFTS.length; i++) {
                if (SHIFTS[i].equalsIgnoreCase(shiftPart)) {
                    shiftBox.setSelectedIndex(i);
                    break;
                }
            }

        } catch (Exception ignored) {
        }
    }
}