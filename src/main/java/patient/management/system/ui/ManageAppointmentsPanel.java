package patient.management.system.ui;

import patient.management.system.dto.AppointmentDTO;
import patient.management.system.model.User;
import patient.management.system.service.ReceptionistService;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageAppointmentsPanel extends JPanel {
    private final User loggedInUser;
    private final ReceptionistService receptionistService;
    private final DefaultTableModel appointmentModel;
    private final JTable appointmentTable;
    private final TableRowSorter<DefaultTableModel> appointmentSorter;
    private final JComboBox<AppointmentSlot> slotBox;
    private final JComboBox<String> rescheduleBox;
    private final JTextArea descriptionArea;
    private final JLabel selectedAppointmentLabel;
    private final JLabel selectedDoctorLabel;
    private final Map<String, AppointmentDTO> appointmentsById;

    public ManageAppointmentsPanel() {
        this(null);
    }

    public ManageAppointmentsPanel(User loggedInUser) {
        super(new BorderLayout(18, 18));
        this.loggedInUser = loggedInUser;
        this.receptionistService = new ReceptionistService();
        this.appointmentsById = new HashMap<>();

        setBackground(UITheme.BACKGROUND);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 24, 22, 24));

        appointmentModel = buildAppointmentModel();
        appointmentTable = AppUI.table(appointmentModel);
        appointmentSorter = new TableRowSorter<>(appointmentModel);
        appointmentTable.setRowSorter(appointmentSorter);

        slotBox = new JComboBox<>();
        rescheduleBox = AppUI.comboBox(new String[]{"Select option", "Yes", "No"});
        descriptionArea = AppUI.textArea("Updated patient description");
        descriptionArea.setPreferredSize(new Dimension(360, 80));
        descriptionArea.setMinimumSize(new Dimension(360, 80));
        descriptionArea.setMaximumSize(new Dimension(360, 80));
        selectedAppointmentLabel = valueLabel("No appointment selected");
        selectedDoctorLabel = valueLabel("No doctor selected");

        add(AppUI.pageTitle("Manage Appointments"), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);

        clearSlots();
        loadAppointments();
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout(18, 18));
        content.setOpaque(false);
        content.add(buildUpdateCard(), BorderLayout.NORTH);
        content.add(buildTableCard(), BorderLayout.CENTER);
        return content;
    }

    private JPanel buildUpdateCard() {
        JPanel card = AppUI.cardPanel();
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        styleComboBox(slotBox);

        form.add(AppUI.smallLabel("Selected Appointment"), gbc(0, 0));
        form.add(selectedAppointmentLabel, gbc(0, 1));
        form.add(AppUI.smallLabel("Doctor"), gbc(1, 0));
        form.add(selectedDoctorLabel, gbc(1, 1));
        form.add(AppUI.smallLabel("New Slot"), gbc(2, 0));
        form.add(slotBox, gbc(2, 1));
        form.add(AppUI.smallLabel("Description"), gbc(0, 2));

        GridBagConstraints descriptionGbc = gbc(0, 3);
        descriptionGbc.gridwidth = 2;
        form.add(descriptionArea, descriptionGbc);

        form.add(AppUI.smallLabel("Allow Early Reschedule?"), gbc(2, 2));
        form.add(rescheduleBox, gbc(2, 3));

        JButton updateButton = AppUI.primaryButton("Update Appointment");
        JButton cancelButton = AppUI.dangerButton("Cancel Appointment");
        JButton clearButton = AppUI.secondaryButton("Clear");
        updateButton.addActionListener(event -> updateAppointment());
        cancelButton.addActionListener(event -> cancelAppointment());
        clearButton.addActionListener(event -> clearForm());

        JPanel buttons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 14, 0));
        buttons.setOpaque(false);
        buttons.add(clearButton);
        buttons.add(cancelButton);
        buttons.add(updateButton);

        card.add(form, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildTableCard() {
        JPanel card = AppUI.cardPanel();
        card.add(buildSearchPanel(), BorderLayout.NORTH);
        card.add(AppUI.tableScrollPane(appointmentTable), BorderLayout.CENTER);

        appointmentTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                updateSelectedAppointment();
            }
        });
        return card;
    }

    private JPanel buildSearchPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setOpaque(false);

        JTextField searchField = AppUI.textField("Search by appointment, doctor, patient, or status");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                filterAppointments(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                filterAppointments(searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                filterAppointments(searchField.getText());
            }
        });

        JLabel hint = new JLabel("Only scheduled and rescheduled appointments are shown here.");
        hint.setFont(UITheme.BODY_FONT);
        hint.setForeground(UITheme.MUTED_TEXT);

        panel.add(searchField);
        panel.add(hint);
        return panel;
    }

    private DefaultTableModel buildAppointmentModel() {
        return new DefaultTableModel(
                new String[]{
                        "Appointment ID", "Doctor", "Patient", "Date", "Time",
                        "Status", "Allow Early Reschedule", "Description"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void loadAppointments() {
        try {
            appointmentModel.setRowCount(0);
            appointmentsById.clear();

            for (AppointmentDTO appointment : receptionistService.getAppointments()) {
                if (!isManageable(appointment.getStatus())) {
                    continue;
                }

                appointmentsById.put(appointment.getAppointmentId(), appointment);
                appointmentModel.addRow(new Object[]{
                        appointment.getAppointmentId(),
                        appointment.getDoctorName(),
                        appointment.getPatientName(),
                        appointment.getAppointmentDate(),
                        formatHour(appointment.getAppointmentHour()),
                        appointment.getStatus(),
                        appointment.isWillingToReschedule() ? "Yes" : "No",
                        appointment.getPatientDescription()
                });
            }
        } catch (RuntimeException exception) {
            showError(exception.getMessage(), "Unable to Load Appointments");
        }
    }

    public void refreshAppointments() {
        clearForm();
        loadAppointments();
    }

    private boolean isManageable(String status) {
        return "SCHEDULED".equalsIgnoreCase(status) || "RESCHEDULED".equalsIgnoreCase(status);
    }

    private GridBagConstraints gbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(6, 8, 6, 8);
        return gbc;
    }

    private JLabel valueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.BODY_FONT);
        label.setForeground(UITheme.MUTED_TEXT);
        return label;
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(UITheme.BODY_FONT);
        comboBox.setBackground(UITheme.FIELD_BACKGROUND);
        comboBox.setForeground(UITheme.TEXT);
        comboBox.setFocusable(false);
        comboBox.setPreferredSize(new Dimension(260, 34));
        comboBox.setMinimumSize(new Dimension(260, 34));
        comboBox.setMaximumSize(new Dimension(260, 34));
    }

    private void filterAppointments(String searchText) {
        String text = searchText.trim();
        if (text.isEmpty()) {
            appointmentSorter.setRowFilter(null);
        } else {
            appointmentSorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
        }
    }

    private void updateSelectedAppointment() {
        try {
            AppointmentDTO appointment = selectedAppointment();
            selectedAppointmentLabel.setText(appointment.getAppointmentId() + " | " + appointment.getPatientName());
            selectedDoctorLabel.setText(appointment.getDoctorName());
            descriptionArea.setText(appointment.getPatientDescription());
            rescheduleBox.setSelectedItem(appointment.isWillingToReschedule() ? "Yes" : "No");
            loadSlots(appointment.getDoctorId());
        } catch (IllegalArgumentException exception) {
            clearSelectionDetails();
        }
    }

    private void loadSlots(String doctorId) {
        slotBox.removeAllItems();
        try {
            List<String> slots = receptionistService.getDoctorAvailableSlotsThisWeek(doctorId);
            if (slots.isEmpty()) {
                slotBox.addItem(AppointmentSlot.message("No available slots this week"));
                return;
            }

            for (String slot : slots) {
                slotBox.addItem(AppointmentSlot.fromBackend(slot));
            }
        } catch (RuntimeException exception) {
            slotBox.addItem(AppointmentSlot.message("Unable to load slots"));
            showError(exception.getMessage(), "Unable to Load Slots");
        }
    }

    private void clearSlots() {
        slotBox.removeAllItems();
        slotBox.addItem(AppointmentSlot.message("Select an appointment first"));
    }

    private void updateAppointment() {
        try {
            AppointmentDTO appointment = selectedAppointment();
            AppointmentSlot slot = (AppointmentSlot) slotBox.getSelectedItem();
            String description = descriptionArea.getText().trim();
            String reschedule = (String) rescheduleBox.getSelectedItem();

            if (slot == null || !slot.isRealSlot()
                    || description.isEmpty() || reschedule == null || reschedule.startsWith("Select")) {
                throw new IllegalArgumentException("All fields must be filled.");
            }

            receptionistService.rescheduleAppointment(
                    appointment.getAppointmentId(),
                    slot.getYear(),
                    slot.getMonth(),
                    slot.getDay(),
                    slot.getHour(),
                    appointment.getDoctorId(),
                    reschedule.equals("Yes"),
                    description,
                    appointment.getDoctorName()
            );

            JOptionPane.showMessageDialog(this, "Appointment updated as RESCHEDULED.");
            clearForm();
            loadAppointments();
        } catch (IllegalArgumentException exception) {
            showWarning(exception.getMessage());
        } catch (RuntimeException exception) {
            showError(exception.getMessage(), "Unable to Update Appointment");
        }
    }

    private void cancelAppointment() {
        try {
            AppointmentDTO appointment = selectedAppointment();
            receptionistService.cancelAppointment(appointment.getAppointmentId(), appointment.getDoctorId());

            JOptionPane.showMessageDialog(
                    this,
                    "Appointment " + appointment.getAppointmentId() + " cancelled and removed from this list."
            );
            clearForm();
            loadAppointments();
        } catch (IllegalArgumentException exception) {
            showWarning(exception.getMessage());
        } catch (RuntimeException exception) {
            showError(exception.getMessage(), "Unable to Cancel Appointment");
        }
    }

    private AppointmentDTO selectedAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow < 0) {
            throw new IllegalArgumentException("Please select an appointment from the table.");
        }

        int modelRow = appointmentTable.convertRowIndexToModel(selectedRow);
        String appointmentId = appointmentModel.getValueAt(modelRow, 0).toString();
        AppointmentDTO appointment = appointmentsById.get(appointmentId);
        if (appointment == null) {
            throw new IllegalArgumentException("Please select an appointment from the table.");
        }
        return appointment;
    }

    private void clearForm() {
        appointmentTable.clearSelection();
        clearSelectionDetails();
    }

    private void clearSelectionDetails() {
        selectedAppointmentLabel.setText("No appointment selected");
        selectedDoctorLabel.setText("No doctor selected");
        clearSlots();
        descriptionArea.setText("");
        rescheduleBox.setSelectedIndex(0);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private String formatHour(int hour) {
        int displayHour = hour % 12 == 0 ? 12 : hour % 12;
        String suffix = hour < 12 ? "AM" : "PM";
        return String.format("%02d:00 %s", displayHour, suffix);
    }

    private static class AppointmentSlot {
        private final LocalDate date;
        private final int hour;
        private final String label;

        AppointmentSlot(LocalDate date, int hour, String label) {
            this.date = date;
            this.hour = hour;
            this.label = label;
        }

        static AppointmentSlot message(String label) {
            return new AppointmentSlot(null, -1, label);
        }

        static AppointmentSlot fromBackend(String value) {
            String[] parts = value.split(" - ");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Unexpected slot format: " + value);
            }

            int commaIndex = parts[0].indexOf(", ");
            String dateText = commaIndex >= 0 ? parts[0].substring(commaIndex + 2) : parts[0];
            int hour = Integer.parseInt(parts[1].substring(0, 2));
            return new AppointmentSlot(LocalDate.parse(dateText), hour, value);
        }

        boolean isRealSlot() {
            return date != null && hour >= 0;
        }

        int getYear() {
            return date.getYear();
        }

        int getMonth() {
            return date.getMonthValue();
        }

        int getDay() {
            return date.getDayOfMonth();
        }

        int getHour() {
            return hour;
        }

        @Override
        public String toString() {
            if (label != null) {
                return label;
            }
            return date.format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")) + " - " + formatHour(hour);
        }

        private static String formatHour(int hour) {
            int displayHour = hour % 12 == 0 ? 12 : hour % 12;
            String suffix = hour < 12 ? "AM" : "PM";
            return String.format("%02d:00 %s", displayHour, suffix);
        }
    }
}
