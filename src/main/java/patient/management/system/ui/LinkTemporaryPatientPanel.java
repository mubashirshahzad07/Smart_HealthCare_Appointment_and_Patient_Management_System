package patient.management.system.ui;

import patient.management.system.model.User;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

public class LinkTemporaryPatientPanel extends JPanel {
    private final User loggedInUser;
    private final DefaultTableModel tempModel;
    private final DefaultTableModel patientModel;
    private final JTable tempTable;
    private final JTable patientTable;
    private final TableRowSorter<DefaultTableModel> tempSorter;
    private final TableRowSorter<DefaultTableModel> patientSorter;
    private final JTextField nameField;
    private final JTextField ageField;
    private final JTextField genderField;
    private final JTextField phoneField;
    private final JTextField cnicField;
    private final JTextField emailField;
    private final JLabel selectedTempLabel;
    private final JLabel selectedPatientLabel;
    private boolean registeredPatientSelected;

    public LinkTemporaryPatientPanel(User loggedInUser) {
        super(new BorderLayout(18, 18));
        this.loggedInUser = loggedInUser;
        setBackground(UITheme.BACKGROUND);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 24, 22, 24));

        tempModel = buildTempModel();
        patientModel = buildPatientModel();
        tempTable = AppUI.table(tempModel);
        patientTable = AppUI.table(patientModel);
        configureTables();
        tempSorter = new TableRowSorter<>(tempModel);
        patientSorter = new TableRowSorter<>(patientModel);
        tempTable.setRowSorter(tempSorter);
        patientTable.setRowSorter(patientSorter);

        nameField = AppUI.textField("Patient name");
        ageField = AppUI.textField("Age");
        genderField = AppUI.textField("Gender");
        phoneField = AppUI.textField("Phone number");
        cnicField = AppUI.textField("CNIC");
        emailField = AppUI.textField("Email");
        selectedTempLabel = new JLabel("No temporary case selected");
        selectedPatientLabel = new JLabel("No registered patient selected");

        add(AppUI.pageTitle("Link Patient Records"), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new GridLayout(1, 2, 18, 18));
        content.setOpaque(false);
        content.add(buildDetailsCard());
        content.add(buildTablesCard());
        return content;
    }

    private JPanel buildDetailsCard() {
        JPanel card = AppUI.cardPanel();
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        selectedTempLabel.setFont(UITheme.BODY_FONT);
        selectedTempLabel.setForeground(UITheme.MUTED_TEXT);
        selectedPatientLabel.setFont(UITheme.BODY_FONT);
        selectedPatientLabel.setForeground(UITheme.MUTED_TEXT);

        form.add(AppUI.smallLabel("Selected Temporary Case"), formGbc(0, 0, 2));
        form.add(selectedTempLabel, formGbc(0, 1, 2));
        form.add(AppUI.smallLabel("Selected Registered Patient"), formGbc(0, 2, 2));
        form.add(selectedPatientLabel, formGbc(0, 3, 2));
        form.add(AppUI.smallLabel("Patient Name *"), formGbc(0, 4, 2));
        form.add(nameField, formGbc(0, 5, 2));
        form.add(AppUI.smallLabel("Age *"), formGbc(0, 6, 1));
        form.add(AppUI.smallLabel("Gender *"), formGbc(1, 6, 1));
        form.add(ageField, formGbc(0, 7, 1));
        form.add(genderField, formGbc(1, 7, 1));
        form.add(AppUI.smallLabel("Phone Number"), formGbc(0, 8, 2));
        form.add(phoneField, formGbc(0, 9, 2));
        form.add(AppUI.smallLabel("CNIC *"), formGbc(0, 10, 2));
        form.add(cnicField, formGbc(0, 11, 2));
        form.add(AppUI.smallLabel("Email *"), formGbc(0, 12, 2));
        form.add(emailField, formGbc(0, 13, 2));

        JButton linkButton = AppUI.primaryButton("Link Patient");
        JButton registerButton = AppUI.accentButton("Register and Link Patient", UITheme.TEAL);
        JButton clearButton = AppUI.secondaryButton("Clear");
        linkButton.addActionListener(event -> linkExisting());
        registerButton.addActionListener(event -> registerAndLink());
        clearButton.addActionListener(event -> clearScreen());

        JPanel buttons = new JPanel(new GridLayout(2, 1, 8, 8));
        buttons.setOpaque(false);

        JPanel topButtons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 12, 0));
        topButtons.setOpaque(false);
        topButtons.add(clearButton);
        topButtons.add(linkButton);

        JPanel bottomButtons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 12, 0));
        bottomButtons.setOpaque(false);
        bottomButtons.add(registerButton);

        buttons.add(topButtons);
        buttons.add(bottomButtons);

        card.add(form, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);
        return card;
    }

    private GridBagConstraints formGbc(int x, int y, int width) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 8, 5, 8);
        return gbc;
    }

    private JPanel buildTablesCard() {
        JPanel card = AppUI.cardPanel();
        JPanel tables = new JPanel(new GridLayout(2, 1, 12, 12));
        tables.setOpaque(false);
        tables.add(buildTempTablePanel());
        tables.add(buildPatientTablePanel());
        card.add(tables, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildTempTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setOpaque(false);

        JTextField searchField = AppUI.textField("Search temporary cases");
        searchField.getDocument().addDocumentListener(filterListener(searchField, tempSorter));
        tempTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                fillFromTemporaryCase();
            }
        });

        JPanel top = new JPanel(new BorderLayout(8, 6));
        top.setOpaque(false);
        top.add(AppUI.smallLabel("Search Temporary Patient"), BorderLayout.NORTH);
        top.add(searchField, BorderLayout.CENTER);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPaneWithBottomBar(tempTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildPatientTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setOpaque(false);

        JTextField searchField = AppUI.textField("Search registered patients");
        searchField.getDocument().addDocumentListener(filterListener(searchField, patientSorter));
        patientTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                fillFromRegisteredPatient();
            }
        });

        JPanel top = new JPanel(new BorderLayout(8, 6));
        top.setOpaque(false);
        top.add(AppUI.smallLabel("Search Registered Patient"), BorderLayout.NORTH);
        top.add(searchField, BorderLayout.CENTER);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPaneWithBottomBar(patientTable), BorderLayout.CENTER);
        return panel;
    }

    private DefaultTableModel buildTempModel() {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Temp ID", "Case ID", "Name", "Age", "Gender", "Phone", "CNIC", "Email", "Triage"},
                0
        );
        model.addRow(new Object[]{"TEMP001", "E101", "Unknown Male", "45", "Male", "", "", "", "RED"});
        model.addRow(new Object[]{"TEMP002", "E103", "Unknown", "30", "Unknown", "0300-9988776", "", "", "YELLOW"});
        model.addRow(new Object[]{"TEMP003", "E104", "Ayesha", "22", "Female", "", "35202-0000000-5", "", "GREEN"});
        return model;
    }

    private DefaultTableModel buildPatientModel() {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Patient ID", "Name", "Age", "Gender", "Phone", "CNIC", "Email"},
                0
        );
        model.addRow(new Object[]{"P101", "Ali Raza", "29", "Male", "0312-3456789", "35202-1234567-1", "ali@gmail.com"});
        model.addRow(new Object[]{"P102", "Sara Ali", "24", "Female", "0300-1112233", "35202-7654321-2", "sara@gmail.com"});
        model.addRow(new Object[]{"P103", "Usman Shah", "33", "Male", "0321-4445566", "35201-1111111-3", "usman@gmail.com"});
        return model;
    }

    private void configureTables() {
        tempTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tempTable.getColumnModel().getColumn(0).setPreferredWidth(85);
        tempTable.getColumnModel().getColumn(1).setPreferredWidth(75);
        tempTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        tempTable.getColumnModel().getColumn(3).setPreferredWidth(55);
        tempTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        tempTable.getColumnModel().getColumn(5).setPreferredWidth(115);
        tempTable.getColumnModel().getColumn(6).setPreferredWidth(135);
        tempTable.getColumnModel().getColumn(7).setPreferredWidth(150);
        tempTable.getColumnModel().getColumn(8).setPreferredWidth(70);

        patientTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        patientTable.getColumnModel().getColumn(0).setPreferredWidth(85);
        patientTable.getColumnModel().getColumn(1).setPreferredWidth(130);
        patientTable.getColumnModel().getColumn(2).setPreferredWidth(55);
        patientTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        patientTable.getColumnModel().getColumn(4).setPreferredWidth(115);
        patientTable.getColumnModel().getColumn(5).setPreferredWidth(135);
        patientTable.getColumnModel().getColumn(6).setPreferredWidth(150);
    }

    private JScrollPane scrollPaneWithBottomBar(JTable table) {
        JScrollPane scrollPane = AppUI.tableScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }

    private DocumentListener filterListener(JTextField searchField, TableRowSorter<DefaultTableModel> sorter) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                filter(searchField.getText(), sorter);
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                filter(searchField.getText(), sorter);
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                filter(searchField.getText(), sorter);
            }
        };
    }

    private void filter(String searchText, TableRowSorter<DefaultTableModel> sorter) {
        String text = searchText.trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
        }
    }

    private void fillFromTemporaryCase() {
        int selectedRow = tempTable.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        int row = tempTable.convertRowIndexToModel(selectedRow);
        selectedTempLabel.setText(tempModel.getValueAt(row, 0) + " | Case " + tempModel.getValueAt(row, 1));

        if (registeredPatientSelected) {
            return;
        }

        nameField.setText(value(row, 2, tempModel));
        ageField.setText(value(row, 3, tempModel));
        genderField.setText(value(row, 4, tempModel));
        phoneField.setText(value(row, 5, tempModel));
        cnicField.setText(value(row, 6, tempModel));
        emailField.setText(value(row, 7, tempModel));
    }

    private void fillFromRegisteredPatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        int row = patientTable.convertRowIndexToModel(selectedRow);
        selectedPatientLabel.setText(patientModel.getValueAt(row, 0) + " | " + patientModel.getValueAt(row, 1));
        registeredPatientSelected = true;

        nameField.setText(value(row, 1, patientModel));
        ageField.setText(value(row, 2, patientModel));
        genderField.setText(value(row, 3, patientModel));
        phoneField.setText(value(row, 4, patientModel));
        cnicField.setText(value(row, 5, patientModel));
        emailField.setText(value(row, 6, patientModel));
    }

    private String value(int row, int column, DefaultTableModel model) {
        Object value = model.getValueAt(row, column);
        return value == null ? "" : value.toString();
    }

    private void linkExisting() {
        try {
            if (tempTable.getSelectedRow() < 0 || patientTable.getSelectedRow() < 0) {
                throw new IllegalArgumentException("Select a temporary case and a registered patient.");
            }

            /*
             * Backend integration point:
             * Link selected temporaryPatientId/emergencyCaseId to selected registered patientId.
             * Registered patient fields are considered the reliable source.
             */
            JOptionPane.showMessageDialog(this, "Temporary patient linked to existing patient.");
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void registerAndLink() {
        try {
            if (tempTable.getSelectedRow() < 0
                    || nameField.getText().trim().isEmpty()
                    || ageField.getText().trim().isEmpty()
                    || genderField.getText().trim().isEmpty()
                    || cnicField.getText().trim().isEmpty()
                    || emailField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Select a temporary case and fill all required patient fields.");
            }

            Integer.parseInt(ageField.getText().trim());

            /*
             * Backend integration point:
             * Create registered Patient from fields, then link temp ID/emergency case to new patientId.
             */
            JOptionPane.showMessageDialog(this, "New patient registered and temporary case linked.");
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(this, "Age must be a number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearScreen() {
        tempTable.clearSelection();
        patientTable.clearSelection();
        registeredPatientSelected = false;
        selectedTempLabel.setText("No temporary case selected");
        selectedPatientLabel.setText("No registered patient selected");
        nameField.setText("");
        ageField.setText("");
        genderField.setText("");
        phoneField.setText("");
        cnicField.setText("");
        emailField.setText("");
    }
}
