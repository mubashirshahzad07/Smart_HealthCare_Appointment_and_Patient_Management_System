package patient.management.system.ui;

import patient.management.system.dto.DoctorDTO;
import patient.management.system.dto.PatientDTO;
import patient.management.system.model.Appointment;
import patient.management.system.model.Receptionist;
import patient.management.system.model.User;
import patient.management.system.service.ReceptionistService;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookAppointmentPanel extends JPanel {
    private final User loggedInUser;
    private final ReceptionistService receptionistService;
    private final DefaultTableModel patientModel;
    private final JTable patientTable;
    private final TableRowSorter<DefaultTableModel> patientSorter;
    private final JComboBox<DoctorOption> doctorBox;
    private final JComboBox<AppointmentSlot> slotBox;
    private final JComboBox<String> rescheduleBox;
    private final JTextArea descriptionArea;
    private final JLabel selectedPatientLabel;
    private final Map<String, PatientDTO> patientsById;

    public BookAppointmentPanel() {
        this(null);
    }

    public BookAppointmentPanel(User loggedInUser) {
        super(new BorderLayout(18, 18));
        this.loggedInUser = loggedInUser;
        this.receptionistService = new ReceptionistService();
        this.patientsById = new HashMap<>();

        setBackground(UITheme.BACKGROUND);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 24, 22, 24));

        patientModel = buildPatientModel();
        patientTable = AppUI.table(patientModel);
        configurePatientTable();
        patientSorter = new TableRowSorter<>(patientModel);
        patientTable.setRowSorter(patientSorter);

        doctorBox = new JComboBox<>();
        slotBox = new JComboBox<>();
        rescheduleBox = AppUI.comboBox(new String[]{"Select option", "Yes", "No"});
        descriptionArea = AppUI.textArea("Patient description / reason for appointment");
        selectedPatientLabel = new JLabel("No patient selected");

        add(AppUI.pageTitle("Book Appointment"), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);

        loadPatients();
        loadDoctors();
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new GridLayout(1, 2, 18, 18));
        content.setOpaque(false);
        content.add(buildBookingCard());
        content.add(buildPatientSelectionCard());
        return content;
    }

    private JPanel buildPatientSelectionCard() {
        JPanel card = AppUI.cardPanel();

        JTextField searchField = AppUI.textField("Search by patient name or CNIC");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                filterPatients(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                filterPatients(searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                filterPatients(searchField.getText());
            }
        });

        patientTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                updateSelectedPatient();
            }
        });

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setOpaque(false);
        top.add(new JLabel("Select Patient"), BorderLayout.NORTH);
        top.add(searchField, BorderLayout.CENTER);

        card.add(top, BorderLayout.NORTH);
        card.add(scrollPaneWithBottomBar(patientTable), BorderLayout.CENTER);
        return card;
    }

    private void configurePatientTable() {
        patientTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        patientTable.getColumnModel().getColumn(0).setPreferredWidth(75);
        patientTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        patientTable.getColumnModel().getColumn(2).setPreferredWidth(145);
        patientTable.getColumnModel().getColumn(3).setPreferredWidth(85);
        patientTable.getColumnModel().getColumn(4).setPreferredWidth(55);
    }

    private JScrollPane scrollPaneWithBottomBar(JTable table) {
        JScrollPane scrollPane = AppUI.tableScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }

    private JPanel buildBookingCard() {
        JPanel card = AppUI.cardPanel();
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        selectedPatientLabel.setFont(UITheme.BODY_FONT);
        selectedPatientLabel.setForeground(UITheme.MUTED_TEXT);

        styleComboBox(doctorBox);
        styleComboBox(slotBox);
        doctorBox.addActionListener(event -> updateSlots());

        form.add(AppUI.smallLabel("Selected Patient"), AppUI.gbc(0, 0));
        form.add(selectedPatientLabel, AppUI.gbc(0, 1));
        form.add(AppUI.smallLabel("Doctor / Specialization"), AppUI.gbc(0, 2));
        form.add(doctorBox, AppUI.gbc(0, 3));
        form.add(AppUI.smallLabel("Available Slot This Week"), AppUI.gbc(0, 4));
        form.add(slotBox, AppUI.gbc(0, 5));
        form.add(AppUI.smallLabel("Patient Description"), AppUI.gbc(0, 6));
        form.add(descriptionArea, AppUI.gbc(0, 7));
        form.add(AppUI.smallLabel("Willing To Be Rescheduled?"), AppUI.gbc(0, 8));
        form.add(rescheduleBox, AppUI.gbc(0, 9));

        JButton bookButton = AppUI.primaryButton("Book Appointment");
        JButton clearButton = AppUI.secondaryButton("Clear");
        bookButton.addActionListener(event -> bookAppointment());
        clearButton.addActionListener(event -> clearForm());

        JPanel buttons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 14, 0));
        buttons.setOpaque(false);
        buttons.add(clearButton);
        buttons.add(bookButton);

        card.add(form, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);
        return card;
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(UITheme.BODY_FONT);
        comboBox.setBackground(UITheme.FIELD_BACKGROUND);
        comboBox.setForeground(UITheme.TEXT);
        comboBox.setFocusable(false);
    }

    private DefaultTableModel buildPatientModel() {
        return new DefaultTableModel(new String[]{"ID", "Name", "CNIC", "Gender", "Age"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void loadPatients() {
        try {
            patientModel.setRowCount(0);
            patientsById.clear();

            for (PatientDTO patient : receptionistService.getPatients()) {
                patientsById.put(patient.getPatientId(), patient);
                patientModel.addRow(new Object[]{
                        patient.getPatientId(),
                        patient.getName(),
                        patient.getCnic(),
                        patient.getGender(),
                        patient.getAge()
                });
            }
        } catch (RuntimeException exception) {
            showError(exception.getMessage(), "Unable to Load Patients");
        }
    }

    private void loadDoctors() {
        doctorBox.removeAllItems();
        try {
            List<DoctorDTO> doctors = receptionistService.getActiveDoctors();
            if (doctors.isEmpty()) {
                doctorBox.addItem(DoctorOption.message("No active doctors available"));
                return;
            }

            doctorBox.addItem(DoctorOption.message("Select doctor"));
            for (DoctorDTO doctor : doctors) {
                doctorBox.addItem(new DoctorOption(doctor));
            }
            doctorBox.setSelectedIndex(0);
        } catch (RuntimeException exception) {
            doctorBox.addItem(DoctorOption.message("Unable to load doctors"));
            showError(exception.getMessage(), "Unable to Load Doctors");
        }
    }

    private void filterPatients(String searchText) {
        String text = searchText.trim();
        if (text.isEmpty()) {
            patientSorter.setRowFilter(null);
        } else {
            patientSorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text), 1, 2));
        }
    }

    private void updateSelectedPatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow < 0) {
            selectedPatientLabel.setText("No patient selected");
            return;
        }

        PatientDTO patient = selectedPatient();
        selectedPatientLabel.setText(
                patient.getPatientId() + " - " + patient.getName() + " (" + patient.getCnic() + ")"
        );
    }

    private void updateSlots() {
        slotBox.removeAllItems();
        DoctorOption doctor = (DoctorOption) doctorBox.getSelectedItem();
        if (doctor == null || !doctor.isRealDoctor()) {
            slotBox.addItem(AppointmentSlot.message("Select doctor first"));
            return;
        }

        try {
            List<String> slots = receptionistService.getDoctorAvailableSlotsThisWeek(doctor.getDoctorId());
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

    private void bookAppointment() {
        try {
            PatientDTO patient = selectedPatient();
            DoctorOption doctor = (DoctorOption) doctorBox.getSelectedItem();
            AppointmentSlot slot = (AppointmentSlot) slotBox.getSelectedItem();
            String description = descriptionArea.getText().trim();
            String reschedule = (String) rescheduleBox.getSelectedItem();

            if (doctor == null || !doctor.isRealDoctor()
                    || slot == null || !slot.isRealSlot()
                    || description.isEmpty()
                    || reschedule == null || reschedule.startsWith("Select")) {
                throw new IllegalArgumentException("All fields must be filled.");
            }

            Receptionist receptionist = receptionistService.getReceptionistByUserId(loggedInUser.getUserId());
            receptionistService.addAppointment(
                    slot.getYear(),
                    slot.getMonth(),
                    slot.getDay(),
                    slot.getHour(),
                    patient.getPatientId(),
                    doctor.getDoctorId(),
                    receptionist.getReceptionistId(),
                    description,
                    Appointment.Status.SCHEDULED,
                    reschedule.equals("Yes"),
                    patient.getName(),
                    doctor.getDoctorName()
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Appointment booked successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
            clearForm();
        } catch (IllegalArgumentException exception) {
            showWarning(exception.getMessage());
        } catch (RuntimeException exception) {
            showError(exception.getMessage(), "Unable to Book Appointment");
        }
    }

    private PatientDTO selectedPatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow < 0) {
            throw new IllegalArgumentException("Please select a patient from the table.");
        }

        int modelRow = patientTable.convertRowIndexToModel(selectedRow);
        String patientId = patientModel.getValueAt(modelRow, 0).toString();
        PatientDTO patient = patientsById.get(patientId);
        if (patient == null) {
            throw new IllegalArgumentException("Please select a patient from the table.");
        }
        return patient;
    }

    private void clearForm() {
        patientTable.clearSelection();
        selectedPatientLabel.setText("No patient selected");
        if (doctorBox.getItemCount() > 0) {
            doctorBox.setSelectedIndex(0);
        }
        updateSlots();
        descriptionArea.setText("");
        rescheduleBox.setSelectedIndex(0);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private static class DoctorOption {
        private final DoctorDTO doctor;
        private final String label;

        DoctorOption(DoctorDTO doctor) {
            this.doctor = doctor;
            this.label = doctor.getDoctorId() + " - " + doctor.getName() + " (" + doctor.getSpecialization() + ")";
        }

        private DoctorOption(String label) {
            this.doctor = null;
            this.label = label;
        }

        static DoctorOption message(String label) {
            return new DoctorOption(label);
        }

        boolean isRealDoctor() {
            return doctor != null;
        }

        String getDoctorId() {
            return doctor.getDoctorId();
        }

        String getDoctorName() {
            return doctor.getName();
        }

        @Override
        public String toString() {
            return label;
        }
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
            return label;
        }
    }
}
