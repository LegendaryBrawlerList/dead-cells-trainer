package com.deadcells.trainer;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Swing-based GUI for the Dead Cells Trainer.
 * Provides buttons to modify common in-game values like health, gold, and cells.
 */
public class TrainerGUI extends JFrame {

    private final MemoryManager memoryManager;
    private final Map<String, Long> addressMap;
    private final JLabel statusLabel;

    /**
     * Construct the trainer window.
     *
     * @param memoryManager initialized memory manager
     * @param addresses     map of feature names to memory addresses (discovered via pattern scanning)
     */
    public TrainerGUI(MemoryManager memoryManager, Map<String, Long> addresses) {
        this.memoryManager = memoryManager;
        this.addressMap = new HashMap<>(addresses);

        setTitle("Dead Cells Trainer v1.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLayout(new BorderLayout());

        // Status bar
        statusLabel = new JLabel("Ready - Process attached");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(statusLabel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        addButton(buttonPanel, "Set Health to Max", () -> writeValue("health", 999));
        addButton(buttonPanel, "Add 1000 Gold", () -> {
            int current = memoryManager.readInt(addressMap.get("gold"));
            return writeValue("gold", current + 1000);
        });
        addButton(buttonPanel, "Set Cells to 999", () -> writeValue("cells", 999));
        addButton(buttonPanel, "Freeze Health", () -> freezeValue("health", 999));

        add(buttonPanel, BorderLayout.CENTER);

        // Exit button
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> {
            memoryManager.close();
            System.exit(0);
        });
        add(exitButton, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addButton(JPanel panel, String label, Supplier<Boolean> action) {
        JButton button = new JButton(label);
        button.addActionListener(e -> {
            boolean success = action.get();
            statusLabel.setText(success ? label + " - Success" : label + " - Failed");
        });
        panel.add(button);
    }

    private boolean writeValue(String key, int value) {
        Long address = addressMap.get(key);
        if (address == null) return false;
        return memoryManager.writeInt(address, value);
    }

    private boolean freezeValue(String key, int value) {
        // Simple freeze: write value repeatedly (in real trainer, use a timer thread)
        Long address = addressMap.get(key);
        if (address == null) return false;
        boolean allOk = true;
        for (int i = 0; i < 5; i++) {
            if (!memoryManager.writeInt(address, value)) {
                allOk = false;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        }
        return allOk;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        MemoryManager memManager = new MemoryManager("deadcells.exe");

        // Example addresses (would be discovered via AOB scanning in real project)
        Map<String, Long> addresses = new HashMap<>();
        addresses.put("health", 0x00A1B2C3L);
        addresses.put("gold", 0x00D4E5F6L);
        addresses.put("cells", 0x0078A9BCL);

        SwingUtilities.invokeLater(() -> new TrainerGUI(memManager, addresses));
    }
}
