package PicLayout;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.util.Units;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.image.BufferedImage;
import java.io.*;
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
        frame.getContentPane().setBackground(new Color(0xDCDCDC));

        previewPanel.setLayout(new BoxLayout(previewPanel, BoxLayout.Y_AXIS));
        previewPanel.setBackground(new Color(0xF0F0F0));
        previewPanel.setPreferredSize(new Dimension(460, 400));
        JScrollPane scrollPane = new JScrollPane(previewPanel);
        scrollPane.setPreferredSize(new Dimension(460, 400));
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        topPanel.setBackground(frame.getContentPane().getBackground());
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(statusLabel, BorderLayout.CENTER);
        frame.add(topPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);
        JButton generateBtn = styledButton("Generate Word", new Color(0x007BFF));
        JButton clearBtn = styledButton("Remove All", new Color(0xDC3545));
        buttonPanel.add(clearBtn);
        buttonPanel.add(generateBtn);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        new DropTarget(frame, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent event) {
                try {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
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

        clearBtn.addActionListener(e -> {
            userImages.clear();
            updatePreview();
        });

        generateBtn.addActionListener(e -> {
            String[] paths = new String[3];
            for (int i = 0; i < 3; i++) {
                paths[i] = i < userImages.size()
                        ? userImages.get(i).getAbsolutePath()
                        : "src/main/java/PicLayout/img/" + (i == 0 ? "black.png" : i == 1 ? "blue.png" : "red.png");
            }

            try {
                File outputFolder = new File("doc");
                if (!outputFolder.exists()) outputFolder.mkdir();
                XWPFDocument document = new XWPFDocument();
                for (String imgPath : paths) {
                    XWPFParagraph paragraph = document.createParagraph();
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    try (InputStream is = new FileInputStream(imgPath)) {
                        BufferedImage bufferedImage = ImageIO.read(new File(imgPath));
                        int originalWidth = bufferedImage.getWidth();
                        int originalHeight = bufferedImage.getHeight();
                        int targetWidthEMU = Units.toEMU(200);
                        double aspectRatio = (double) originalHeight / originalWidth;
                        int targetHeightEMU = (int) (targetWidthEMU * aspectRatio);
                        run.addPicture(is, Document.PICTURE_TYPE_PNG, imgPath, targetWidthEMU, targetHeightEMU);
                    }
                    document.createParagraph();
                }
                String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
                String outputPath = desktopPath + File.separator + "output.docx";
                try (FileOutputStream out = new FileOutputStream(outputPath)) {
                    document.write(out);
                }
                JOptionPane.showMessageDialog(frame, "✅ Word file created on Desktop");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "❌ Failed to create Word document");
                ex.printStackTrace();
            }
        });

        frame.setVisible(true);
    }

    private static void updatePreview() {
        previewPanel.removeAll();
        int maxTotalHeight = 390;
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
                int maxPanelWidth = 340;
                if (targetWidth > maxPanelWidth) {
                    targetWidth = maxPanelWidth;
                    targetHeight = (int) (targetWidth / aspectRatio);
                }
                Image scaled = icon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                JLabel imgLabel = new JLabel(new ImageIcon(scaled));

                JPanel imageBox = new JPanel(new BorderLayout());
                imageBox.setBackground(Color.WHITE);

                JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
                topPanel.setOpaque(false);

                JButton removeBtn = styledButton("✕", new Color(0x6C757D));
                removeBtn.setPreferredSize(new Dimension(30, 25));
                removeBtn.setFont(new Font("Arial", Font.BOLD, 12));
                removeBtn.setMargin(new Insets(0, 0, 0, 0));
                removeBtn.setFocusable(false);
                removeBtn.setBorder(BorderFactory.createEmptyBorder());

                int indexToRemove = i;
                removeBtn.addActionListener(e -> {
                    userImages.remove(indexToRemove);
                    updatePreview();
                });

                topPanel.add(removeBtn);
                imageBox.add(topPanel, BorderLayout.NORTH);
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

    private static JButton styledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
            }
        });
        return btn;
    }
}
