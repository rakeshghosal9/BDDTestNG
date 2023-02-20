package com.report;

import common.action.MariaDBConnection;
import common.action.ReusableCommonMethods;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import net.masterthought.cucumber.Reportable;
import net.masterthought.cucumber.json.support.Status;
import net.masterthought.cucumber.presentation.PresentationMode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GenerateReport {

    public static List<String> jsonFiles = new ArrayList<>();
    public static HashMap<String, String> allScenariosWithJSONPath = new HashMap<>();
    public static Set<String> failedScenarioNames = new HashSet<>();
    //Mention is failed scenarios need to be written in Maria DB or not
    //it should be set as No if you are not using Maria DB
    public static String WRITE_FAILED_CASES_TO_MYSQL_DB = "No";
    //Mention the table name to store failed scenarios in MariaDB in SCHEMA_NAME.TABLE_NAME format
    public static String failedScenariosTableName = "bdd_framework.execution_statistics";
    //Set the below variable as Yes if you want to generate local Cucumber Report
    //You may set it as No if you are running from Jekins as local report may not be needed in that case
    public static String generateLocalCucumberReport = "No";

    public static void main(String args[]) throws SQLException {

        //Check if the scenario need not to be rerun
        jsonFiles = getJSONFileNames(System.getProperty("user.dir") + "\\target\\cucumber-report\\");
        for (String jsonFileName :
                jsonFiles) {
            getAllScenariosWithJSONPath(jsonFileName);
        }
        // Create Final Report Directory
        ReusableCommonMethods.createDirectoryIfNotExists
                (System.getProperty("user.dir") + "\\target\\final-report");
        //Check if the rerunFile name was given in the command line
        if (System.getProperty("rerunFile") != null && !System.getProperty("rerunFile").isEmpty()) {
            try {
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(new FileReader(System.getProperty("user.dir") +
                        "\\src\\test\\resources\\FailedScenarios\\" + System.getProperty("rerunFile")));
                JSONObject failedScenarios = (JSONObject) obj;
                for (String scenarioName : allScenariosWithJSONPath.keySet()) {
                    if (failedScenarios.get(scenarioName) != null &&
                            failedScenarios.get(scenarioName).toString().trim().equalsIgnoreCase("Failed")) {
                        String newPath = allScenariosWithJSONPath.get(scenarioName).
                                replace("cucumber-report", "final-report");
                        copyFile(allScenariosWithJSONPath.get(scenarioName), newPath);
                    }
                }
            }catch (NoSuchFileException NE) {
                //If the JSON File name provided is not present then copy all the JSON files to Final-Report folder
                for (String scenarioName : allScenariosWithJSONPath.keySet()) {
                        String newPath = allScenariosWithJSONPath.get(scenarioName).
                                replace("cucumber-report", "final-report");
                        copyFile(allScenariosWithJSONPath.get(scenarioName), newPath);
                }

            }
            catch (Exception e) {
                System.out.println("Exception occurred : " + e);
            }
            //Check if the rerunKey was given for fetching the failures from Maria DB
        } else if (System.getProperty("rerunKey") != null && !System.getProperty("rerunKey").isEmpty()) {
            try {
                String query = "SELECT SCENARIO_NAME FROM " + failedScenariosTableName + " " +
                        " WHERE RERUN_KEY = '" + System.getProperty("rerunKey") + "';";
                //Get the failed scenarios of the rerun key from Maria DB
                HashMap<String, String> failedScenarios = MariaDBConnection.getFailedScenariosByRerunKey(query);
                for (String scenarioName : allScenariosWithJSONPath.keySet()) {
                    if (failedScenarios.get(scenarioName) != null &&
                            failedScenarios.get(scenarioName).equalsIgnoreCase("Failed")) {
                        String newPath = allScenariosWithJSONPath.get(scenarioName).
                                replace("cucumber-report", "final-report");
                        copyFile(allScenariosWithJSONPath.get(scenarioName), newPath);
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception occurred while getting rerun key scenarios from Maria DB: " + e);
            }
            //If rerunKey and rerunFile was not given, then copy all the JSON files to final-report folder
        } else {
            for (String scenarioName : allScenariosWithJSONPath.keySet()) {
                String newPath = allScenariosWithJSONPath.get(scenarioName).
                        replace("cucumber-report", "final-report");
                copyFile(allScenariosWithJSONPath.get(scenarioName), newPath);
            }
        }
        System.out.println("Generating Report");
        String folderName = ReusableCommonMethods.getTodaysDateAndTime("dd_MM_yyyy_HH_mm_ss");

        //Initialize the jsonFiles object and then fetch the final report directory JSON files only as we need
        //to ignore the skipped test cases from rerunKey or rerunFile
        jsonFiles = new ArrayList<>();
        jsonFiles = getJSONFileNames(System.getProperty("user.dir") + "\\target\\final-report\\");

        //Capture the failed scenarios in the current build
        for (String jsonFileName:
             jsonFiles) {
            getScenarioNameWithOverallStatus(jsonFileName);
        }
        //generate the JSON file under resources/FailedScenarios containing the failed scenarios
        generateFailedScenariosJSONFile(failedScenarioNames, System.getProperty("user.dir") +
                "\\src\\test\\resources\\FailedScenarios\\FailedScenarios_" + folderName);
        //write failed scenarios with current date and time as runid in Maria DB
        if (WRITE_FAILED_CASES_TO_MYSQL_DB != null &&
                WRITE_FAILED_CASES_TO_MYSQL_DB.equalsIgnoreCase("Yes")) {
            writeFailedScenariosInMySQLDB(failedScenarioNames, folderName);
        } else {
            System.out.println("WRITE_FAILED_CASES_TO_MYSQL_DB property is set as NO in global config properties file");
        }
        //Generate Cucumber Report locally if the property is set as Yes
        if(generateLocalCucumberReport!=null && generateLocalCucumberReport.equalsIgnoreCase("Yes")) {
            generateReport(folderName);
        }
    }

    /**
     * This method will generate the Cucumber Report. You may set the parameters according to your need.
     * @param folderName - Folder Path in which cucumber report needs to be generated
     */
    public static void generateReport(String folderName) {
        try {
            File reportOutputDirectory = new File("src\\test\\resources\\CucumberReports\\" + folderName);
            String buildNumber = "1";
            String projectName = "BDDFramework";

            Configuration configuration = new Configuration(reportOutputDirectory, projectName);
            // optional configuration - check javadoc for details
            configuration.addPresentationModes(PresentationMode.RUN_WITH_JENKINS);
            // do not make scenario failed when step has status SKIPPED
            configuration.setNotFailingStatuses(Collections.singleton(Status.SKIPPED));
            configuration.setBuildNumber(buildNumber);
            // addidtional metadata presented on main page
            configuration.addClassifications("Platform", "Windows");
            configuration.addClassifications("Browser", "Firefox");
            configuration.addClassifications("Branch", "release/1.0");

            /*// optionally add metadata presented on main page via properties file
            List<String> classificationFiles = new ArrayList<>();
            classificationFiles.add("properties-1.properties");
            classificationFiles.add("properties-2.properties");
            configuration.addClassificationFiles(classificationFiles);

            // optionally specify qualifiers for each of the report json files
            configuration.addPresentationModes(PresentationMode.PARALLEL_TESTING);
            configuration.setQualifier("cucumber-report-1", "First report");
            configuration.setQualifier("cucumber-report-2", "Second report");*/

            ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
            Reportable result = reportBuilder.generateReports();
            // and here validate 'result' to decide what to do if report has failed
        } catch (Exception e) {

            System.out.println("Exception occurred while generating report : " + e);
        }
    }

    /**
     * This method will return all the JSON files from a directory given as parameter
     * @param folderPath - Folder Path from where the JSON files need to be read
     * @return List<String>
     */
    public static List<String> getJSONFileNames(String folderPath) {
        try {
            File folder = new File(folderPath);
            File[] listOfFiles = folder.listFiles();
            int totalFiles = 0;
            if (listOfFiles != null) {
                totalFiles = listOfFiles.length;
            }
            List<String> jsonFileName = new ArrayList<>(totalFiles);
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    if (file.getName().trim().endsWith("json")) {
                        jsonFileName.add(folderPath + "\\" + file.getName());
                    }
                }
            }
            return jsonFileName;

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }


    /**
     * This method will scan each step of a scenario and validate if a scenario is failed or not.
     * If the scenario is failed it stores the failed scenario name in a List<String>
     * @param jsonPath - Full path of the JSON file
     */
    public static void getScenarioNameWithOverallStatus(String jsonPath) {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(jsonPath));
            JSONArray jsonArray = (JSONArray) obj;
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            JSONArray jsonArrayElements = (JSONArray) jsonObject.get("elements");
            for (int totalSteps = 0; totalSteps < jsonArrayElements.size(); totalSteps++) {
                jsonObject = (JSONObject) jsonArrayElements.get(totalSteps);
                jsonArray = (JSONArray) jsonObject.get("steps");
                if (!getResultFromSteps(jsonArray)) {
                    storeFailedScenarios(jsonArrayElements);
                }
            }
        } catch (Exception e) {
            System.out.println("Exception : " + e);
        }

    }

    /**
     * this method will scan each step and validate if the scenario is failed
     * @param jsonArray - the JSONArray object of the current step in JSON file
     * @return true for pass and false if the scenario is failed
     */
    public static boolean getResultFromSteps(JSONArray jsonArray) {
        try {
            for (int step = 0; step < jsonArray.size(); step++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(step);
                JSONObject result = (JSONObject) jsonObject.get("result");
                String status = (String) result.get("status");
                System.out.println(status);
                if (status.equalsIgnoreCase("failed") || status.equalsIgnoreCase("skipped")) {
                    return false;
                }
            }
            return true;

        } catch (Exception e) {
            System.out.println("Exception occurred while fetching result status from JSONArray : " + e);
            return false;
        }
    }

    /**
     * This method will write failed scenario name in a global variable
     * @param jsonArrayElements - JSONArray object of "element" in the JSON File.
     */
    public static void storeFailedScenarios(JSONArray jsonArrayElements) {
        try {
            for (int totalSection = 0; totalSection < jsonArrayElements.size(); totalSection++) {
                String type = ((JSONObject) jsonArrayElements.get(totalSection)).get("type").toString();
                if (type.equalsIgnoreCase("scenario")) {
                    failedScenarioNames.add(((JSONObject) jsonArrayElements.get(totalSection)).get("name").toString());
                }
            }
        } catch (Exception e) {
            System.out.println("Exception occurred : " + e);
        }
    }

    /**
     * This method will generate a JSON file containing all the failed scenarios
     * @param failedScenarios - failed scenario name
     * @param fileLocation - corresponding JSON file location
     */

    public static void generateFailedScenariosJSONFile(Set<String> failedScenarios, String fileLocation) {
        try {
            JSONObject jsonObject = new JSONObject();
            for (String scenarioName : failedScenarios) {
                jsonObject.put(scenarioName.trim(), "Failed");
            }
            FileWriter file = new FileWriter(fileLocation);
            file.write(jsonObject.toJSONString());
            file.close();
        } catch (Exception e) {
            System.out.println("Exception Occurred : " + e);
        }
    }

    /**
     * This method will write all the failed scenarios in Maria DB
     * @param failedScenarios - Set of all the failed scenarios
     * @param runID - current date and time in a specific format to be used as run ID in Maria DB
     */
    public static void writeFailedScenariosInMySQLDB(Set<String> failedScenarios, String runID) {
        try {
            Connection conn = MariaDBConnection.getMySQLConnection();
            String rerunKey = generateRerunKey();
            for (String scenarioName :
                    failedScenarios) {
                String query = "INSERT INTO "+failedScenariosTableName+" VALUES " +
                        "('" + runID + "','" + scenarioName + "','" + rerunKey + "');";
                MariaDBConnection.executeQuery(query, conn);
            }
            if(conn!=null)
            conn.close();

        } catch (Exception e) {
            System.out.println("Exception occurred while inserting failed scenario details : " + e);
        }
    }

    /**
     * This method will generate a 4 digit alphanumeric rerun key which is not already existing in the Maria DB
     * @return String
     * @throws SQLException
     */
    public static String generateRerunKey() throws SQLException {
        Connection conn = MariaDBConnection.getMySQLConnection();
        String rerunKey = null;
        try {
            String query = "SELECT COUNT(*) FROM "+failedScenariosTableName+" WHERE RERUN_KEY IN ('" + rerunKey + "');";
            while (true) {
                rerunKey = ReusableCommonMethods.generateRandomAlphnumericString(4);
                if (MariaDBConnection.validateRerunKeyPresent(query, conn) <= 0) {
                    if(conn!=null)
                    conn.close();
                    return rerunKey;
                }
            }

        } catch (Exception e) {
            if(conn!=null)
            conn.close();
            return rerunKey;
        }
    }

    /**
     * This method will copy a JSON file from one location to another location
     * @param srcFileName - source JSON file name
     * @param destFileName - destination JSON file name
     * @return true if the copy was successful, false if the copy was failed
     */

    public static boolean copyFile(String srcFileName, String destFileName) {
        try {
            File srcFile = new File(srcFileName);
            File destFile = new File(destFileName);
            Files.copy(srcFile.toPath(), destFile.toPath());
            return true;
        } catch (Exception e) {
            System.out.println("Exception occurred : " + e);
            return false;
        }
    }

    /**
     * This method will create a HashMap with scenario name as Key and corresponding JSON file location as value
     * @param jsonPath - full file path of the JSON file
     */

    public static void getAllScenariosWithJSONPath(String jsonPath) {
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(jsonPath));
            JSONArray jsonArray = (JSONArray) obj;
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            JSONArray jsonArrayElements = (JSONArray) jsonObject.get("elements");
            for (int i = 0; i < jsonArrayElements.size(); i++) {
                JSONObject jsonElementObject = (JSONObject) jsonArrayElements.get(i);
                if (jsonElementObject.get("type") != null &&
                        jsonElementObject.get("type").toString().trim().equalsIgnoreCase("scenario")) {
                    allScenariosWithJSONPath.put(jsonElementObject.get("name").toString().trim(), jsonPath);
                }
            }
        } catch (Exception e) {
            System.out.println("Exception occurred : " + e);
        }
    }

}
