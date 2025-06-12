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
        frame.setSize(500, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        previewPanel.setLayout(new BoxLayout(previewPanel, BoxLayout.Y_AXIS));
        previewPanel.setBackground(Color.WHITE);
        previewPanel.setPreferredSize(new Dimension(460, 400));
        JScrollPane scrollPane = new JScrollPane(previewPanel);
        scrollPane.setPreferredSize(new Dimension(460, 400));
        frame.add(scrollPane, BorderLayout.CENTER);

        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(statusLabel, BorderLayout.NORTH);

        JButton generateBtn = new JButton("Generate Word Document");
        frame.add(generateBtn, BorderLayout.SOUTH);

        // Drag-and-drop enabled on whole frame
        new DropTarget(frame, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent event) {
                try {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) event.getTransferable()
                            .getTransferData(DataFlavor.javaFileListFlavor);

                    for (File file : droppedFiles) {
                        if (file.getName().toLowerCase().matches(".*\\.(png|jpg|jpeg)") && userImages.size() < 3) {
                            userImages.add(file);
                        }
                    }
                    updatePreview();
                } catch (Exception ex) {
                    statusLabel.setText("❌ Error reading dropped files.");
                    ex.printStackTrace();
                }
            }
        });

        generateBtn.addActionListener(e -> {
            String[] paths = new String[3];
            for (int i = 0; i < 3; i++) {
                paths[i] = i < userImages.size()
                        ? userImages.get(i).getAbsolutePath()
                        : "src/main/java/PicLayout/img/" + (i == 0 ? "black.png" : i == 1 ? "blue.png" : "red.png");
            }
            PicLayout.main(paths);
            JOptionPane.showMessageDialog(frame, "✅ Word file created in /doc folder");
        });

        frame.setVisible(true);
    }

    private static void updatePreview() {
        previewPanel.removeAll();
        int maxTotalHeight = 390; // Total height for preview area
        int maxPerImageHeight = maxTotalHeight / 3;

        for (int i = 0; i < userImages.size(); i++) {
            File file = userImages.get(i);
            try {
                ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                int originalWidth = icon.getIconWidth();
                int originalHeight = icon.getIconHeight();

                double aspectRatio = (double) originalWidth / originalHeight;
                int targetHeight = maxPerImageHeight;
                int targetWidth = (int) (targetHeight * aspectRatio);

                // If width is too wide for the panel, scale it down further
                int maxPanelWidth = 340;
                if (targetWidth > maxPanelWidth) {
                    targetWidth = maxPanelWidth;
                    targetHeight = (int) (targetWidth / aspectRatio);
                }

                Image scaled = icon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                JLabel imgLabel = new JLabel(new ImageIcon(scaled));

                JPanel imageBox = new JPanel(new BorderLayout());
                JButton removeBtn = new JButton("X");
                int indexToRemove = i;
                removeBtn.addActionListener(e -> {
                    userImages.remove(indexToRemove);
                    updatePreview();
                });

                imageBox.add(removeBtn, BorderLayout.NORTH);
                imageBox.add(imgLabel, BorderLayout.CENTER);
                imageBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
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
