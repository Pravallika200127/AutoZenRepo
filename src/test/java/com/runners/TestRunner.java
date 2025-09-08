package com.runners;

import config.ConfigReader;
import utils.TestRailClient;
import utils.TestRailFeatureGenerator;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.BeforeSuite;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"hooks", "steps"},
        plugin = {"pretty"}
)
public class TestRunner extends AbstractTestNGCucumberTests {

    @BeforeSuite
    public void generateFeatures() throws Exception {
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
    }
}
