package utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.util.List;

public class TestRailFeatureGenerator {
    private final TestRailClient client;

    public TestRailFeatureGenerator(TestRailClient client) {
        this.client = client;
    }

    public void generateFeatures(int projectId, int suiteId, String outputDir) throws Exception {
        List<JSONObject> cases = client.getCases(projectId, suiteId);

        if (cases.isEmpty()) {
            System.out.println("⚠️ No TestRail cases found.");
            return;
        }

        StringBuilder featureFile = new StringBuilder();
        featureFile.append("Feature: Auto-generated from TestRail\n");
        featureFile.append("  This feature is automatically generated from TestRail test cases.\n\n");

        for (JSONObject testCase : cases) {
            int caseId = testCase.getInt("id");
            featureFile.append("@C").append(caseId).append("\n");

            String bddJson = testCase.optString("custom_testrail_bdd_scenario", "");
            if (!bddJson.isEmpty()) {
                try {
                    JSONArray arr = new JSONArray(bddJson);
                    for (int i = 0; i < arr.length(); i++) {
                        featureFile.append(arr.getJSONObject(i).optString("content", "")).append("\n");
                    }
                } catch (Exception e) {
                    featureFile.append("  Scenario: ").append(testCase.getString("title")).append("\n");
                    featureFile.append("    Given User executes TestRail case ").append(caseId).append("\n");
                }
            } else {
                featureFile.append("  Scenario: ").append(testCase.getString("title")).append("\n");
                featureFile.append("    Given User executes TestRail case ").append(caseId).append("\n");
            }
            featureFile.append("\n");
        }

        try (FileWriter writer = new FileWriter(outputDir + "/testrail.feature")) {
            writer.write(featureFile.toString());
        }

        System.out.println("✅ Generated feature file with " + cases.size() + " scenarios: " + outputDir + "/testrail.feature");
    }
}
