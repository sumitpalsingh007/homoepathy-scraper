package in.spstech;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeopathicScraper {

    public static void main(String[] args) {
        String url = "https://medicosage.com"; // Replace with the actual URL
        scrapeAndSaveData(url);
    }

    public static void scrapeAndSaveData(String url) {
        try {
            // Fetch the HTML content of the webpage
            Document document = Jsoup.connect(url).get();

            // Select elements - this will depend on the structure of the website
            Elements medicineRows = document.select("div.medicine-card-class"); // Adjust class name to match site

            List<String[]> data = new ArrayList<>();

            for (Element row : medicineRows) {
                try {
                    String medicineName = row.select("h2").text(); // Select the medicine name
                    String symptoms = row.select("p").text();    // Select symptoms
                    data.add(new String[]{medicineName, symptoms});
                } catch (Exception e) {
                    System.err.println("Skipping a row due to parsing issue: " + e.getMessage());
                }
            }

            // Save extracted data to CSV
            saveToCSV(data, "homeopathic_medicines.csv");

            System.out.println("Data successfully saved to 'homeopathic_medicines.csv'");

        } catch (IOException e) {
            System.err.println("Error fetching or parsing webpage: " + e.getMessage());
        }
    }

    private static void saveToCSV(List<String[]> data, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            // Write headers
            writer.write("Medicine,Symptoms/Indications\n");

            // Write data rows
            for (String[] row : data) {
                writer.write(row[0] + "," + row[1] + "\n");
            }

            System.out.println("CSV file created.");
        } catch (IOException e) {
            System.err.println("Error writing CSV file: " + e.getMessage());
        }
    }
}
