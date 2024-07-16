package com.java.liyao;

// 行吧，我也不知道是怎么爬下来的，反正是爬下来了（doge）

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Fetcher {

    private static final String baseURL = "https://api2.newsminer.net/svc/news/queryNewsList";

    private static void fetchAndSaveNews(String apiUrl, String outputPath) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    writeStringToFile(response.toString(), outputPath);
                }
            } else {
                System.out.println("Failed to get news data: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeStringToFile(String content, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void fetcher(String[] args, String outputPath) throws UnsupportedEncodingException {
        final String apiUrl = baseURL + "?size=" + URLEncoder.encode(args[0], StandardCharsets.UTF_8.toString())
                + "&startDate=" + URLEncoder.encode(args[1], StandardCharsets.UTF_8.toString())
                + "&endDate=" + URLEncoder.encode(args[2], StandardCharsets.UTF_8.toString())
                + "&words=" + URLEncoder.encode(args[3], StandardCharsets.UTF_8.toString())
                + "&categories=" + URLEncoder.encode(args[4], StandardCharsets.UTF_8.toString())
                + "&page=" + URLEncoder.encode(args[5], StandardCharsets.UTF_8.toString());

        fetchAndSaveNews(apiUrl, outputPath);
    }
}