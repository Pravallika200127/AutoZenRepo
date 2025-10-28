package utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 🔹 FeatureGenerator
 * Fetches TestRail cases and generates .feature files (Gherkin format)
 */
public class FeatureGenerator {
    private final Client testRailClient;
    private final String featuresDir;

    public FeatureGenerator(Client testRailClient) {
        this.testRailClient = testRailClient;
        this.featuresDir = "src/test/resources/features";

        // Create directory if missing
        File dir = new File(featuresDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Generate a feature file from a TestRail case
     */
    public String generateFeatureFile(int caseId) throws IOException {
        System.out.println("📝 Generating feature file for TestRail case C" + caseId + "...");

        // Fetch case from TestRail
        JSONObject testCase = testRailClient.fetchTestCase(caseId);

        // Extract data
        String title = testCase.optString("title", "Untitled Test Case");
        String description = testCase.optString("custom_preconds", "");
        String stepsContent = extractBDDSteps(testCase);

        if (stepsContent != null && !stepsContent.isEmpty()) {
            System.out.println("✅ Successfully extracted BDD/Gherkin steps");
        } else {
            System.out.println("⚠️ No BDD steps found, will generate default scenario");
        }

        // Compose feature content
        String featureContent = generateFeatureContent(caseId, title, description, stepsContent);

        // Save file
        String fileName = "TestCase_" + caseId + ".feature";
        String filePath = featuresDir + "/" + fileName;

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(featureContent);
        }

        System.out.println("✅ Feature file created: " + new File(filePath).getAbsolutePath());
        return filePath;
    }

    /**
     * Extract BDD steps from TestRail JSON fields
     */
    private String extractBDDSteps(JSONObject testCase) {
        StringBuilder steps = new StringBuilder();

        // 1️⃣ custom_testrail_bdd_scenario (preferred)
        if (testCase.has("custom_testrail_bdd_scenario") && !testCase.isNull("custom_testrail_bdd_scenario")) {
            try {
                Object bddObj = testCase.get("custom_testrail_bdd_scenario");
                JSONArray bddArray = (bddObj instanceof String)
                        ? new JSONArray((String) bddObj)
                        : (JSONArray) bddObj;

                System.out.println("🔍 Found 'custom_testrail_bdd_scenario' with " + bddArray.length() + " items");

                for (int i = 0; i < bddArray.length(); i++) {
                    JSONObject bddItem = bddArray.getJSONObject(i);
                    String content = bddItem.optString("content", "");
                    if (!content.isEmpty()) {
                        steps.append(cleanHtml(content)).append("\n");
                    }
                }

                if (steps.length() > 0) return steps.toString().trim();

            } catch (Exception e) {
                System.err.println("⚠️ Error parsing 'custom_testrail_bdd_scenario': " + e.getMessage());
            }
        }

        // 2️⃣ custom_steps_separated
        if (testCase.has("custom_steps_separated") && !testCase.isNull("custom_steps_separated")) {
            try {
                JSONArray stepsArray = testCase.getJSONArray("custom_steps_separated");
                System.out.println("🔍 Found 'custom_steps_separated' (" + stepsArray.length() + " steps)");

                for (int i = 0; i < stepsArray.length(); i++) {
                    JSONObject step = stepsArray.getJSONObject(i);
                    String content = step.optString("content", "");
                    String expected = step.optString("expected", "");

                    if (!content.isEmpty()) {
                        steps.append(formatGherkinStep(cleanHtml(content), i)).append("\n");
                    }
                    if (!expected.isEmpty()) {
                        steps.append(formatGherkinStep(cleanHtml(expected), i, true)).append("\n");
                    }
                }

                if (steps.length() > 0) return steps.toString().trim();

            } catch (Exception e) {
                System.err.println("⚠️ Error parsing 'custom_steps_separated': " + e.getMessage());
            }
        }

        // 3️⃣ custom_steps (plain text)
        if (testCase.has("custom_steps") && !testCase.isNull("custom_steps")) {
            String customSteps = testCase.optString("custom_steps", "");
            if (!customSteps.isEmpty()) {
                System.out.println("🔍 Found 'custom_steps'");
                return parseTextSteps(customSteps);
            }
        }

        // 4️⃣ other potential custom Gherkin fields
        for (String field : new String[]{"custom_gherkin", "custom_bdd_scenario", "custom_bdd", "custom_scenario"}) {
            if (testCase.has(field) && !testCase.isNull(field)) {
                String gherkin = testCase.optString(field, "");
                if (!gherkin.isEmpty()) {
                    System.out.println("🔍 Found '" + field + "'");
                    return cleanHtml(gherkin);
                }
            }
        }

        return steps.toString().trim();
    }

    /**
     * Parse plain text steps into Gherkin format
     */
    private String parseTextSteps(String textSteps) {
        StringBuilder gherkin = new StringBuilder();
        String[] lines = cleanHtml(textSteps).split("\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (!line.isEmpty()) {
                gherkin.append(formatGherkinStep(line, i)).append("\n");
            }
        }
        return gherkin.toString();
    }

    /**
     * Format steps with Gherkin keywords
     */
    private String formatGherkinStep(String step, int index) {
        return formatGherkinStep(step, index, false);
    }

    private String formatGherkinStep(String step, int index, boolean isExpected) {
        if (step.matches("^(Given|When|Then|And|But)\\s.*")) return step;

        if (isExpected) return "Then " + step;
        if (index == 0) return "Given " + step;

        String lower = step.toLowerCase();
        if (lower.contains("click") || lower.contains("enter") || lower.contains("select"))
            return "When " + step;
        if (lower.contains("should") || lower.contains("verify") || lower.contains("validate"))
            return "Then " + step;

        return "And " + step;
    }

    /**
     * Clean HTML and entities
     */
    private String cleanHtml(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.replaceAll("<[^>]*>", "")
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .trim();
    }

    /**
     * Build final .feature file content
     */
    private String generateFeatureContent(int caseId, String title, String description, String stepsContent) {
        StringBuilder sb = new StringBuilder();
        sb.append("@TestRail\n");
        sb.append("Feature: ").append(title).append("\n\n");

        if (description != null && !description.isEmpty())
            sb.append("  ").append(cleanHtml(description)).append("\n\n");

        sb.append("@CaseID_").append(caseId).append("\n");
        sb.append("Scenario: ").append(title).append("\n");

        if (stepsContent != null && !stepsContent.isEmpty()) {
            for (String line : stepsContent.split("\\n")) {
                if (!line.trim().isEmpty()) {
                    sb.append("  ").append(line.trim()).append("\n");
                }
            }
        } else {
            sb.append("  Given I have the test case \"").append(title).append("\"\n");
            sb.append("  When I execute the test\n");
            sb.append("  Then it should complete successfully\n");
        }
        return sb.toString();
    }

    /**
     * Generate feature file with provided Gherkin
     */
    public String generateFeatureFileWithGherkin(int caseId, String title, String gherkinSteps) throws IOException {
        String featureContent = generateFeatureContent(caseId, title, null, gherkinSteps);
        String fileName = "TestCase_" + caseId + ".feature";
        String filePath = featuresDir + "/" + fileName;

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(featureContent);
        }

        System.out.println("✅ Feature file generated: " + filePath);
        return filePath;
    }

    public String getFeaturesDirectory() {
        return featuresDir;
    }
}
