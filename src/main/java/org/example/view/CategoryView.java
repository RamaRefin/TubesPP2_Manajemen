package org.example.view;

import org.example.controller.CategoryController;
import org.example.model.WasteItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class CategoryView extends JFrame {

    private final CategoryController categoryController = new CategoryController();
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private static final Color PRIMARY_GREEN = new Color(34, 139, 34);
    private static final Color DANGER_RED = new Color(220, 53, 69);
    private static final Color GRAY = new Color(128, 128, 128);
    private JTextField itemNameField;
    private JComboBox<Object> itemComboBox;

    public CategoryView() {
        setTitle("Kategori Sampah");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        add(mainPanel);

        // Table Panel
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Button Panel at the bottom
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        loadCategoryData();
        pack();
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Table Setup
        String[] columnNames = {"Kategori", "Jenis Sampah"};
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        categoryTable = new JTable(tableModel);

        // Styling the table
        styleTable();

        JScrollPane scrollPane = new JScrollPane(categoryTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_GREEN));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void styleTable() {
        // Header Styling
        JTableHeader header = categoryTable.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setFont(new Font("Arial", Font.BOLD, 14));
                c.setForeground(Color.WHITE);
                c.setBackground(PRIMARY_GREEN);
                setBorder(new LineBorder(PRIMARY_GREEN));
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        // Table Styling
        categoryTable.setFont(new Font("Arial", Font.PLAIN, 12));
        categoryTable.setRowHeight(30);
        categoryTable.setShowGrid(true);
        categoryTable.setGridColor(Color.LIGHT_GRAY);
        categoryTable.setSelectionBackground(new Color(200, 230, 200));
        categoryTable.setSelectionForeground(Color.BLACK);

        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        categoryTable.setDefaultRenderer(Object.class, centerRenderer);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Reduced horizontal gap
        panel.setBackground(Color.WHITE);

        // Add Button
        JButton addButton = createStyledButton("Tambah", PRIMARY_GREEN);
        addButton.addActionListener(e -> showAddDialog());

        // Edit Button
        JButton editButton = createStyledButton("Ubah", PRIMARY_GREEN);
        editButton.addActionListener(e -> showEditDialog());

        // Delete Button
        JButton deleteButton = createStyledButton("Hapus", PRIMARY_GREEN);
        deleteButton.addActionListener(e -> showDeleteDialog());

        // Cancel Button
        JButton cancelButton = createStyledButton("Cancel", GRAY);
        cancelButton.addActionListener(e -> dispose());

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(cancelButton);


        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(100, 30)); // Reduced button size
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12)); // Reduced font size
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(8, 1), // Reduced border radius
                new EmptyBorder(3, 10, 3, 10) // Reduced padding
        ));
        return button;
    }

    private void showAddDialog() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Mode Selection
        String[] modes = {"Kategori", "Jenis Sampah"};
        JComboBox<String> modeComboBox = new JComboBox<>(modes);
        panel.add(new JLabel("Pilih Tipe:"), gbc);
        gbc.gridy++;
        panel.add(modeComboBox, gbc);
        gbc.gridy++;

        // Category ComboBox (initially hidden)
        List<WasteItem> categories = categoryController.getAllWasteItems();
        JComboBox<WasteItem> categoryComboBox = new JComboBox<>(
                categories.stream()
                        .filter(item -> item.getItemType().equals("category"))
                        .toArray(WasteItem[]::new)
        );
        categoryComboBox.setVisible(false);


        JLabel categoryLabel = new JLabel("Pilih Kategori:");
        categoryLabel.setVisible(false);
        panel.add(categoryLabel, gbc);
        gbc.gridy++;
        panel.add(categoryComboBox, gbc);


        // Name Field
        panel.add(new JLabel("Nama:"), gbc);
        gbc.gridy++;
        itemNameField = new JTextField(20);
        panel.add(itemNameField, gbc);
        gbc.gridy++;


        // Add category selector when "Jenis Sampah" is selected
        modeComboBox.addActionListener(e -> {
            boolean isType = modeComboBox.getSelectedIndex() == 1;
            categoryComboBox.setVisible(isType);
            categoryLabel.setVisible(isType);

            ((Window) SwingUtilities.getWindowAncestor(panel)).pack();
            panel.revalidate();
            panel.repaint();
        });

        int result = JOptionPane.showConfirmDialog(this, panel, "Tambah Item",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String itemName = itemNameField.getText().trim();
            if (itemName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama tidak boleh kosong", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (modeComboBox.getSelectedIndex() == 0) {
                // Adding Category
                WasteItem newCategory = new WasteItem(0, itemName, "category", null);
                if (categoryController.addWasteItem(newCategory)) {
                    JOptionPane.showMessageDialog(this, "Kategori berhasil ditambahkan");
                    loadCategoryData();
                }
            } else {
                // Adding Type
                WasteItem selectedCategory = (WasteItem) categoryComboBox.getSelectedItem();
                if (selectedCategory == null) {
                    JOptionPane.showMessageDialog(this, "Pilih kategori terlebih dahulu");
                    return;
                }
                WasteItem newType = new WasteItem(0, itemName, "type", selectedCategory.getItemId());
                if (categoryController.addWasteItem(newType)) {
                    JOptionPane.showMessageDialog(this, "Jenis sampah berhasil ditambahkan");
                    loadCategoryData();
                }
            }
        }
    }

    private void showEditDialog() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item yang akan diubah", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String currentCategoryName = (String) categoryTable.getValueAt(selectedRow, 0);
        String currentTypeName = (String) categoryTable.getValueAt(selectedRow, 1);


        // Get all categories for the combo box
        List<WasteItem> categories = categoryController.getAllWasteItems();
        List<WasteItem> categoryList = categories.stream()
                .filter(item -> item.getItemType().equals("category"))
                .toList();

        JComboBox<WasteItem> categoryComboBox = new JComboBox<>(categoryList.toArray(new WasteItem[0]));

        // Set selected category
        WasteItem currentCategoryItem = categories.stream()
                .filter(item -> item.getItemName().equals(currentCategoryName) && item.getItemType().equals("category"))
                .findFirst()
                .orElse(null);

        categoryComboBox.setSelectedItem(currentCategoryItem);



        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);


        // Category ComboBox
        panel.add(new JLabel("Kategori Baru:"), gbc);
        gbc.gridy++;
        panel.add(categoryComboBox, gbc);
        gbc.gridy++;

        // Type Name Field
        panel.add(new JLabel("Jenis Sampah Baru:"), gbc);
        gbc.gridy++;
        JTextField newTypeNameField = new JTextField(currentTypeName, 20);
        panel.add(newTypeNameField, gbc);


        int result = JOptionPane.showConfirmDialog(this, panel, "Ubah Item",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {

            WasteItem selectedCategory = (WasteItem) categoryComboBox.getSelectedItem();
            String newTypeName = newTypeNameField.getText().trim();


            if (selectedCategory == null || newTypeName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Kategori dan Jenis Sampah tidak boleh kosong", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }


            // Find the item in the database and update it
            List<WasteItem> items = categoryController.getAllWasteItems();
            WasteItem typeToUpdate = items.stream()
                    .filter(item -> item.getItemName().equals(currentTypeName) && Objects.equals(item.getParentId(), currentCategoryItem.getItemId()))
                    .findFirst()
                    .orElse(null);


            if (typeToUpdate != null) {
                WasteItem updatedItem = new WasteItem(
                        typeToUpdate.getItemId(),
                        newTypeName,
                        "type",
                        selectedCategory.getItemId()
                );

                if (categoryController.updateWasteItem(updatedItem)) {
                    JOptionPane.showMessageDialog(this, "Item berhasil diubah");
                    loadCategoryData();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal mengubah item", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menemukan item yang akan diubah", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showDeleteDialog() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item yang akan dihapus", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selectedCategoryName = (String) categoryTable.getValueAt(selectedRow, 0);
        String selectedTypeName = (String) categoryTable.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah anda yakin ingin menghapus '" + selectedCategoryName + " - " + selectedTypeName + "'?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            List<WasteItem> items = categoryController.getAllWasteItems();
            WasteItem categoryToDelete = items.stream()
                    .filter(item -> item.getItemName().equals(selectedCategoryName) && item.getItemType().equals("category"))
                    .findFirst()
                    .orElse(null);

            if(categoryToDelete != null){
                if (categoryController.deleteWasteItem(categoryToDelete.getItemId())) {
                    JOptionPane.showMessageDialog(this, "Item berhasil dihapus");
                    loadCategoryData();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus item", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus item", "Error", JOptionPane.ERROR_MESSAGE);
            }


        }
    }


    private void loadCategoryData() {
        tableModel.setRowCount(0);
        List<WasteItem> items = categoryController.getAllWasteItems();

        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Data tidak ditemukan", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (WasteItem item : items) {
            if (item.getItemType().equals("category")) {
                List<WasteItem> types = categoryController.getWasteTypesByCategory(item.getItemId());
                if (!types.isEmpty()) {
                    for (WasteItem type : types) {
                        tableModel.addRow(new Object[]{item.getItemName(), type.getItemName()});
                    }
                } else {
                    tableModel.addRow(new Object[]{item.getItemName(), "Tidak ada jenis sampah"});
                }
            }
        }
    }

    static class RoundBorder extends LineBorder {
        private final int radius;

        RoundBorder(int radius, int thickness) {
            super(Color.LIGHT_GRAY, thickness, true);
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Shape round = new java.awt.geom.RoundRectangle2D.Float(x, y, width - 1, height - 1, radius, radius);
            g2d.setColor(getLineColor());
            g2d.setStroke(new BasicStroke(getThickness()));
            g2d.draw(round);
            g2d.dispose();
        }
    }

    public void display() {
        setVisible(true);
    }
}