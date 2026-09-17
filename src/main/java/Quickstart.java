import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// import com.google.gson.*;

public class Quickstart {
    public static void main(String[] args) {
        String apiKey = System.getenv("API_TOKEN");
        String URL = "https://api.waqi.info/feed/here/?token=" + apiKey;
        String response = get(URL);
        JFrame frame = buildUI(apiKey);
        System.out.println(response);
    }

    public static JFrame buildUI(String apiKey) {
        JFrame frame = new JFrame("Weather App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JTextField cityField = new JTextField();
        cityField.setToolTipText("Enter city name (not used in this example)");
        JButton getWeatherButton = new JButton("Get Weather");
        getWeatherButton.setToolTipText("Click to fetch weather data");
        getWeatherButton.setPreferredSize(new Dimension(30, 30));
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(getWeatherButton);
        JTextArea weatherInfoArea = new JTextArea(10, 40);
        weatherInfoArea.setEditable(false);

        getWeatherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //String city = cityField.getText();
                String apiKey = System.getenv("API_TOKEN");
                String URL = "https://api.waqi.info/feed/here/?token=" + apiKey;
                String response = get(URL);
                weatherInfoArea.setText(response);
            }
        });

        panel.add(cityField, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(new JScrollPane(weatherInfoArea), BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);

        return frame;
    }

    public static String formatJSON(String rawJson) {
        if (rawJson == null || rawJson.trim().isEmpty()) {
            return "";
        }
        
        StringBuilder pretty = new StringBuilder();
        int indentLevel = 0;
        boolean inQuotes = false;

        for (int i = 0; i < rawJson.length(); i++) {
            char ch = rawJson.charAt(i);

            // Handle string literals so we don't format inside a text value
            if (ch == '"') {
                // Check for escaped quotes \"
                if (i > 0 && rawJson.charAt(i - 1) != '\\') {
                    inQuotes = !inQuotes;
                }
            }

            if (inQuotes) {
                pretty.append(ch);
                continue;
            }

            // Format according to JSON structural rules
            switch (ch) {
                case '{':
                case '[':
                    pretty.append(ch).append("\n");
                    indentLevel++;
                    pretty.append("\t".repeat(indentLevel));
                    break;
                case '}':
                case ']':
                    pretty.append("\n");
                    indentLevel--;
                    pretty.append("\t".repeat(indentLevel));
                    pretty.append(ch);
                    break;
                case ',':
                    pretty.append(ch).append("\n");
                    pretty.append("\t".repeat(indentLevel));
                    break;
                case ':':
                    pretty.append(ch).append(" ");
                    break;
                    
                // Skip existing whitespaces to re-align cleanly
                case ' ':
                case '\n':
                case '\r':
                case '\t':
                    break;
                default:
                    pretty.append(ch);
                    break;
            }
        }
        return pretty.toString();
    }


    public static String get(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String prettyPrint = formatJSON(response.body());
            return prettyPrint;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
