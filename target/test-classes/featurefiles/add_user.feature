Feature: ADD USERS FOR NU HOSPITALS

  Scenario Outline:
    Given Entered a valid <Username> <Password>
    When Click on the sign in button
    And Navigate to Users Module
    And Navigate to ADD USER Module
    Then ADD USERS <file_path> <sheet_name>

    Examples:
      | Username     | Password         | file_path                                                 | sheet_name           |
      | "superadmin" | "Mobilewise@123" | "/home/saiprakesh/Downloads/Nurse List All Unit (2).xlsx" | "Nursing Department" |


