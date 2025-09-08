package com.stepsdefs;

import io.cucumber.java.en.Given;

public class StepDefinitions {

    @Given("User executes TestRail case {int}")
    public void executeCase(int caseId) {
        System.out.println("🚀 Executing TestRail case: " + caseId);
        // TODO: Map caseId to actual Selenium logic
    }
}
