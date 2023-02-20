@USER_REGISTRATION
Feature: User Registration for Automation Practice

Background: Test Background
  When this is a background step

  Scenario: User Registration Background
    When click on the sign in link of the homepage
    And enter the registration email address as "auto-generated" and click on create account button
    And enter all the registration details as given below
      | title | firstName | lastName | password    | dob      | addressFirstName | addressLastName | company | addressLine1 | addressLine2 | city | state | zipCode | country | addInfo  | homePhone  | mobilePhone | addressAlias |
      | Mr.   | Rahul     | Pandey   | Password123 | 1-1-1987 | Rahul            | Pandey          | CTS     | Addline1     | Addline2     | City | 23    | 55344   | 21      | Add Info | 2311111232 | 1211111222  | Reg Address  |
    Then the registration should be successful



