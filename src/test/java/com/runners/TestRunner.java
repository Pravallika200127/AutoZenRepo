package com.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import config.ConfigReader;
import utils.TestRailClient;
import utils.TestRailFeatureGenerator;

@CucumberOptions(
        features = "src/test/resources/features", // folder where generated features will go
        glue = {"com.stepdefs", "com.hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-report.html",
                "json:target/cucumber-report.json",
                "junit:target/cucumber-report.xml"
        },
        monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {

    // 🔹 Static block executes before Cucumber scans features
    static {
        try {
            System.out.println("🔄 Fetching TestRail cases and generating feature files...");

            TestRailClient client = new TestRailClient(
                    ConfigReader.get("testrail.url"),
                    ConfigReader.get("testrail.username"),
                    ConfigReader.get("testrail.apikey")
            );

            TestRailFeatureGenerator generator = new TestRailFeatureGenerator(client);
            generator.generateFeatures(
                    Integer.parseInt(ConfigReader.get("testrail.projectId")),
                    Integer.parseInt(ConfigReader.get("testrail.suiteId")),
                    "src/test/resources/features"
            );

            System.out.println("✅ Feature files generated successfully from TestRail!");
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate feature files from TestRail", e);
        }
    }
}
