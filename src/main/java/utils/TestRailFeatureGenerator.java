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

    /**
     * Generates feature files from TestRail cases
     *
     * @param projectId TestRail project ID
     * @param suiteId   TestRail suite ID
     * @param outputDir Folder to write .feature files
     */
    public void generateFeatures(int projectId, int suiteId, String outputDir) throws Exception {
        List<JSONObject> cases = client.getCases(projectId, suiteId);

        if (cases.isEmpty()) {
            System.out.println("⚠️ No TestRail cases found in project/suite. No features generated.");
            return;
        }

        StringBuilder featureFile = new StringBuilder();
        featureFile.append("Feature: Auto-generated from TestRail\n");
        featureFile.append("  This feature is automatically generated from TestRail test cases.\n\n");

        for (JSONObject testCase : cases) {
            int caseId = testCase.getInt("id");
            featureFile.append("@C").append(caseId).append("\n");

            // If TestRail contains a BDD scenario JSON, use it
            String bddScenarioJson = testCase.optString("custom_testrail_bdd_scenario", "");
            if (!bddScenarioJson.isEmpty()) {
                try {
                    JSONArray bddArray = new JSONArray(bddScenarioJson);
                    for (int i = 0; i < bddArray.length(); i++) {
                        JSONObject obj = bddArray.getJSONObject(i);
                        String content = obj.optString("content", "");
                        featureFile.append(content).append("\n");
                    }
                } catch (Exception e) {
                    System.err.println("⚠️ Failed to parse BDD scenario for case " + caseId + ": " + e.getMessage());
                    featureFile.append("  Scenario: ").append(testCase.getString("title")).append("\n");
                    featureFile.append("    Given User executes TestRail case ").append(caseId).append("\n");
                }
            } else {
                // Fallback: placeholder scenario
                featureFile.append("  Scenario: ").append(testCase.getString("title")).append("\n");
                featureFile.append("    Given User executes TestRail case ").append(caseId).append("\n");
            }

            featureFile.append("\n");
        }

        // Write to file
        try (FileWriter writer = new FileWriter(outputDir + "/testrail.feature")) {
            writer.write(featureFile.toString());
        }

        System.out.println("✅ Generated feature file with " + cases.size() + " scenarios: " + outputDir + "/testrail.feature");
    }
}
