package in.spstech;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

public class ExtractKeynotesData {

    public static void main(String[] args) {
        String pdfPath = "src/main/resources/keynotes-allen.pdf"; // Update with actual path
        String outputCsv = "src/main/resources/keynotes_allen.csv";

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

       /* try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/kyenote-allen.txt"))) {
            writer.write(text);
            System.out.println("Data successfully written to the file.");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }*/

        String content = new String(Files.readAllBytes(Paths.get("/Users/sps/code/homeopathy-scraper/src/main/resources/kyenote-allen.txt")));

        // Regex pattern to extract medicine name, sub-name, and description
        String regex = "Keynotes by H\\.C\\. Allen\\s*\\n([A-Z][a-zA-Z\\s]+?)\\.\\s*(.*?)\\n(.*?)(?=(Keynotes by H\\.C\\. Allen|$))";

        // Compile the regex
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);

        // Print extracted data in table format
        System.out.printf("%-20s | %-30s | %s%n", "Medicine Name", "Sub Name", "Description");
        System.out.println("-------------------------------------------------------------------------------------------");

        while (matcher.find()) {
            String medicineName = matcher.group(1).trim().replaceAll(",", "");
            String subName = matcher.group(2).trim().replaceAll(",", "");
            String description = matcher.group(3).replaceAll("\\s+", " ").trim().replaceAll(",", "");
            data.add(new String[]{medicineName, subName, description});
            System.out.printf("%-20s | %-30s | %s%n", medicineName, subName, description);
        }

        return data;
    }

    public static void saveToCSV(List<String[]> data, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("Medicine,Sub-Name,Description\n");

            for (String[] row : data) {
                writer.write(row[0] + "," + row[1] + "," + row[2] + "\n");
            }

            System.out.println("Data successfully saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
