package in.spstech;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

public class ExtractKeynotesData {

    public static void main(String[] args) {
        String pdfPath = "/Users/sps/code/homeopathy-scraper/src/main/resources/keynotes-allen.pdf"; // Update with actual path
        String outputCsv = "keynotes_data.csv";

        try {
            List<String[]> extractedData = extractDataFromPDF(pdfPath);
            saveToCSV(extractedData, outputCsv);
            System.out.println("Data extraction complete. Output saved to: " + outputCsv);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> extractDataFromPDF(String filePath) throws IOException {
        List<String[]> data = new ArrayList<>();

        // Load PDF
        PDDocument document = PDDocument.load(new File(filePath));
        PDFTextStripper pdfStripper = new PDFTextStripper();

        // Extract text
        String text = pdfStripper.getText(document);
        document.close();

        // Define a regex pattern to match Medicine | Relationship | Aggravation
        Pattern pattern = Pattern.compile("^([A-Z][a-zA-Z\\s]+(?:\\\\.[a-zA-Z\\\\s]*)?)\\\\.(?:\\\\s*\\\\(([^)]*)\\\\))?\\\\s*Relationship\\\\s*:\\\\s*(.*?)\\\\s*Aggravates\\\\s*(.*?)(?=\\\\n|$)\n");
        Matcher matcher = pattern.matcher(text);

        // Parse using regex
        while (matcher.find()) {
            String medicine = matcher.group(1).trim();
            String relationship = matcher.group(2).trim();
            String aggravation = matcher.group(3).trim();
            data.add(new String[]{medicine, relationship, aggravation});
        }

        return data;
    }

    public static void saveToCSV(List<String[]> data, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("Medicine,Relationship,Aggravation\n");

            for (String[] row : data) {
                writer.write(row[0] + "," + row[1] + "," + row[2] + "\n");
            }

            System.out.println("Data successfully saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
