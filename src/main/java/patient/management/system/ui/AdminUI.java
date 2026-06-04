package patient.management.system.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

final class AdminUI {
    static final Color NAVY = UITheme.NAVY;
    static final Color BLUE = UITheme.BLUE;
    static final Color LIGHT = UITheme.BACKGROUND;
    static final Color WHITE = Color.WHITE;
    static final Color GREEN = UITheme.SUCCESS;
    static final Color RED = UITheme.DANGER;
    static final Color ORANGE = UITheme.WARNING;
    static final Color PURPLE = new Color(0x8E5AB5);
    static final Color SIDEBAR_BUTTON = new Color(0x173B5C);
    static final Color SIDEBAR_HOVER = new Color(0x214F78);
    static final Color SIDEBAR_SHADOW = new Color(0x092238);

    private AdminUI() {
    }

    static JPanel basePanel(String heading, String subtitle) {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(22, 28, 20, 28));

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setBackground(LIGHT);

        JLabel titleLabel = new JLabel(heading);
        titleLabel.setFont(UITheme.TITLE_FONT);
        titleLabel.setForeground(NAVY);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(UITheme.BODY_FONT);
        subLabel.setForeground(UITheme.MUTED_TEXT);

        top.add(titleLabel);
        top.add(subLabel);

        panel.add(top, BorderLayout.NORTH);
        return panel;
    }

    static JPanel infoCard(String title, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JPanel bar = new JPanel();
        bar.setBackground(accent);
        bar.setPreferredSize(new Dimension(0, 4));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.BODY_FONT);
        titleLabel.setForeground(UITheme.MUTED_TEXT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(UITheme.TITLE_FONT);
        valueLabel.setForeground(accent);

        JPanel body = new JPanel(new GridLayout(2, 1, 0, 4));
        body.setBackground(WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        body.add(titleLabel);
        body.add(valueLabel);

        card.add(bar, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);

        return card;
    }

    static JTable table(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(34);
        table.setSelectionBackground(new Color(0xD6EAF8));
        table.setSelectionForeground(NAVY);
        table.setGridColor(UITheme.TABLE_GRID);
        table.setShowGrid(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setFont(UITheme.BUTTON_FONT);
        header.setBackground(NAVY);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        return table;
    }

    static JScrollPane tableCard(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        scrollPane.getViewport().setBackground(WHITE);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        return scrollPane;
    }

    static JPanel searchBar(String placeholder, JTable table, int... searchColumns) {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setBackground(LIGHT);
        bar.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        JTextField search = new JTextField();
        search.setFont(UITheme.BODY_FONT);
        search.setToolTipText(placeholder);
        search.setPreferredSize(new Dimension(320, 34));
        search.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));

        JLabel label = new JLabel(placeholder);
        label.setFont(UITheme.LABEL_FONT);
        label.setForeground(UITheme.TEXT);

        TableRowSorter<DefaultTableModel> sorter =
                new TableRowSorter<>((DefaultTableModel) table.getModel());

        table.setRowSorter(sorter);

        search.getDocument().addDocumentListener(new DocumentListener() {
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
                String query = search.getText().trim();

                if (query.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query, searchColumns));
                }
            }
        });

        JPanel input = new JPanel(new BorderLayout(0, 5));
        input.setBackground(LIGHT);
        input.add(label, BorderLayout.NORTH);
        input.add(search, BorderLayout.CENTER);

        bar.add(input, BorderLayout.CENTER);
        return bar;
    }

    static JPanel buttonBar(JButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        panel.setBackground(LIGHT);

        for (JButton button : buttons) {
            panel.add(button);
        }

        return panel;
    }

    static JButton actionButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setFont(UITheme.BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(bg);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(170, 36));
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

        return button;
    }

    static JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.LABEL_FONT);
        label.setForeground(NAVY);

        return label;
    }

    static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            JLabel label = new JLabel(value == null ? "" : value.toString(), SwingConstants.CENTER);
            label.setOpaque(true);
            label.setFont(UITheme.BUTTON_FONT);
            label.setForeground(Color.WHITE);

            String status = label.getText().toLowerCase();

            if (status.contains("available") || status.equals("active")) {
                label.setBackground(GREEN);
            } else if (status.contains("busy")) {
                label.setBackground(ORANGE);
            } else if (status.contains("inactive")) {
                label.setBackground(RED);
            } else {
                label.setBackground(Color.GRAY);
            }

            return label;
        }
    }

    static class TriageRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            JLabel label = new JLabel(value == null ? "" : value.toString(), SwingConstants.CENTER);
            label.setOpaque(true);
            label.setFont(UITheme.BUTTON_FONT);
            label.setForeground(Color.WHITE);

            String color = label.getText().toUpperCase();

            if (color.contains("RED")) {
                label.setBackground(new Color(0xC0392B));
            } else if (color.contains("YELLOW")) {
                label.setBackground(new Color(0xB8860B));
            } else if (color.contains("GREEN")) {
                label.setBackground(new Color(0x27AE60));
            } else if (color.contains("BLACK")) {
                label.setBackground(new Color(0x2C3E50));
            } else {
                label.setBackground(BLUE);
            }

            return label;
        }
    }
}