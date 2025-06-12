package PicLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class PicLayoutApp {

    private static final List<File> userImages = new ArrayList<>();
    private static final JPanel previewPanel = new JPanel();
    private static final JLabel statusLabel = new JLabel("Drag & drop up to 3 images");
    private static final JButton generateBtn = new JButton("Generate Word Document");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PicLayoutApp::createUI);
    }

    private static void createUI() {
        JFrame frame = new JFrame("📄 PicLayout App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 450);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null); // Center window

        // Top instructions
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(statusLabel, BorderLayout.CENTER);
        frame.add(topPanel, BorderLayout.NORTH);

        // Image preview panel
        previewPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JScrollPane scrollPane = new JScrollPane(previewPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        frame.add(scrollPane, BorderLayout.CENTER);

        // Bottom control panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton clearBtn = new JButton("Remove All");
        clearBtn.addActionListener(e -> {
            userImages.clear();
            refreshPreview();
        });

        generateBtn.setEnabled(false);
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
            JOptionPane.showMessageDialog(frame, "✅ DOCX created at /doc/output.docx");
        });

        bottomPanel.add(clearBtn);
        bottomPanel.add(generateBtn);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Drag and drop support
        new DropTarget(previewPanel, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent event) {
                try {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> dropped = (List<File>) event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File file : dropped) {
                        if (userImages.size() < 3 && file.getName().matches(".*\\.(png|jpg|jpeg)")) {
                            userImages.add(file);
                        }
                    }
                    refreshPreview();
                } catch (Exception ex) {
                    statusLabel.setText("❌ Drop failed.");
                    ex.printStackTrace();
                }
            }
        });

        frame.setVisible(true);
    }

    private static void refreshPreview() {
        previewPanel.removeAll();
        for (int i = 0; i < userImages.size(); i++) {
            File imgFile = userImages.get(i);
            JPanel imgHolder = new JPanel(new BorderLayout());
            imgHolder.setPreferredSize(new Dimension(130, 130));
            imgHolder.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            try {
                BufferedImage img = ImageIO.read(imgFile);
                ImageIcon icon = new ImageIcon(img.getScaledInstance(120, 100, Image.SCALE_SMOOTH));
                JLabel imgLabel = new JLabel(icon);
                imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
                imgHolder.add(imgLabel, BorderLayout.CENTER);

                JButton removeBtn = new JButton("X");
                removeBtn.setForeground(Color.RED);
                removeBtn.setMargin(new Insets(2, 5, 2, 5));
                removeBtn.setFont(new Font("Arial", Font.BOLD, 12));
                int indexToRemove = i;
                removeBtn.addActionListener(e -> {
                    userImages.remove(indexToRemove);
                    refreshPreview();
                });

                imgHolder.add(removeBtn, BorderLayout.NORTH);
                previewPanel.add(imgHolder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        statusLabel.setText(userImages.size() + " image(s) selected");
        generateBtn.setEnabled(true);
        previewPanel.revalidate();
        previewPanel.repaint();
    }
}
