package hooks;

import config.ConfigReader;
import drivers.DriverFactory;
import utils.TestRailClient;
import io.cucumber.java.*;

public class Hooks {
    private static TestRailClient testRail;

    @Before
    public void setUp() {
        DriverFactory.initDriver();
        testRail = new TestRailClient(
                ConfigReader.get("testrail.url"),
                ConfigReader.get("testrail.username"),
                ConfigReader.get("testrail.apikey")
        );
    }

    @After
    public void tearDown(Scenario scenario) throws Exception {
        String tag = scenario.getSourceTagNames().stream()
                .filter(t -> t.startsWith("@C"))
                .findFirst()
                .orElse(null);

        if (tag != null) {
            int caseId = Integer.parseInt(tag.replace("@C", ""));
            int runId = Integer.parseInt(ConfigReader.get("testrail.runId"));

            if (scenario.isFailed()) {
                testRail.updateResult(runId, caseId, 5, "❌ Failed in automation");
            } else {
                testRail.updateResult(runId, caseId, 1, "✅ Passed in automation");
            }
        }
        DriverFactory.quitDriver();
    }
}
