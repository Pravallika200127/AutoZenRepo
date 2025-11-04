package utils;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import config.ConfigReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * 🔹 TestRail API Client (Extended)
 * - Keeps all previous methods intact
 * - Adds ability to create a result for a specific case and return its result-id
 * - Adds ability to upload Extent report (HTML, ZIP, PNG) directly to a specific result
 */
public class Client {
    private final String apiBaseUrl;
    private final String username;
    private final String apiKey;
    private final int projectId;
    private final int suiteId;
    private final int caseId;
    private final OkHttpClient httpClient;
    private int runId = 0;
    private int testId = 0;
    private int lastResultId = 0;

    public Client() {
        String baseUrl = ConfigReader.get("testrail.url");
        this.apiBaseUrl = baseUrl.endsWith("/") ? baseUrl + "index.php?/api/v2" : baseUrl + "/index.php?/api/v2";
        this.username = ConfigReader.get("testrail.username");
        this.apiKey = ConfigReader.get("testrail.apikey");
        this.projectId = Integer.parseInt(ConfigReader.get("testrail.projectId", "0"));
        this.suiteId = Integer.parseInt(ConfigReader.get("testrail.suiteId", "0"));

        String caseIdStr = ConfigReader.get("testrail.caseId", "0");
        if (caseIdStr != null && caseIdStr.startsWith("C")) {
            caseIdStr = caseIdStr.substring(1);
        }
        this.caseId = Integer.parseInt(caseIdStr);

        this.httpClient = new OkHttpClient();

    }

    // ==========================
    // 🔹 Generic GET helper
    // ==========================
    private String get(String path) throws IOException {
        String url = path.startsWith("http") ? path : apiBaseUrl + "/" + path;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", getBasicAuthHeader())
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                String errorBody = response.body() != null ? response.body().string() : "No error details";
                throw new IOException("GET request failed: " + response.code() + " - " + errorBody);
            }
        }
    }

    // ==========================
    // 🔹 Fetch a single case
    // ==========================
    public JSONObject fetchTestCase(int caseId) throws IOException {
        System.out.println("🔍 Fetching test case C" + caseId + " from TestRail...");
        String json = get("get_case/" + caseId);
        JSONObject testCase = new JSONObject(json);
        System.out.println("✅ Test case fetched successfully");
        return testCase;
    }

    // ==========================
    // 🔹 Fetch all cases
    // ==========================
    public List<JSONObject> fetchCases(int projectId, Integer suiteId) throws IOException {
        System.out.println("🔍 Fetching test cases from TestRail...");

        String path = suiteId == null
                ? "get_cases/" + projectId
                : "get_cases/" + projectId + "?suite_id=" + suiteId;

        String json = get(path);
        List<JSONObject> cases = new ArrayList<>();

        try {
            JSONObject responseObj = new JSONObject(json);
            if (responseObj.has("cases")) {
                JSONArray casesArray = responseObj.getJSONArray("cases");
                for (int i = 0; i < casesArray.length(); i++) {
                    cases.add(casesArray.getJSONObject(i));
                }
            }
        } catch (Exception e) {
            try {
                JSONArray casesArray = new JSONArray(json);
                for (int i = 0; i < casesArray.length(); i++) {
                    cases.add(casesArray.getJSONObject(i));
                }
            } catch (Exception ex) {
                throw new IOException("Failed to parse test cases response: " + ex.getMessage());
            }
        }

        System.out.println("✅ Fetched " + cases.size() + " test cases");
        return cases;
    }

    // ==========================
    // 🔹 Create test run
    // ==========================
    public int createTestRun(String runName) throws IOException {
        System.out.println("\n🏃 Creating test run in TestRail...");

        String url = apiBaseUrl + "/add_run/" + projectId;

        JSONObject payload = new JSONObject();
        payload.put("suite_id", suiteId);
        payload.put("name", runName);
        payload.put("description", "Automated test run - " + java.time.LocalDateTime.now());
        payload.put("include_all", false);

        JSONArray caseIds = new JSONArray();
        caseIds.put(caseId);
        payload.put("case_ids", caseIds);

        RequestBody body = RequestBody.create(payload.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", getBasicAuthHeader())
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        System.out.println("🔗 POST: " + url);

        try (Response response = httpClient.newCall(request).execute()) {
            System.out.println("📊 Response Status: " + response.code());

            if (response.isSuccessful() && response.body() != null) {
                JSONObject runResponse = new JSONObject(response.body().string());
                this.runId = runResponse.getInt("id");

                // Try to fetch the test id(s)
                String testsUrl = apiBaseUrl + "/get_tests/" + runId;
                Request testsRequest = new Request.Builder()
                        .url(testsUrl)
                        .addHeader("Authorization", getBasicAuthHeader())
                        .get()
                        .build();

                try (Response testsResponse = httpClient.newCall(testsRequest).execute()) {
                    if (testsResponse.isSuccessful() && testsResponse.body() != null) {
                        String testsBody = testsResponse.body().string();
                        JSONArray testsArray;
                        try {
                            JSONObject jsonResponse = new JSONObject(testsBody);
                            testsArray = jsonResponse.has("tests")
                                    ? jsonResponse.getJSONArray("tests")
                                    : new JSONArray(testsBody);
                        } catch (Exception e) {
                            testsArray = new JSONArray(testsBody);
                        }

                        if (testsArray.length() > 0) {
                            this.testId = testsArray.getJSONObject(0).getInt("id");
                        }
                    }
                }

                System.out.println("✅ Test run created: R" + runId);
                System.out.println("   Test ID: " + testId);
                return runId;
            } else {
                String errorBody = response.body() != null ? response.body().string() : "No error details";
                throw new IOException("❌ Failed to create test run: " + response.code() + " - " + errorBody);
            }
        }
    }

    // ==========================
    // 🔹 Update test result (existing)
    // ==========================
    public boolean updateTestResult(boolean passed, String comment) throws IOException {
        System.out.println("\n📤 Updating test result in TestRail...");

        if (testId == 0) {
            System.err.println("❌ No test ID available. Cannot update result.");
            return false;
        }

        String url = apiBaseUrl + "/add_result/" + testId;
        int statusId = passed ? 1 : 5;

        JSONObject payload = new JSONObject();
        payload.put("status_id", statusId);
        payload.put("comment", comment);

        RequestBody body = RequestBody.create(payload.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", getBasicAuthHeader())
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        System.out.println("🔗 POST: " + url);
        System.out.println("📦 Status: " + (passed ? "PASSED" : "FAILED"));

        try (Response response = httpClient.newCall(request).execute()) {
            System.out.println("📊 Response Status: " + response.code());
            if (response.isSuccessful() && response.body() != null) {
                String resBody = response.body().string();
                try {
                    JSONObject json = new JSONObject(resBody);
                    if (json.has("id")) lastResultId = json.getInt("id");
                } catch (Exception ignored) {}
                System.out.println("✅ Test result updated successfully in TestRail");
                return true;
            } else {
                System.err.println("❌ Failed to update result: " + response.code());
                return false;
            }
        }
    }

    // ==========================
    // 🔹 Overloaded: Update test result for a specific case and return result id
    // ==========================
    public Integer updateTestResult(int caseIdForResult, boolean passed, String comment) throws IOException {
        System.out.println("\n📤 Adding result for case C" + caseIdForResult + " in run R" + runId + "...");

        if (runId == 0) {
            System.err.println("⚠️ No active runId found. Creating a new run automatically.");
            createTestRun("Automated Run - " + java.time.LocalDateTime.now());
        }

        String url = apiBaseUrl + "/add_result_for_case/" + runId + "/" + caseIdForResult;
        int statusId = passed ? 1 : 5;

        JSONObject payload = new JSONObject();
        payload.put("status_id", statusId);
        payload.put("comment", comment);

        RequestBody body = RequestBody.create(payload.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", getBasicAuthHeader())
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        System.out.println("🔗 POST: " + url);
        try (Response response = httpClient.newCall(request).execute()) {
            String respBody = response.body() != null ? response.body().string() : "";
            System.out.println("📊 Response Status: " + response.code());
            if (response.isSuccessful()) {
                JSONObject json = new JSONObject(respBody);
                if (json.has("id")) {
                    int resultId = json.getInt("id");
                    this.lastResultId = resultId;
                    System.out.println("✅ Result added for C" + caseIdForResult + " (Result ID: " + resultId + ")");
                    return resultId;
                } else {
                    System.err.println("⚠️ Response didn't include result id. Body: " + respBody);
                    return null;
                }
            } else {
                System.err.println("❌ Failed to add result_for_case: " + response.code() + " - " + respBody);
                return null;
            }
        }
    }

    // ==========================
    // 🔹 Create defect
    // ==========================
    public boolean createDefect(String title, String description) throws IOException {
        System.out.println("\n🐛 Creating defect in TestRail...");

        if (testId == 0) {
            System.err.println("⚠️  Cannot create defect: No test ID available");
            return false;
        }

        String url = apiBaseUrl + "/add_result/" + testId;
        JSONObject payload = new JSONObject();
        payload.put("status_id", 5);
        payload.put("comment", "🐛 DEFECT: " + title + "\n\n" + description);
        payload.put("defects", title);

        RequestBody body = RequestBody.create(payload.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", getBasicAuthHeader())
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("✅ Defect information added to test result");
                return true;
            } else {
                System.out.println("⚠️ Could not add defect: " + response.code());
                return false;
            }
        }
    }

    // ==========================
    // 🔹 Upload Extent report / Screenshot / ZIP to a result
    // ==========================
    public boolean uploadExtentReportToResult(int resultId, File reportFile) throws IOException {
        System.out.println("\n📤 Uploading Extent report to TestRail result ID: " + resultId);

        if (resultId == 0) {
            System.err.println("❌ Invalid resultId provided.");
            return false;
        }

        if (reportFile == null || !reportFile.exists()) {
            System.err.println("❌ Report file not found: " + (reportFile == null ? "null" : reportFile.getAbsolutePath()));
            return false;
        }

        String mimeType = "application/octet-stream";
        String name = reportFile.getName().toLowerCase();
        if (name.endsWith(".zip")) mimeType = "application/zip";
        else if (name.endsWith(".html") || name.endsWith(".htm")) mimeType = "text/html";
        else if (name.endsWith(".png")) mimeType = "image/png";
        else if (name.endsWith(".txt")) mimeType = "text/plain";
        else if (name.endsWith(".json")) mimeType = "application/json";

        String url = apiBaseUrl + "/add_attachment_to_result/" + resultId;

        RequestBody fileBody = RequestBody.create(reportFile, MediaType.parse(mimeType));
        RequestBody multipart = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("attachment", reportFile.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", getBasicAuthHeader())
                .post(multipart)
                .build();

        System.out.println("🔗 POST: " + url);
        System.out.println("📦 Uploading file: " + reportFile.getName() + " (" + mimeType + ")");

        try (Response response = httpClient.newCall(request).execute()) {
            String resp = response.body() != null ? response.body().string() : "";
            if (response.isSuccessful()) {
                this.lastResultId = resultId;
                System.out.println("✅ Report uploaded successfully to Result ID: " + resultId);
                return true;
            } else {
                System.err.println("⚠️ Upload failed: " + response.code() + " - " + resp);
                return false;
            }
        }
    }

    // ==========================
    // 🔹 Close test run
    // ==========================
    public boolean closeTestRun() throws IOException {
        if (runId == 0) {
            System.out.println("⚠️  No test run to close");
            return false;
        }

        System.out.println("\n🏁 Closing test run R" + runId + "...");
        String url = apiBaseUrl + "/close_run/" + runId;
        RequestBody body = RequestBody.create("{}", MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", getBasicAuthHeader())
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return true;
            } else {
                System.err.println("⚠️ Could not close test run: " + response.code());
                return false;
            }
        }
    }

    // ==========================
    // 🔹 Auth helper
    // ==========================
    private String getBasicAuthHeader() {
        String credentials = username + ":" + apiKey;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    // ==========================
    // 🔹 Getters
    // ==========================
    public int getRunId() { return runId; }
    public int getTestId() { return testId; }
    public int getCaseId() { return caseId; }
    public int getLastResultId() { return lastResultId; }
}
