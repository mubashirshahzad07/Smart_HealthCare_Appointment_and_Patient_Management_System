package patient.management.system.ui;

import patient.management.system.dto.PatientDTO;
import patient.management.system.model.TriageColor;
import patient.management.system.model.User;
import patient.management.system.service.ReceptionistService;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.RowFilter;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Dimension;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmergencyCaseEntryPanel extends JPanel {
    private final User loggedInUser;
    private final ReceptionistService receptionistService;
    private final DefaultTableModel patientModel;
    private final JTable patientTable;
    private final TableRowSorter<DefaultTableModel> patientSorter;
    private final JCheckBox temporaryIdCheckBox;
    private final JTextField nameField;
    private final JTextField ageField;
    private final JTextField genderField;
    private final JTextField cnicField;
    private final JTextField arrivalTimeField;
    private final JTextArea complaintArea;
    private final JTextArea triageRemarksArea;
    private final ButtonGroup triageGroup;
    private final java.util.List<JToggleButton> triageButtons;
    private String selectedTriageColor;

    public EmergencyCaseEntryPanel(User loggedInUser) {
        super(new BorderLayout(18, 18));
        this.loggedInUser = loggedInUser;
        this.receptionistService = new ReceptionistService();
        setBackground(UITheme.BACKGROUND);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 24, 22, 24));

        patientModel = buildPatientModel();
        patientTable = AppUI.table(patientModel);
        configurePatientTable();
        patientSorter = new TableRowSorter<>(patientModel);
        patientTable.setRowSorter(patientSorter);

        temporaryIdCheckBox = new JCheckBox("Patient not registered / unknown");
        nameField = AppUI.textField("Patient name or Unknown");
        ageField = AppUI.textField("Age or estimated age");
        genderField = AppUI.textField("Gender");
        cnicField = AppUI.textField("CNIC");
        arrivalTimeField = AppUI.textField("Arrival time");
        arrivalTimeField.setEditable(false);
        arrivalTimeField.setText(currentArrivalTime());
        complaintArea = AppUI.textArea("Initial complaint");
        triageRemarksArea = AppUI.textArea("Triage remarks");
        triageGroup = new ButtonGroup();
        triageButtons = new java.util.ArrayList<>();

        add(AppUI.pageTitle("Emergency Case"), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
        loadPatients();
        setTemporaryMode(false);
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new GridLayout(1, 2, 18, 18));
        content.setOpaque(false);
        content.add(buildCaseDetailsCard());
        content.add(buildTriageAndSearchCard());
        return content;
    }

    private JPanel buildCaseDetailsCard() {
        JPanel card = AppUI.cardPanel();
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        temporaryIdCheckBox.setFont(UITheme.BODY_FONT);
        temporaryIdCheckBox.setForeground(UITheme.TEXT);
        temporaryIdCheckBox.setBackground(UITheme.CARD);
        temporaryIdCheckBox.setFocusPainted(false);
        temporaryIdCheckBox.addActionListener(event -> {
            setTemporaryMode(temporaryIdCheckBox.isSelected());
            if (temporaryIdCheckBox.isSelected()) {
                patientTable.clearSelection();
            }
        });

        patientTable.getSelectionModel().addListSelectionListener(event -> {
            if (!temporaryIdCheckBox.isSelected() && !event.getValueIsAdjusting()) {
                fillSelectedPatientDetails();
            }
        });

        form.add(temporaryIdCheckBox, gbc(0, 0));
        form.add(AppUI.smallLabel("Patient Name / Label *"), gbc(0, 1));
        form.add(nameField, gbc(0, 2));
        form.add(AppUI.smallLabel("Age / Estimated Age *"), gbc(0, 3));
        form.add(ageField, gbc(0, 4));
        form.add(AppUI.smallLabel("Gender *"), gbc(0, 5));
        form.add(genderField, gbc(0, 6));
        form.add(AppUI.smallLabel("CNIC"), gbc(0, 7));
        form.add(cnicField, gbc(0, 8));
        form.add(AppUI.smallLabel("Arrival Time"), gbc(0, 9));
        form.add(arrivalTimeField, gbc(0, 10));
        form.add(AppUI.smallLabel("Initial Complaint *"), gbc(0, 11));
        form.add(complaintArea, gbc(0, 12));

        JButton createButton = AppUI.primaryButton("Register Emergency Case");
        JButton clearButton = AppUI.secondaryButton("Clear");
        createButton.addActionListener(event -> createEmergencyCase());
        clearButton.addActionListener(event -> clearForm());

        JPanel buttons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 14, 0));
        buttons.setOpaque(false);
        buttons.add(clearButton);
        buttons.add(createButton);

        card.add(form, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildTriageAndSearchCard() {
        JPanel card = AppUI.cardPanel();
        JPanel body = new JPanel(new BorderLayout(14, 14));
        body.setOpaque(false);

        JPanel triageSection = new JPanel(new BorderLayout(10, 10));
        triageSection.setOpaque(false);

        JPanel triageTop = new JPanel(new BorderLayout(6, 6));
        triageTop.setOpaque(false);
        triageTop.add(AppUI.smallLabel("Select Triage"), BorderLayout.NORTH);
        triageTop.add(buildTriageButtons(), BorderLayout.CENTER);

        JPanel remarksPanel = new JPanel(new BorderLayout(4, 4));
        remarksPanel.setOpaque(false);
        remarksPanel.add(AppUI.smallLabel("Triage Remarks *"), BorderLayout.NORTH);
        remarksPanel.add(triageRemarksArea, BorderLayout.CENTER);

        triageSection.add(triageTop, BorderLayout.NORTH);
        triageSection.add(remarksPanel, BorderLayout.CENTER);

        body.add(buildPatientSearchPanel(), BorderLayout.NORTH);
        body.add(triageSection, BorderLayout.CENTER);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildTriageButtons() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 8, 8));
        panel.setOpaque(false);
        panel.add(triageButton("RED", "Immediate", new Color(0xFFD6D6), new Color(0xB42318)));
        panel.add(triageButton("YELLOW", "Delayed", new Color(0xFFE9A8), new Color(0x8A5A00)));
        panel.add(triageButton("GREEN", "Minor", new Color(0xCFF5DE), new Color(0x087443)));
        panel.add(triageButton("BLACK", "Deceased", new Color(0xD8DEE6), new Color(0x24292F)));
        return panel;
    }

    private JToggleButton triageButton(String color, String detail, Color background, Color foreground) {
        JToggleButton button = new JToggleButton("<html><center>" + color + "<br><span style='font-size:9px'>" + detail + "</span></center></html>") {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(graphics);
            }
        };
        button.setFont(UITheme.BUTTON_FONT);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(foreground),
                javax.swing.BorderFactory.createEmptyBorder(12, 6, 12, 6)
        ));
        button.putClientProperty("fillColor", background);
        button.putClientProperty("textColor", foreground);
        button.addActionListener(event -> {
            selectedTriageColor = color;
            refreshTriageButtons();
        });
        triageGroup.add(button);
        triageButtons.add(button);
        return button;
    }

    private void refreshTriageButtons() {
        for (JToggleButton button : triageButtons) {
            Color fillColor = (Color) button.getClientProperty("fillColor");
            Color textColor = (Color) button.getClientProperty("textColor");

            button.setBackground(fillColor);
            button.setForeground(textColor);
            if (button.isSelected()) {
                button.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                        javax.swing.BorderFactory.createLineBorder(textColor, 3),
                        javax.swing.BorderFactory.createEmptyBorder(10, 4, 10, 4)
                ));
            } else {
                button.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                        javax.swing.BorderFactory.createLineBorder(textColor),
                        javax.swing.BorderFactory.createEmptyBorder(12, 6, 12, 6)
                ));
            }
        }
    }

    private JPanel buildPatientSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setOpaque(false);

        JTextField searchField = AppUI.textField("Search registered patient by name or CNIC");
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

        JLabel label = new JLabel("Search Registered Patient");
        label.setFont(UITheme.LABEL_FONT);
        label.setForeground(UITheme.TEXT);

        JPanel top = new JPanel(new BorderLayout(8, 6));
        top.setOpaque(false);
        top.add(label, BorderLayout.NORTH);
        top.add(searchField, BorderLayout.CENTER);

        JScrollPane scrollPane = AppUI.tableScrollPane(patientTable);
        int fourRowsHeight = patientTable.getRowHeight() * 4 + patientTable.getTableHeader().getPreferredSize().height + 4;
        scrollPane.setPreferredSize(new Dimension(420, fourRowsHeight));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private DefaultTableModel buildPatientModel() {
        return new DefaultTableModel(new String[]{"ID", "Name", "CNIC", "Age", "Gender"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void loadPatients() {
        try {
            patientModel.setRowCount(0);
            List<PatientDTO> patients = receptionistService.getPatients();

            for (PatientDTO patient : patients) {
                patientModel.addRow(new Object[]{
                        patient.getPatientId(),
                        patient.getName(),
                        patient.getCnic(),
                        patient.getAge(),
                        patient.getGender()
                });
            }
        } catch (RuntimeException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "Unable to Load Patients",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void configurePatientTable() {
        patientTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        patientTable.getColumnModel().getColumn(0).setPreferredWidth(65);
        patientTable.getColumnModel().getColumn(1).setPreferredWidth(145);
        patientTable.getColumnModel().getColumn(2).setPreferredWidth(135);
        patientTable.getColumnModel().getColumn(3).setPreferredWidth(55);
        patientTable.getColumnModel().getColumn(4).setPreferredWidth(80);
    }

    private GridBagConstraints gbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 6, 5, 6);
        return gbc;
    }

    private void filterPatients(String searchText) {
        String text = searchText.trim();
        if (text.isEmpty()) {
            patientSorter.setRowFilter(null);
        } else {
            patientSorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text), 1, 2));
        }
    }

    private void fillSelectedPatientDetails() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        int modelRow = patientTable.convertRowIndexToModel(selectedRow);
        nameField.setText(patientModel.getValueAt(modelRow, 1).toString());
        cnicField.setText(patientModel.getValueAt(modelRow, 2).toString());
        ageField.setText(patientModel.getValueAt(modelRow, 3).toString());
        genderField.setText(patientModel.getValueAt(modelRow, 4).toString());
        arrivalTimeField.setText(currentArrivalTime());
    }

    private void setTemporaryMode(boolean temporaryMode) {
        boolean editable = temporaryMode;
        nameField.setEditable(editable);
        ageField.setEditable(editable);
        genderField.setEditable(editable);
        cnicField.setEditable(editable);

        if (!temporaryMode) {
            nameField.setText("");
            ageField.setText("");
            genderField.setText("");
            cnicField.setText("");
        }
        arrivalTimeField.setText(currentArrivalTime());
    }

    private void createEmergencyCase() {
        try {
            boolean temporary = temporaryIdCheckBox.isSelected();
            String complaint = complaintArea.getText().trim();
            String remarks = triageRemarksArea.getText().trim();

            if (!temporary && patientTable.getSelectedRow() < 0) {
                throw new IllegalArgumentException("Select a registered patient or create a temporary ID.");
            }
            if (nameField.getText().trim().isEmpty()
                    || ageField.getText().trim().isEmpty()
                    || genderField.getText().trim().isEmpty()
                    || complaint.isEmpty()
                    || selectedTriageColor == null
                    || remarks.isEmpty()) {
                throw new IllegalArgumentException("All required fields must be filled.");
            }

            int age = Integer.parseInt(ageField.getText().trim());
            if (age <= 0) {
                throw new IllegalArgumentException("Age must be greater than 0.");
            }

            receptionistService.registerEmergencyCase(
                    temporary ? null : selectedPatientId(),
                    temporary,
                    nameField.getText().trim(),
                    age,
                    genderField.getText().trim(),
                    cnicField.getText().trim(),
                    complaint,
                    TriageColor.valueOf(selectedTriageColor),
                    remarks
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Emergency case registered successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
            clearForm();
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(this, "Age must be a number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "Unable to Register Emergency Case",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void refreshPatients() {
        clearForm();
        loadPatients();
    }

    private String selectedPatientId() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow < 0) {
            throw new IllegalArgumentException("Select a registered patient or create a temporary ID.");
        }

        int modelRow = patientTable.convertRowIndexToModel(selectedRow);
        return patientModel.getValueAt(modelRow, 0).toString();
    }

    private void clearForm() {
        patientTable.clearSelection();
        temporaryIdCheckBox.setSelected(false);
        setTemporaryMode(false);
        complaintArea.setText("");
        triageRemarksArea.setText("");
        triageGroup.clearSelection();
        selectedTriageColor = null;
        refreshTriageButtons();
        arrivalTimeField.setText(currentArrivalTime());
    }

    private String currentArrivalTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
