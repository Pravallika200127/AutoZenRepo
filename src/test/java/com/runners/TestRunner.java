package com.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;
import config.ConfigReader;
import utils.Client;
import utils.FeatureGenerator;
import java.io.File;

@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"com.stepsdefs", "hooks"},
    plugin = {
        "pretty",
        "html:reports/cucumber-html-report.html",
        "json:reports/cucumber.json",
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
        "hooks.Hooks"  // ✅ Register Hooks as event listener to capture Gherkin steps
    },
    monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {

    static {
        // Generate feature files BEFORE Cucumber initialization
        generateFeaturesBeforeCucumber();
    }

    private static void generateFeaturesBeforeCucumber() {
        try {
            System.out.println("✅ Configuration loaded successfully from: src/test/resources/config.properties\n");
            String testCaseIdStr = ConfigReader.get("testrail.caseId");
            if (testCaseIdStr == null || testCaseIdStr.isEmpty()) {
                throw new RuntimeException("❌ testrail.caseId not configured in config.properties");
            }
            // Handle both "C40" and "40" formats
            if (testCaseIdStr.startsWith("C")) {
                testCaseIdStr = testCaseIdStr.substring(1);
            }
            int testCaseId = Integer.parseInt(testCaseIdStr);
            // Initialize TestRail client and feature generator
            Client testRailClient = new Client();
            FeatureGenerator generator = new FeatureGenerator(testRailClient);
            System.out.println("📝 Generating feature file for TestRail case C" + testCaseId + "...");
            String featureFilePath = generator.generateFeatureFile(testCaseId);
            System.out.println("✅ Feature file generated successfully: " + featureFilePath + "\n");
        } catch (Exception e) {
            System.err.println("❌ Error generating features from TestRail: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate feature files", e);
        }
    }

    @BeforeSuite
    public void beforeSuite() {
        // Create reports directory if it doesn't exist
        File reportsDir = new File("reports");
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
            System.out.println("✅ Created reports directory");
        }
    }

    @AfterSuite
    public void afterSuite() {
        System.out.println("\n" + "=".repeat(90));
        System.out.println("🏁 TESTRAIL AUTOMATION SUITE COMPLETED");
        System.out.println("=".repeat(90));
        System.out.println("📊 Check Extent Report: test-output/ExtentReport.html");
        System.out.println("📈 Cucumber HTML: reports/cucumber-html-report.html");
        System.out.println("=".repeat(90) + "\n");
    }
}