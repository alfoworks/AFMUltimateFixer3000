package ru.allformine.afmuf.net;

import ru.allformine.afmuf.AFMUltimateFixer;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class Requests {
    public static void sendPostJSON(String JSON, String urlString) {
        try {
            URL url = new URL(urlString);
            URLConnection con = url.openConnection();
            HttpsURLConnection connection = (HttpsURLConnection) con;
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            byte[] out = JSON.getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            connection.setFixedLengthStreamingMode(length);
            connection.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.addRequestProperty("User-Agent", "Mozilla 5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.0.11) ");
            connection.connect();

            try (OutputStream os = connection.getOutputStream()) {
                os.write(out);
            }


            if (connection.getErrorStream() != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append("\n").append(line);
                }

                System.out.println("anus");
                AFMUltimateFixer.logger.error("Can't send JSON to url " + urlString + ".");
                AFMUltimateFixer.logger.error("JSON: " + JSON);
                AFMUltimateFixer.logger.error("Response: " + result.toString());
            }
        } catch (Exception e) {
            AFMUltimateFixer.logger.error("Can't send JSON to url " + urlString + ".");
            AFMUltimateFixer.logger.error("JSON: " + JSON);

            e.printStackTrace();
        }
    }
}
