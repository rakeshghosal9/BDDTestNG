@STOCK_ANALYSIS
Feature: Stock Analysis 2

  Scenario: Stock Analysis of given Indian stocks
    Given we read all the company name from excel "StockName" and sheet name "Set2"
    When landed on the google homepage
    Then capture all the stock statistics
