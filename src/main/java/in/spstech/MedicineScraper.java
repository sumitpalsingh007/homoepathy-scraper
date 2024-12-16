package in.spstech;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MedicineScraper {

    public static void main(String[] args) {
        String baseUrl = "https://www.reckeweg-india.com/homoeopathy-products/dilutions-6.html";
        Set<String> medicineNames = new HashSet<>();

        try {
            // Start scraping from the initial page
            scrapePage(baseUrl, medicineNames);

            // Save scraped names to CSV
            saveToCSV(medicineNames, "medicines.csv");
            System.out.println("Scraping finished. Data saved to 'medicines.csv'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to scrape the current page and subsequent paginated pages
     */
    private static void scrapePage(String url, Set<String> medicineNames) throws IOException {
        System.out.println("Scraping URL: " + url);

        Document document = Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
            .get();

        // Extract medicines from the current page
        Elements medicines = document.select("table.product-table.table-bordered tbody "); // Adjust based on DOM structure
        for (Element medicine : medicines) {
            String name = medicine.text().trim();
            if (!name.isEmpty()) {
                medicineNames.add(name);
            }
        }

        // Find the link to the next page, if available
        Elements nextPageLink = document.select("a[rel=next]");
        if (!nextPageLink.isEmpty()) {
            String nextPageUrl = nextPageLink.attr("href");
            System.out.println("Next page found: " + nextPageUrl);
            scrapePage(nextPageUrl, medicineNames);
        } else {
            System.out.println("No further pages to scrape.");
        }
    }

    /**
     * Save the data into a CSV file
     */
    private static void saveToCSV(Set<String> data, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("Medicine Name\n");
            for (String medicine : data) {
                writer.write(medicine + "\n");
            }
            System.out.println("Data saved to CSV.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
