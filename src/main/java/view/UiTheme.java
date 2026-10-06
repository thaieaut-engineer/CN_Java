package view;

import java.awt.Color;
import java.awt.Font;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;

public final class UiTheme {
    public static final Color BLUE = new Color(37, 99, 235);
    public static final Color BLUE_DARK = new Color(29, 78, 216);
    public static final Color BLUE_PALE = new Color(239, 246, 255);
    public static final Color NAV = new Color(15, 35, 72);
    public static final Color NAV_LIGHT = new Color(24, 52, 101);
    public static final Color TEXT = new Color(30, 41, 59);
    public static final Color MUTED = new Color(100, 116, 139);
    public static final Color BORDER = new Color(226, 232, 240);
    public static final Color PAGE = new Color(245, 248, 253);
    public static final Color SURFACE = Color.WHITE;

    private UiTheme() {
    }

    public static void install() {
        UIManager.put("Panel.background", PAGE);
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 12);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("Component.focusColor", BLUE);
        UIManager.put("Component.borderColor", BORDER);
        UIManager.put("Table.rowHeight", 34);
        UIManager.put("TableHeader.background", new Color(239, 246, 255));
        UIManager.put("TableHeader.foreground", new Color(51, 65, 85));
        UIManager.put("TableHeader.separatorColor", BORDER);
        UIManager.put("ProgressBar.foreground", BLUE);
        UIManager.put("ScrollBar.thumb", new Color(191, 211, 243));
    }

    public static void stylePage(JComponent component) {
        component.setBackground(PAGE);
        component.setOpaque(true);
    }

    public static void styleSurface(JComponent component) {
        component.setBackground(SURFACE);
        component.setOpaque(true);
        Border border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(12, 14, 12, 14));
        component.setBorder(border);
    }

    public static void stylePrimary(AbstractButton button) {
        button.setBackground(BLUE);
        button.setForeground(Color.WHITE);
        button.setFont(button.getFont().deriveFont(Font.BOLD));
        button.setFocusPainted(false);
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.putClientProperty("JButton.defaultButton", Boolean.TRUE);
        button.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
    }

    public static void styleSecondary(AbstractButton button) {
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(51, 65, 85));
        button.setFocusPainted(false);
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 13, 8, 13)));
    }

    public static void styleDanger(AbstractButton button) {
        button.setBackground(new Color(254, 242, 242));
        button.setForeground(new Color(185, 28, 28));
        button.setFocusPainted(false);
        button.putClientProperty("JButton.buttonType", "roundRect");
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT);
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(239, 243, 249));
        table.setFillsViewportHeight(true);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new java.awt.Dimension(header.getPreferredSize().width, 38));
        header.setReorderingAllowed(false);
    }

    public static void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.putClientProperty("JComponent.roundRect", Boolean.TRUE);
    }
}
