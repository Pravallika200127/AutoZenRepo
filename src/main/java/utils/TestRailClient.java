package utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestRailClient {
    private final String baseUrl;
    private final String username;
    private final String apiKey;

    public TestRailClient(String baseUrl, String username, String apiKey) {
        this.baseUrl = baseUrl;
        this.username = username;
        this.apiKey = apiKey;
    }

    public List<JSONObject> getCases(int projectId, int suiteId) throws Exception {
        String endpoint = String.format("%s/index.php?/api/v2/get_cases/%d&suite_id=%d",
                baseUrl, projectId, suiteId);

        String response = sendGetRequest(endpoint).trim();
        JSONObject root = new JSONObject(response);

        if (!root.has("cases")) {
            throw new RuntimeException("❌ TestRail API response does not contain 'cases'. Response:\n" + response);
        }

        JSONArray casesArray = root.getJSONArray("cases");
        return casesArray.toList().stream()
                .map(o -> new JSONObject((Map<?, ?>) o))
                .collect(Collectors.toList());
    }

    public void updateResult(int runId, int caseId, int statusId, String comment) throws Exception {
        String endpoint = String.format("%s/index.php?/api/v2/add_result_for_case/%d/%d",
                baseUrl, runId, caseId);

        JSONObject json = new JSONObject();
        json.put("status_id", statusId);
        json.put("comment", comment);

        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        String auth = username + ":" + apiKey;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
        conn.setDoOutput(true);

        conn.getOutputStream().write(json.toString().getBytes());
        conn.getOutputStream().flush();
        conn.getOutputStream().close();

        if (conn.getResponseCode() < 200 || conn.getResponseCode() >= 300) {
            throw new RuntimeException("Failed to update TestRail result. HTTP code: " + conn.getResponseCode());
        }
    }

    private String sendGetRequest(String endpoint) throws Exception {
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        String auth = username + ":" + apiKey;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        conn.setRequestProperty("Authorization", "Basic " + encodedAuth);

        BufferedReader in = new BufferedReader(new InputStreamReader(
                conn.getResponseCode() >= 200 && conn.getResponseCode() < 300 ?
                        conn.getInputStream() : conn.getErrorStream()));

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) response.append(line);
        in.close();

        return response.toString();
    }
}
