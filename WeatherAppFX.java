// WeatherAppFX.java
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WeatherAppFX extends Application {

    private static final String WEATHER_API_KEY = "651070ec7d4c8acd1f65fb9760c0d72a";

    @Override
    public void start(Stage primaryStage) {
        Label locationLabel = new Label("Detecting location...");
        Label weatherLabel = new Label("Loading weather...");
        Label tempLabel = new Label("");

        VBox root = new VBox(10, locationLabel, weatherLabel, tempLabel);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 300, 150);
        primaryStage.setTitle("Weather Forecast");
        primaryStage.setScene(scene);
        primaryStage.show();

        new Thread(() -> {
            try {
                String locationResponse = sendGET("http://ip-api.com/json/");
                JsonObject locationJson = JsonParser.parseString(locationResponse).getAsJsonObject();
                String city = locationJson.get("city").getAsString();

                String weatherUrl = String.format(
                        "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=%s",
                        city, WEATHER_API_KEY);
                String weatherResponse = sendGET(weatherUrl);
                JsonObject weatherJson = JsonParser.parseString(weatherResponse).getAsJsonObject();

                String weather = weatherJson.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
                double temp = weatherJson.getAsJsonObject("main").get("temp").getAsDouble();

                javafx.application.Platform.runLater(() -> {
                    locationLabel.setText("Location: " + city);
                    weatherLabel.setText("Weather: " + weather);
                    tempLabel.setText("Temperature: " + temp + " °C");
                });

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    locationLabel.setText("Error loading data.");
                    weatherLabel.setText("Check logs for details.");
                });
                e.printStackTrace();
            }
        }).start();
    }

    public static String sendGET(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        conn.disconnect();
        return content.toString();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
