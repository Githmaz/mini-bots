package PicLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PicLayoutApp {
    private static final List<File> userImages = new ArrayList<>();
    private static final JLabel statusLabel = new JLabel("Drag & drop up to 3 images here");

    public static void main(String[] args) {
        JFrame frame = new JFrame("PicLayout Generator");
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel dropPanel = new JPanel();
        dropPanel.setBorder(BorderFactory.createDashedBorder(Color.GRAY));
        dropPanel.setBackground(Color.LIGHT_GRAY);
        dropPanel.add(statusLabel);
        frame.add(dropPanel, BorderLayout.CENTER);

        JButton generateBtn = new JButton("Generate Word Document");
        frame.add(generateBtn, BorderLayout.SOUTH);

        // Enable drag-and-drop
        new DropTarget(dropPanel, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent event) {
                try {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) event.getTransferable()
                            .getTransferData(DataFlavor.javaFileListFlavor);

                    for (File file : droppedFiles) {
                        if (file.getName().toLowerCase().matches(".*\\.(png|jpg|jpeg)")) {
                            if (userImages.size() < 3) {
                                userImages.add(file);
                            }
                        }
                    }

                    statusLabel.setText(userImages.size() + " image(s) selected");
                } catch (Exception ex) {
                    statusLabel.setText("Error reading files.");
                    ex.printStackTrace();
                }
            }
        });

        // Button action
        generateBtn.addActionListener(e -> {
            // Use default images if less than 3 provided
            String[] paths = new String[3];
            for (int i = 0; i < 3; i++) {
                if (i < userImages.size()) {
                    paths[i] = userImages.get(i).getAbsolutePath();
                } else {
                    // Fallback to default image
                    paths[i] = "src/main/java/PicLayout/img/" + (i == 0 ? "black.png" : i == 1 ? "blue.png" : "red.png");
                }
            }

            // Call the core logic
            PicLayout.main(paths);
            JOptionPane.showMessageDialog(frame, "✅ Word file created in /doc folder");
        });

        frame.setVisible(true);
    }
}
