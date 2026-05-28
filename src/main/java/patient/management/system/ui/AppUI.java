package patient.management.system.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public final class AppUI {
    private AppUI() {
    }

    public static JLabel pageTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.TITLE_FONT);
        label.setForeground(UITheme.NAVY);
        return label;
    }

    public static JLabel smallLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.LABEL_FONT);
        label.setForeground(UITheme.TEXT);
        return label;
    }

    public static JTextField textField(String tooltip) {
        JTextField field = new JTextField();
        field.setToolTipText(tooltip);
        field.setFont(UITheme.BODY_FONT);
        field.setForeground(UITheme.TEXT);
        field.setCaretColor(UITheme.BLUE);
        field.setBackground(UITheme.FIELD_BACKGROUND);
        field.setPreferredSize(new Dimension(180, 34));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(0, 9, 0, 9)
        ));
        return field;
    }

    public static JTextArea textArea(String tooltip) {
        JTextArea area = new JTextArea(4, 20);
        area.setToolTipText(tooltip);
        area.setFont(UITheme.BODY_FONT);
        area.setForeground(UITheme.TEXT);
        area.setCaretColor(UITheme.BLUE);
        area.setBackground(UITheme.FIELD_BACKGROUND);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(8, 9, 8, 9)
        ));
        return area;
    }

    public static JComboBox<String> comboBox(String[] values) {
        JComboBox<String> comboBox = new JComboBox<>(values);
        comboBox.setFont(UITheme.BODY_FONT);
        comboBox.setBackground(UITheme.FIELD_BACKGROUND);
        comboBox.setForeground(UITheme.TEXT);
        comboBox.setFocusable(false);
        comboBox.setPreferredSize(new Dimension(180, 34));
        return comboBox;
    }

    public static JButton primaryButton(String text) {
        return button(text, UITheme.BLUE, Color.WHITE);
    }

    public static JButton accentButton(String text, Color accentColor) {
        return button(text, accentColor, Color.WHITE);
    }

    public static JButton secondaryButton(String text) {
        return button(text, UITheme.FIELD_BACKGROUND, UITheme.TEXT);
    }

    public static JButton dangerButton(String text) {
        return button(text, UITheme.DANGER, Color.WHITE);
    }

    private static JButton button(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(UITheme.BUTTON_FONT);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        return button;
    }

    public static JPanel cardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    public static JPanel pagePanel() {
        JPanel panel = new JPanel(new BorderLayout(18, 18));
        panel.setBackground(UITheme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
        return panel;
    }

    public static JTable table(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(UITheme.BODY_FONT);
        table.setForeground(UITheme.TEXT);
        table.setRowHeight(28);
        table.setShowGrid(true);
        table.setGridColor(UITheme.TABLE_GRID);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setSelectionBackground(UITheme.SOFT_BLUE);
        table.setSelectionForeground(UITheme.TEXT);
        table.setDefaultEditor(Object.class, null);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component component = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column
                );

                if (isSelected) {
                    component.setBackground(UITheme.SOFT_BLUE);
                    component.setForeground(UITheme.TEXT);
                } else if (row % 2 == 0) {
                    component.setBackground(Color.WHITE);
                    component.setForeground(UITheme.TEXT);
                } else {
                    component.setBackground(new Color(0xF6FAFE));
                    component.setForeground(UITheme.TEXT);
                }

                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return component;
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setFont(UITheme.LABEL_FONT);
        header.setForeground(Color.WHITE);
        header.setBackground(UITheme.NAVY);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 34));
        header.setBorder(BorderFactory.createLineBorder(UITheme.NAVY));

        return table;
    }

    public static JScrollPane tableScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        scrollPane.getViewport().setBackground(UITheme.CARD);
        return scrollPane;
    }

    public static GridBagConstraints gbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        return gbc;
    }
}
