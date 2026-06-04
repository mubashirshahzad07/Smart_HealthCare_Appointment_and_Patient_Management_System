package patient.management.system.ui;

import patient.management.system.dto.AppointmentDTO;
import patient.management.system.dto.DoctorDTO;
import patient.management.system.dto.PatientHistoryDTO;
import patient.management.system.model.User;
import patient.management.system.service.DoctorService;

import javax.swing.JButton;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DoctorMedicalRecordsPanel extends JPanel {
    private final User loggedInUser;
    private final DoctorService doctorService;
    private final DefaultTableModel appointmentModel;
    private final JTable appointmentTable;
    private final List<PatientHistoryDTO> allHistoryRecords;
    private final DefaultListModel<PatientHistoryDTO> historyListModel;
    private final JList<PatientHistoryDTO> historyList;
    private final JTextArea historyDetailArea;
    private final JLabel selectedAppointmentLabel;
    private final JTextArea diagnosisArea;
    private final JTextArea treatmentArea;
    private final JTextArea prescriptionArea;
    private final JTextArea notesArea;
    private final JTextField recordDateTimeField;
    private DoctorDTO loggedInDoctor;

    public DoctorMedicalRecordsPanel(User loggedInUser) {
        super(new BorderLayout(18, 18));
        this.loggedInUser = loggedInUser;
        this.doctorService = new DoctorService();
        setBackground(UITheme.BACKGROUND);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 24, 22, 24));

        appointmentModel = buildAppointmentModel();
        appointmentTable = AppUI.table(appointmentModel);
        configureAppointmentTable();

        allHistoryRecords = new ArrayList<>();
        historyListModel = new DefaultListModel<>();
        historyList = new JList<>(historyListModel);
        historyDetailArea = AppUI.textArea("Selected record details");
        historyDetailArea.setEditable(false);
        historyDetailArea.setText("Select a patient record to view its details.");
        configureHistoryList();
        refreshHistoryList("");

        selectedAppointmentLabel = mutedLabel("No appointment / patient selected");
        diagnosisArea = AppUI.textArea("Diagnosis");
        treatmentArea = AppUI.textArea("Treatment given");
        prescriptionArea = AppUI.textArea("Prescription");
        notesArea = AppUI.textArea("Additional notes");
        diagnosisArea.setRows(2);
        treatmentArea.setRows(2);
        prescriptionArea.setRows(2);
        notesArea.setRows(2);
        recordDateTimeField = AppUI.textField("Record date and time");
        recordDateTimeField.setEditable(false);
        recordDateTimeField.setText(currentRecordDateTime());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabs(), BorderLayout.CENTER);
        refreshPanel();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = AppUI.pageTitle("Medical Records");
        JLabel hint = new JLabel("Create patient records and review previous history.");
        hint.setFont(UITheme.BODY_FONT);
        hint.setForeground(UITheme.MUTED_TEXT);

        header.add(title, BorderLayout.WEST);
        header.add(hint, BorderLayout.EAST);
        return header;
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.BUTTON_FONT);
        tabs.addTab("New Record", buildNewRecordTab());
        tabs.addTab("Patient History", buildHistoryTab());
        return tabs;
    }

    private JPanel buildNewRecordTab() {
        JPanel content = new JPanel(new GridLayout(1, 2, 16, 16));
        content.setBackground(UITheme.BACKGROUND);
        content.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 0, 0, 0));
        content.add(buildRecordFormCard());
        content.add(buildAppointmentsCard());
        return content;
    }

    private JPanel buildRecordFormCard() {
        JPanel card = AppUI.cardPanel();
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        form.add(AppUI.smallLabel("Selected Appointment / Patient"), AppUI.gbc(0, 0));
        form.add(selectedAppointmentLabel, AppUI.gbc(0, 1));
        form.add(AppUI.smallLabel("Diagnosis *"), AppUI.gbc(0, 2));
        form.add(diagnosisArea, AppUI.gbc(0, 3));
        form.add(AppUI.smallLabel("Treatment Given *"), AppUI.gbc(0, 4));
        form.add(treatmentArea, AppUI.gbc(0, 5));
        form.add(AppUI.smallLabel("Prescription"), AppUI.gbc(0, 6));
        form.add(prescriptionArea, AppUI.gbc(0, 7));
        form.add(AppUI.smallLabel("Notes"), AppUI.gbc(0, 8));
        form.add(notesArea, AppUI.gbc(0, 9));
        form.add(AppUI.smallLabel("Record Date & Time"), AppUI.gbc(0, 10));
        form.add(recordDateTimeField, AppUI.gbc(0, 11));

        JButton clearButton = AppUI.secondaryButton("Clear");
        JButton saveButton = AppUI.primaryButton("Save Medical Record");
        clearButton.addActionListener(event -> clearForm());
        saveButton.addActionListener(event -> saveMedicalRecord());

        JPanel buttons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 14, 0));
        buttons.setOpaque(false);
        buttons.add(clearButton);
        buttons.add(saveButton);

        card.add(form, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildAppointmentsCard() {
        JPanel card = AppUI.cardPanel();
        JPanel body = new JPanel(new BorderLayout(8, 8));
        body.setOpaque(false);

        JTextField searchField = AppUI.textField("Search appointment");
        addSearchFilter(searchField, appointmentModel, appointmentTable);
        appointmentTable.getSelectionModel().addListSelectionListener(event -> updateSelectedAppointment());

        JScrollPane scrollPane = AppUI.tableScrollPane(appointmentTable);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        JPanel top = new JPanel(new BorderLayout(4, 4));
        top.setOpaque(false);
        top.add(new JLabel("Appointments"), BorderLayout.NORTH);
        top.add(searchField, BorderLayout.CENTER);

        body.add(top, BorderLayout.NORTH);
        body.add(scrollPane, BorderLayout.CENTER);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildHistoryTab() {
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(UITheme.BACKGROUND);
        content.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 0, 0, 0));

        JTextField searchField = AppUI.textField("Search by patient ID, patient name, or diagnosis");
        addHistorySearchFilter(searchField);

        JPanel historyListCard = AppUI.cardPanel();
        JPanel historyListHeader = new JPanel(new BorderLayout(4, 6));
        historyListHeader.setOpaque(false);
        historyListHeader.add(new JLabel("Patient Records"), BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new BorderLayout(4, 4));
        searchPanel.setOpaque(false);
        searchPanel.add(AppUI.smallLabel("Search Patient History"), BorderLayout.NORTH);
        searchPanel.add(searchField, BorderLayout.CENTER);

        historyListHeader.add(searchPanel, BorderLayout.CENTER);
        historyListCard.add(historyListHeader, BorderLayout.NORTH);
        historyListCard.add(new JScrollPane(historyList), BorderLayout.CENTER);

        JPanel detailCard = AppUI.cardPanel();
        detailCard.add(new JLabel("Record Details"), BorderLayout.NORTH);
        detailCard.add(new JScrollPane(historyDetailArea), BorderLayout.CENTER);

        JPanel records = new JPanel(new GridLayout(1, 2, 16, 16));
        records.setOpaque(false);
        records.add(historyListCard);
        records.add(detailCard);

        content.add(records, BorderLayout.CENTER);
        return content;
    }

    private DefaultTableModel buildAppointmentModel() {
        return readOnlyModel(
                new String[]{"Appointment ID", "Patient ID", "Patient Name", "Date", "Time", "Description", "Status"}
        );
    }

    private DefaultTableModel readOnlyModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void configureAppointmentTable() {
        appointmentTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {100, 80, 130, 100, 85, 190, 100};
        setColumnWidths(appointmentTable, widths);
    }

    private void configureHistoryList() {
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyList.setCellRenderer(new PatientHistoryRenderer());
        historyList.setFixedCellHeight(76);
        historyList.setBackground(UITheme.CARD);
        historyList.addListSelectionListener(event -> updateHistoryDetails());
    }

    private void setColumnWidths(JTable table, int[] widths) {
        for (int index = 0; index < widths.length; index++) {
            table.getColumnModel().getColumn(index).setPreferredWidth(widths[index]);
        }
    }

    private void addSearchFilter(JTextField searchField, DefaultTableModel model, JTable table) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent event) {
                filter();
            }

            public void removeUpdate(DocumentEvent event) {
                filter();
            }

            public void changedUpdate(DocumentEvent event) {
                filter();
            }

            private void filter() {
                String query = searchField.getText().trim();
                sorter.setRowFilter(query.isEmpty() ? null : javax.swing.RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(query)));
            }
        });
    }

    private void addHistorySearchFilter(JTextField searchField) {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent event) {
                filter();
            }

            public void removeUpdate(DocumentEvent event) {
                filter();
            }

            public void changedUpdate(DocumentEvent event) {
                filter();
            }

            private void filter() {
                refreshHistoryList(searchField.getText().trim());
            }
        });
    }

    private void refreshHistoryList(String query) {
        historyListModel.clear();
        String normalizedQuery = query.toLowerCase();

        for (PatientHistoryDTO record : allHistoryRecords) {
            if (historyMatches(record, normalizedQuery)) {
                historyListModel.addElement(record);
            }
        }

        historyDetailArea.setText("Select a patient record to view its details.");
    }

    public void refreshPanel() {
        try {
            if (loggedInDoctor == null) {
                loggedInDoctor = doctorService.getDoctorByUserId(loggedInUser.getUserId());
            }

            loadAppointments();
            loadHistoryRecords();
            refreshHistoryList("");
        } catch (RuntimeException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "Unable to Load Medical Records",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void loadAppointments() {
        try {
            appointmentModel.setRowCount(0);

            List<AppointmentDTO> appointments =
                    doctorService.getPendingMedicalRecordAppointments(loggedInDoctor.getDoctorId());

            for (AppointmentDTO appointment : appointments) {
                appointmentModel.addRow(new Object[]{
                        appointment.getAppointmentId(),
                        appointment.getPatientId(),
                        appointment.getPatientName(),
                        appointment.getAppointmentDate(),
                        formatHour(appointment.getAppointmentHour()),
                        appointment.getPatientDescription(),
                        appointment.getStatus()
                });
            }
        } catch (RuntimeException exception) {
            appointmentModel.setRowCount(0);
            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "Unable to Load Appointments",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void loadHistoryRecords() {
        try {
            allHistoryRecords.clear();

            allHistoryRecords.addAll(
                    doctorService.getPatientHistoryForDoctor(loggedInDoctor.getDoctorId())
            );
        } catch (RuntimeException exception) {
            allHistoryRecords.clear();
            historyListModel.clear();
            historyDetailArea.setText("Select a patient record to view its details.");
            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "Unable to Load Patient History",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateHistoryDetails() {
        PatientHistoryDTO record = historyList.getSelectedValue();
        if (record == null) {
            return;
        }

        historyDetailArea.setText(
                "Patient: " + value(record.getPatientName()) + " (" + value(record.getPatientId()) + ")\n"
                        + "Record ID: " + value(record.getMedicalRecordId()) + "\n"
                        + "Date & Time: " + value(record.getRecordDateTime()) + "\n"
                        + "Handled By: " + value(record.getHandledBy()) + "\n\n"
                        + "Diagnosis\n" + value(record.getDiagnosis()) + "\n\n"
                        + "Treatment Given\n" + value(record.getTreatmentGiven()) + "\n\n"
                        + "Prescription\n" + value(record.getPrescription()) + "\n\n"
                        + "Notes\n" + value(record.getNotes())
        );
        historyDetailArea.setCaretPosition(0);
    }

    private void updateSelectedAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow < 0) {
            selectedAppointmentLabel.setText("No appointment / patient selected");
            return;
        }

        int modelRow = appointmentTable.convertRowIndexToModel(selectedRow);
        selectedAppointmentLabel.setText(
                appointmentModel.getValueAt(modelRow, 0) + " | " + appointmentModel.getValueAt(modelRow, 2)
        );
        recordDateTimeField.setText(currentRecordDateTime());
    }

    private void saveMedicalRecord() {
        try {
            if (appointmentTable.getSelectedRow() < 0
                    || diagnosisArea.getText().trim().isEmpty()
                    || treatmentArea.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Select an appointment and fill all required fields.");
            }

            int modelRow = appointmentTable.convertRowIndexToModel(appointmentTable.getSelectedRow());
            String appointmentId = appointmentModel.getValueAt(modelRow, 0).toString();

            doctorService.updateMedicalRecord(
                    appointmentId,
                    diagnosisArea.getText().trim(),
                    treatmentArea.getText().trim(),
                    prescriptionArea.getText().trim(),
                    notesArea.getText().trim()
            );

            JOptionPane.showMessageDialog(this, "Medical record saved successfully.");
            clearForm();
            refreshPanel();
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Unable to Save Medical Record", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        appointmentTable.clearSelection();
        selectedAppointmentLabel.setText("No appointment / patient selected");
        diagnosisArea.setText("");
        treatmentArea.setText("");
        prescriptionArea.setText("");
        notesArea.setText("");
        recordDateTimeField.setText(currentRecordDateTime());
    }

    private JLabel mutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.BODY_FONT);
        label.setForeground(UITheme.MUTED_TEXT);
        return label;
    }

    private String currentRecordDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private String formatHour(int hour) {
        int displayHour = hour % 12 == 0 ? 12 : hour % 12;
        String suffix = hour < 12 ? "AM" : "PM";
        return String.format("%02d:00 %s", displayHour, suffix);
    }

    private String value(String value) {
        return value == null ? "" : value;
    }

    private boolean historyMatches(PatientHistoryDTO record, String query) {
        return query.isEmpty()
                || value(record.getPatientId()).toLowerCase().contains(query)
                || value(record.getPatientName()).toLowerCase().contains(query)
                || value(record.getDiagnosis()).toLowerCase().contains(query);
    }

    private class PatientHistoryRenderer extends JPanel implements ListCellRenderer<PatientHistoryDTO> {
        private final JLabel patientLabel;
        private final JLabel summaryLabel;

        PatientHistoryRenderer() {
            super(new GridLayout(2, 1, 0, 4));
            patientLabel = new JLabel();
            summaryLabel = new JLabel();
            patientLabel.setFont(UITheme.BUTTON_FONT);
            summaryLabel.setFont(UITheme.BODY_FONT);
            summaryLabel.setForeground(UITheme.MUTED_TEXT);
            setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 10, 8, 10));
            add(patientLabel);
            add(summaryLabel);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends PatientHistoryDTO> list, PatientHistoryDTO record,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            patientLabel.setText(value(record.getPatientName()) + " | " + value(record.getPatientId()));
            summaryLabel.setText(value(record.getRecordDateTime()) + " | " + value(record.getDiagnosis()));
            setBackground(isSelected ? UITheme.SOFT_BLUE : (index % 2 == 0 ? Color.WHITE : new Color(0xF6FAFE)));
            setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createMatteBorder(
                            0, isSelected ? 4 : 0, 1, 0,
                            isSelected ? UITheme.BLUE : UITheme.TABLE_GRID
                    ),
                    javax.swing.BorderFactory.createEmptyBorder(8, isSelected ? 8 : 12, 8, 10)
            ));
            return this;
        }
    }
}
