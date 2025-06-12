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
    private static final JLabel statusLabel = new JLabel("Drag & drop up to 3 images");
    private static final JPanel previewPanel = new JPanel();

    public static void main(String[] args) {
        JFrame frame = new JFrame("PicLayout Generator");
        frame.setSize(500, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        // Preview panel (vertical layout)
        previewPanel.setLayout(new BoxLayout(previewPanel, BoxLayout.Y_AXIS));
        previewPanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(previewPanel);
        scrollPane.setPreferredSize(new Dimension(460, 500));
        frame.add(scrollPane, BorderLayout.CENTER);

        // Top label
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(statusLabel, BorderLayout.NORTH);

        // Bottom generate button
        JButton generateBtn = new JButton("Generate Word Document");
        frame.add(generateBtn, BorderLayout.SOUTH);

        // Enable drag-and-drop anywhere
        new DropTarget(frame, new DropTargetAdapter() {
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
                    updatePreview();
                } catch (Exception ex) {
                    statusLabel.setText("❌ Error reading dropped files.");
                    ex.printStackTrace();
                }
            }
        });

        // Generate action
        generateBtn.addActionListener(e -> {
            String[] paths = new String[3];
            for (int i = 0; i < 3; i++) {
                if (i < userImages.size()) {
                    paths[i] = userImages.get(i).getAbsolutePath();
                } else {
                    paths[i] = "src/main/java/PicLayout/img/" + (i == 0 ? "black.png" : i == 1 ? "blue.png" : "red.png");
                }
            }
            PicLayout.main(paths);
            JOptionPane.showMessageDialog(frame, "✅ Word file created in /doc folder");
        });

        frame.setVisible(true);
    }

    private static void updatePreview() {
        previewPanel.removeAll();
        int width = 360;

        for (int i = 0; i < userImages.size(); i++) {
            File file = userImages.get(i);
            try {
                ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                int originalWidth = icon.getIconWidth();
                int originalHeight = icon.getIconHeight();
                int newHeight = (int) ((double) width / originalWidth * originalHeight);

                Image scaled = icon.getImage().getScaledInstance(width, newHeight, Image.SCALE_SMOOTH);
                JLabel imgLabel = new JLabel(new ImageIcon(scaled));

                // Container with remove button
                JPanel imageBox = new JPanel(new BorderLayout());
                JButton removeBtn = new JButton("X");
                int indexToRemove = i;
                removeBtn.addActionListener(e -> {
                    userImages.remove(indexToRemove);
                    updatePreview();
                });

                imageBox.add(removeBtn, BorderLayout.NORTH);
                imageBox.add(imgLabel, BorderLayout.CENTER);
                imageBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                previewPanel.add(imageBox);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        previewPanel.revalidate();
        previewPanel.repaint();
        statusLabel.setText(userImages.size() + " image(s) selected");
    }
}
