package hooks;

import config.ConfigReader;
import drivers.DriverFactory;
import utils.TestRailClient;
import io.cucumber.java.*;

public class Hooks {
    private TestRailClient testRail;

    @Before
    public void setUp() {
        System.out.println("Initializing driver and TestRail client...");
        DriverFactory.initDriver();

        String url = ConfigReader.get("testrail.url");
        String username = ConfigReader.get("testrail.username");
        String apiKey = ConfigReader.get("testrail.apikey");

        if (url != null && username != null && apiKey != null) {
            testRail = new TestRailClient(url, username, apiKey);
            System.out.println("TestRail client initialized successfully.");
        } else {
            System.out.println("Missing TestRail configuration. Please check your config files.");
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            String tag = scenario.getSourceTagNames().stream()
                .filter(t -> t.startsWith("@C"))
                .findFirst()
                .orElse(null);

            if (tag != null && testRail != null) {
                int caseId = Integer.parseInt(tag.replace("@C", ""));
                int runId = Integer.parseInt(ConfigReader.get("testrail.runId"));

                if (scenario.isFailed()) {
                    testRail.updateResult(runId, caseId, 5, "❌ Failed in automation");
                } else {
                    testRail.updateResult(runId, caseId, 1, "✅ Passed in automation");
                }
                System.out.println("Updated TestRail with scenario result.");
            } else if (tag != null) {
                System.out.println("TestRail client not initialized. Skipping result update.");
            }
        } catch (Exception e) {
            System.out.println("Exception in tearDown: " + e.getMessage());
        } finally {
            DriverFactory.quitDriver();
            System.out.println("Driver quit successfully.");
        }
    }
}