package PicLayout;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.util.Units;

import java.io.*;


public class PicLayout {
    public static void main(String[] args) {
        try {
            // Create output folder
            File outputFolder = new File("doc");
            if (!outputFolder.exists()) outputFolder.mkdir();

            // Create the Word document
            XWPFDocument document = new XWPFDocument();

            // Images to insert
            String[] imagePaths = {
                    "src/main/java/PicLayout/img/black.png",
                    "src/main/java/PicLayout/img/blue.png",
                    "src/main/java/PicLayout/img/red.png"
            };


            for (String imgPath : imagePaths) {
                XWPFParagraph paragraph = document.createParagraph();
                paragraph.setAlignment(ParagraphAlignment.CENTER);

                XWPFRun run = paragraph.createRun();

                try (InputStream is = new FileInputStream(imgPath)) {
                    String imgFormat = getImageFormat(imgPath);
                    run.addPicture(is, Document.PICTURE_TYPE_PNG, imgPath, Units.toEMU(150), Units.toEMU(150));
                }

                document.createParagraph(); // Spacer
            }

            // Save the document
            String outputPath = "doc/output.docx";
            try (FileOutputStream out = new FileOutputStream(outputPath)) {
                document.write(out);
            }

            System.out.println("✅ Word document created: " + outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getImageFormat(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

//    private static int getPictureType(String format) {
//        return switch (format) {
//            case "emf" -> Document.PICTURE_TYPE_EMF;
//            case "wmf" -> Document.PICTURE_TYPE_WMF;
//            case "pict" -> Document.PICTURE_TYPE_PICT;
//            case "jpeg", "jpg" -> Document.PICTURE_TYPE_JPEG;
//            case "png" -> Document.PICTURE_TYPE_PNG;
//            case "dib" -> Document.PICTURE_TYPE_DIB;
//            case "gif" -> Document.PICTURE_TYPE_GIF;
//            case "tiff" -> Document.PICTURE_TYPE_TIFF;
//            case "eps" -> Document.PICTURE_TYPE_EPS;
//            case "bmp" -> Document.PICTURE_TYPE_BMP;
//            case "wpg" -> Document.PICTURE_TYPE_WPG;
//            default -> throw new IllegalArgumentException("Unsupported image format: " + format);
//        };

}
