package utils;

import com.google.gson.*;
import java.io.FileWriter;
import java.util.List;

public class TestRailFeatureGenerator {

    private final TestRailClient client;

    public TestRailFeatureGenerator(TestRailClient client) {
        this.client = client;
    }

    /**
     * Generate feature files from TestRail cases
     */
    public void generateFeatures(int projectId, Integer suiteId, String outputDir) throws Exception {
        List<JsonObject> cases = client.getCases(projectId, suiteId);

        if (cases.isEmpty()) {
            System.out.println("⚠️ No TestRail cases found.");
            return;
        }

        StringBuilder featureFile = new StringBuilder();
        featureFile.append("Feature: Auto-generated from TestRail\n");
        featureFile.append("  This feature is automatically generated from TestRail cases.\n");

        for (JsonObject testCase : cases) {
            int caseId = testCase.get("id").getAsInt();
            String title = testCase.get("title").getAsString();

            // Optional: BDD scenario from custom field
            String bddScenario = "";
            if (testCase.has("custom_testrail_bdd_scenario") && !testCase.get("custom_testrail_bdd_scenario").isJsonNull()) {
                JsonArray arr = JsonParser.parseString(testCase.get("custom_testrail_bdd_scenario").getAsString()).getAsJsonArray();
                if (arr.size() > 0) {
                    bddScenario = arr.get(0).getAsJsonObject().get("content").getAsString();
                }
            }

            featureFile.append("\n  @C").append(caseId).append("\n");
            featureFile.append("  Scenario: ").append(title).append("\n");

            if (!bddScenario.isEmpty()) {
                featureFile.append("    ").append(bddScenario.replace("\n", "\n    ")).append("\n");
            } else {
                featureFile.append("    Given User executes TestRail case ").append(caseId).append("\n");
            }
        }

        try (FileWriter writer = new FileWriter(outputDir + "/testrail.feature")) {
            writer.write(featureFile.toString());
        }

        System.out.println("✅ Generated feature file: " + outputDir + "/testrail.feature");
    }
}
