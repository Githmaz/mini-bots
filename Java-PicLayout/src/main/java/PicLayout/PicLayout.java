package PicLayout;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.util.Units;

import java.io.*;

public class PicLayout {
    public static void main(String[] args) {

        try {
            File outputFolder = new File("doc");
            if (!outputFolder.exists()) outputFolder.mkdir();

            XWPFDocument document = new XWPFDocument();

            // Use passed args as image paths
            String[] imagePaths = args;

            for (String imgPath : imagePaths) {
                XWPFParagraph paragraph = document.createParagraph();
                paragraph.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun run = paragraph.createRun();

                try (InputStream is = new FileInputStream(imgPath)) {
                    run.addPicture(is, Document.PICTURE_TYPE_PNG, imgPath, Units.toEMU(150), Units.toEMU(150));
                }

                document.createParagraph(); // Spacer
            }

            String outputPath = "doc/output.docx";
            try (FileOutputStream out = new FileOutputStream(outputPath)) {
                document.write(out);
            }

            System.out.println("✅ Word document created: " + outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

