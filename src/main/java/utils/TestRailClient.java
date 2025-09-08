package utils;

import com.google.gson.*;
import okhttp3.*;
import okio.ByteString;

import java.io.IOException;
import java.util.*;

public class TestRailClient {

    private final String base;
    private final String auth;
    private final OkHttpClient http = new OkHttpClient();
    private final Gson gson = new Gson();

    public TestRailClient(String baseUrl, String user, String apiKey) {
        this.base = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.auth = "Basic " + ByteString.encodeUtf8(user + ":" + apiKey).base64();
    }

    private String get(String path) throws IOException {
        Request req = new Request.Builder()
                .url(base + path)
                .addHeader("Authorization", auth)
                .build();
        try (Response r = http.newCall(req).execute()) {
            if (!r.isSuccessful()) throw new IOException("GET " + path + " -> " + r.code());
            return r.body().string();
        }
    }

    private JsonObject post(String path, JsonObject body) throws IOException {
        RequestBody rb = RequestBody.create(gson.toJson(body), MediaType.get("application/json"));
        Request req = new Request.Builder()
                .url(base + path)
                .addHeader("Authorization", auth)
                .post(rb)
                .build();
        try (Response r = http.newCall(req).execute()) {
            if (!r.isSuccessful())
                throw new IOException("POST " + path + " -> " + r.code() + " " + r.message());
            return JsonParser.parseString(r.body().string()).getAsJsonObject();
        }
    }

    // ---- Cases ----
    public List<JsonObject> getCases(int projectId, Integer suiteId) throws IOException {
        String path = suiteId == null
                ? "index.php?/api/v2/get_cases/" + projectId
                : "index.php?/api/v2/get_cases/" + projectId + "&suite_id=" + suiteId;

        String json = get(path);
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

        // The "cases" field contains the array of test cases
        JsonArray arr = obj.getAsJsonArray("cases");
        List<JsonObject> cases = new ArrayList<>();
        for (JsonElement e : arr) {
            cases.add(e.getAsJsonObject());
        }
        return cases;
    }

    // ---- Update result for a case ----
    public void updateResult(int runId, int caseId, int statusId, String comment) throws IOException {
        JsonObject body = new JsonObject();
        body.addProperty("status_id", statusId); // 1=Passed,5=Failed
        if (comment != null) body.addProperty("comment", comment);

        post("index.php?/api/v2/case/" + runId + "/" + caseId, body);
    }

}
