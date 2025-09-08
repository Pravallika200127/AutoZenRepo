Feature: Auto-generated from TestRail

@C1

Feature: Facebook Signup
  Scenario: Complete signup process
    Given User opens the Facebook signup page
    When User enters first name "John"
    And User enters last name "Doe"
    And User enters mobile number or email "john.doe@example.com"
    And User re-enters mobile number or email "john.doe@example.com"
    And User enters a new password "StrongPass@123"
    And User selects birth date "10" "May" "1995"
    And User selects gender "Male"
    And User clicks the Sign Up button
    Then User should see an account verification page
    And User should receive a confirmation email or SMS



