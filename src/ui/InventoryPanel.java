package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import model.*;

/**
 * InventoryPanel
 * -------------------------
 * Responsible for displaying and managing motor parts inventory UI.
 * Includes stock management operations such as sale, restock,
 * discontinuation, and auto-threshold visualization.
 */
public class InventoryPanel extends JPanel {

    // Singleton instance for centralized inventory access
    private final DataStore store = DataStore.getInstance();

    private DefaultTableModel tableModel;
    private JTable table;

    public InventoryPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildButtonBar(), BorderLayout.SOUTH);

SwingUtilities.invokeLater(() -> refreshTable());
    }

    // ================= TABLE SECTION =================
    private JPanel buildTablePanel() {

        // Table column definitions (UI unchanged)
        String[] cols = {"Part #","Part Name","Stock","Threshold","Rack","Vendor","State"};

        tableModel = new DefaultTableModel(cols, 0) {
          @Override
            public boolean isCellEditable(int r, int c) {
                return false; // Prevent direct editing
            }
        };

        table = new JTable(tableModel);

        // Table styling
        table.setRowHeight(24);
        table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));

        table.getTableHeader().setBackground(new Color(33, 97, 140));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));

        // Custom renderer for state-based row coloring
        table.setDefaultRenderer(Object.class, new StateRenderer());

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder("Inventory — Parts List"));

        // Legend for UI clarity
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 2));
        legend.add(legendBox(new Color(255,243,205), "Low Stock"));
        legend.add(legendBox(new Color(255,205,210), "Out of Stock"));
        legend.add(legendBox(new Color(213,245,227), "Order Pending"));
        legend.add(legendBox(new Color(205,245,255), "Restocked"));
        legend.add(legendBox(new Color(220,220,220), "Discontinued"));
        legend.add(legendBox(Color.WHITE, "In Stock"));

        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.add(sp, BorderLayout.CENTER);
        p.add(legend, BorderLayout.SOUTH);

        return p;
    }

    // Creates colored legend labels
    private JLabel legendBox(Color c, String text) {
        JLabel l = new JLabel("  " + text + "  ");
        l.setOpaque(true);
        l.setBackground(c);
        l.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return l;
    }

    // ================= BUTTON BAR =================
    private JPanel buildButtonBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bar.setBackground(new Color(210, 220, 235));

        // Action buttons (UI unchanged)
        JButton addBtn = makeBtn("Add New Part", new Color(0, 150, 60));
        JButton saleBtn = makeBtn("Record Sale", new Color(0, 100, 180));

        // Renamed button as requested
        JButton thresholderBtn = makeBtn("Thresholder", new Color(100, 0, 160));

        JButton restockBtn = makeBtn("Restock Part", new Color(0, 130, 50));
        JButton discontinueBtn = makeBtn("Discontinue Part", new Color(180, 0, 0));
        JButton refreshBtn = makeBtn("Refresh Table", new Color(60, 60, 60));
        JButton deleteBtn = makeBtn("Delete Part", new Color(120, 0, 0));

        bar.add(addBtn);
        bar.add(saleBtn);
        bar.add(thresholderBtn);
        bar.add(restockBtn);
        bar.add(discontinueBtn);
        bar.add(refreshBtn);
        bar.add(deleteBtn);

        // Action bindings
        addBtn.addActionListener(e -> showAddPartDialog());
        saleBtn.addActionListener(e -> showRecordSaleDialog());

        // Shows auto-calculated threshold only (no manual input allowed)
        thresholderBtn.addActionListener(e -> showThresholderDialog());

        restockBtn.addActionListener(e -> showRestockDialog());
        discontinueBtn.addActionListener(e -> showDiscontinueDialog());
        refreshBtn.addActionListener(e -> refreshTable());
        deleteBtn.addActionListener(e -> showDeletePartDialog());

        return bar;
    }

    // Utility method for button styling
    private JButton makeBtn(String label, Color bg) {
        JButton b = new JButton(label);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setBorderPainted(false);
        return b;
    }

    // ================= ADD NEW PART =================
    private void showAddPartDialog() {

        JTextField tfPartNo = new JTextField(10);
        JTextField tfPartName = new JTextField(14);
        JTextField tfStock = new JTextField(6);
        JTextField tfRack = new JTextField(6);
        JTextField tfVendorId = new JTextField(8);

        JPanel form = new JPanel(new GridLayout(5, 2));
        form.add(new JLabel("Part Number:")); form.add(tfPartNo);
        form.add(new JLabel("Part Name:")); form.add(tfPartName);
        form.add(new JLabel("Current Stock:")); form.add(tfStock);
        form.add(new JLabel("Rack Number:")); form.add(tfRack);
        form.add(new JLabel("Vendor ID:")); form.add(tfVendorId);

        int r = JOptionPane.showConfirmDialog(this, form, "Add Part", JOptionPane.OK_CANCEL_OPTION);
        if (r != JOptionPane.OK_OPTION) return;

        try {
            // Threshold is ignored here (auto-calculated inside MotorPart)
            store.inventory.addPart(
                new MotorPart(
                    tfPartNo.getText(),
                    tfPartName.getText(),
                    Integer.parseInt(tfStock.getText()),
                    0,
                    tfRack.getText(),
                    tfVendorId.getText()
                )
            );

            refreshTable();

        } catch (NumberFormatException | NullPointerException ex){
            JOptionPane.showMessageDialog(this, "Invalid input");
        }
    }

    // ================= THRESHOLDER (AUTO DISPLAY ONLY) =================
    private void showThresholderDialog() {

        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a part first.");
            return;
        }

        MotorPart p = store.inventory.getPartsList().get(row);

        // Displays system-calculated threshold value only
        JOptionPane.showMessageDialog(this,
            "Auto Calculated Threshold: " + p.getThresholdValue(),
            "Thresholder",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ================= RECORD SALE =================
    private void showRecordSaleDialog() {

        int row = table.getSelectedRow();
        if (row == -1) return;

        String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity sold:");
        if (qtyStr == null) return;

        try {
            int qty = Integer.parseInt(qtyStr);
            MotorPart p = store.inventory.getPartsList().get(row);

            p.setCurrentStock(p.getCurrentStock() - qty);
            refreshTable();

        } catch (NumberFormatException | NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input");
        }
    }

    // ================= RESTOCK =================
    private void showRestockDialog() {

        int row = table.getSelectedRow();
        if (row == -1) return;

        String qtyStr = JOptionPane.showInputDialog(this, "Enter restock quantity:");
        if (qtyStr == null) return;

        try {
            int qty = Integer.parseInt(qtyStr);
            MotorPart p = store.inventory.getPartsList().get(row);

            p.receiveStock(qty);
            refreshTable();

        }catch (NumberFormatException | NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input");
        }
    }

    // ================= DISCONTINUE =================
    private void showDiscontinueDialog() {

        int row = table.getSelectedRow();
        if (row == -1) return;

        MotorPart p = store.inventory.getPartsList().get(row);
        p.discontinue();

        refreshTable();
    }

    // ================= TABLE REFRESH =================
    public void refreshTable() {

        tableModel.setRowCount(0);

        for (MotorPart p : store.inventory.getPartsList()) {
            tableModel.addRow(new Object[]{
                p.getPartNumber(),
                p.getPartName(),
                p.getCurrentStock(),
                p.getThresholdValue(),
                p.getRackNumber(),
                p.getVendorId(),
                p.getState().name()
            });
        }
    }

    // ================= DELETE PART =================
private void showDeletePartDialog() {

    int row = table.getSelectedRow();

    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Please select a part first.");
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to permanently delete this part?",
        "Confirm Delete",
        JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {

        // Remove from inventory list
        store.inventory.getPartsList().remove(row);

        refreshTable();

        JOptionPane.showMessageDialog(this, "Part deleted successfully.");
    }
}

    // ================= ROW COLOR RENDERER =================
    static class StateRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object v, boolean s, boolean f, int r, int c) {

            Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);

            String state = (String) t.getValueAt(r, 6);

         switch (state) {

    case "LOW_STOCK" -> comp.setBackground(new Color(255, 243, 205));

    case "OUT_OF_STOCK" -> comp.setBackground(new Color(255, 205, 210));

    default -> comp.setBackground(Color.WHITE);
}

return comp;

        }
    }
}