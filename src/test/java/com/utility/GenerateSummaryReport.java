package com.utility;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

public class GenerateSummaryReport {

    public static List<HashMap<String, String>> allFailedScenarios = new ArrayList<>();

    public static void main(String[] args) throws IOException, ParseException {
        List<HashMap<String, String>> consolidatedScenarioWiseResult = new ArrayList<>();
        String scenarioName;
        String featureName ;
        JSONParser parser = new JSONParser();
        //Parse the cucumber-report.json file. Please change the path according to your project
        Object obj = parser.parse(new FileReader(
                System.getProperty("user.dir") + "\\target\\cucumber-report\\cucumber-report.json"));
        //rootArray defines the number of feature files in the cucumber-report.json
        JSONArray rootArray = (JSONArray) obj;
        //loop through each feature file
        for (int featureID = 0; featureID < rootArray.size(); featureID++) {
            JSONObject rootMap = (JSONObject) rootArray.get(featureID);
            featureName = rootMap.get("name").toString();
            //elementsArray defines the number of scenario for a feature file
            JSONArray elementsArray = (JSONArray) rootMap.get("elements");
            //loop through each scenario of a feature file
            for (int i = 0; i < elementsArray.size(); i++) {
                JSONObject scenarioWiseObject = (JSONObject) elementsArray.get(i);
                scenarioName = scenarioWiseObject.get("name").toString();
                //Validate if any step got failed in case @Before step present
                //If any step got failed, break the loop and store in a List<HashMap<String, String>>
                if (scenarioWiseObject.get("before") != null) {
                    ScenarioDetails result = getScenarioStatus((JSONArray) scenarioWiseObject.get("before"));
                    if (!result.status) {
                        HashMap<String, String> temp = new HashMap<>(4);
                        temp.put("FEATURE_NAME", featureName);
                        temp.put("SCENARIO_NAME", scenarioName);
                        temp.put("STATUS", "FAIL");
                        temp.put("ERROR_MESSAGE", result.faiureMessage);
                        consolidatedScenarioWiseResult.add(temp);
                        continue;
                    }
                }
                //Validate if any step got failed in scenario steps
                //If any step got failed, break the loop and store in a List<HashMap<String, String>>
                if (scenarioWiseObject.get("steps") != null) {
                    ScenarioDetails result = getScenarioStatus((JSONArray) scenarioWiseObject.get("steps"));
                    if (!result.status) {
                        HashMap<String, String> temp = new HashMap<>(4);
                        temp.put("FEATURE_NAME", featureName);
                        temp.put("SCENARIO_NAME", scenarioName);
                        temp.put("STATUS", "FAIL");
                        temp.put("ERROR_MESSAGE", result.faiureMessage);
                        consolidatedScenarioWiseResult.add(temp);
                        continue;
                    }
                }
                //Validate if any step got failed in case @After steps present
                //If any step got failed, break the loop and store in a List<HashMap<String, String>>
                if (scenarioWiseObject.get("after") != null) {
                    ScenarioDetails result = getScenarioStatus((JSONArray) scenarioWiseObject.get("after"));
                    if (!result.status) {
                        HashMap<String, String> temp = new HashMap<>(4);
                        temp.put("FEATURE_NAME", featureName);
                        temp.put("SCENARIO_NAME", scenarioName);
                        temp.put("STATUS", "FAIL");
                        temp.put("ERROR_MESSAGE", result.faiureMessage);
                        consolidatedScenarioWiseResult.add(temp);
                        continue;
                    }
                }
                //At this point the scenario would have definitely passed, hence storing the result as Pass
                HashMap<String, String> temp = new HashMap<>(4);
                temp.put("FEATURE_NAME", featureName);
                temp.put("SCENARIO_NAME", scenarioName);
                temp.put("STATUS", "PASS");
                temp.put("ERROR_MESSAGE", "NA");
                consolidatedScenarioWiseResult.add(temp);

            }
        }

        //Call the summary details method to get pass, fail, pass percentage, fail percentage for each feature file
        //as well as in total
        List<HashMap<String, String>> summary = getSummaryDetails(consolidatedScenarioWiseResult);
        //below method generates the HTML report using the summary data.
        generateHTMLReport(summary);

    }

    public static ScenarioDetails getScenarioStatus(JSONArray stepName) {
        try {
            JSONObject stepObject;
            JSONObject resultObject;
            //iterate over each steps of a scenario
            for (int i = 0; i < stepName.size(); i++) {
                stepObject = (JSONObject) stepName.get(i);
                resultObject = (JSONObject) stepObject.get("result");
                //check for the status of the steps and if any of the step is fail get the error message
                if (resultObject.get("status") != null && !resultObject.get("status").toString().trim().contains("passed")) {
                    if (resultObject.get("error_message") != null) {
                        return new ScenarioDetails(false, resultObject.get("error_message").toString());
                    } else {
                        return new ScenarioDetails(false, "Error Message Not Found");
                    }
                }
            }
            return new ScenarioDetails(true, "");
        } catch (Exception e) {
            System.out.println("Exception occurred while getting scenario status");
            return new ScenarioDetails(false, "Exception - " + e);

        }
    }
    //Below method will accept the consolidatedScenarioWiseResult and calculate the summary to be put in HTML report
    public static List<HashMap<String, String>> getSummaryDetails(List<HashMap<String, String>> consolidatedScenarioWiseResult) {
        try {
            int totalScenario = 0;
            int totalPass = 0;
            int totalFail = 0;
            //Get all the feature file name first in a HashSet to filter duplicate
            Set<String> allFeatureName = new HashSet<>(consolidatedScenarioWiseResult.size());
            List<HashMap<String, String>> summary = new ArrayList<>();
            for (int i = 0; i < consolidatedScenarioWiseResult.size(); i++) {
                allFeatureName.add(consolidatedScenarioWiseResult.get(i).get("FEATURE_NAME"));
            }
            Iterator<String> featureIT = allFeatureName.iterator();
            while (featureIT.hasNext()) {
                int total = 0;
                int pass = 0;
                int fail = 0;
                String featureName = featureIT.next();
                for (int i = 0; i < consolidatedScenarioWiseResult.size(); i++) {
                    if (consolidatedScenarioWiseResult.get(i).get("FEATURE_NAME").equalsIgnoreCase(featureName)) {
                        total++;
                        if (consolidatedScenarioWiseResult.get(i).get("STATUS").equalsIgnoreCase("PASS")) {
                            pass++;
                        } else {
                            fail++;
                            allFailedScenarios.add(consolidatedScenarioWiseResult.get(i));
                        }
                    }
                }
                HashMap<String, String> temp = new HashMap<>();
                temp.put("FEATURE_NAME", featureName);
                temp.put("TOTAL", "" + total);
                temp.put("PASS", "" + pass);
                temp.put("FAIL", "" + fail);
                double pass_per = ((double) pass / (double) total) * 100;
                double fail_per = ((double) fail / (double) total) * 100;
                temp.put("P_PER", "" + pass_per);
                temp.put("F_PER", "" + fail_per);
                summary.add(temp);
                totalScenario = totalScenario + total;
                totalPass = totalPass + pass;
                totalFail = totalFail + fail;
            }
            HashMap<String, String> temp = new HashMap<>();
            temp.put("FEATURE_NAME", "TOTAL");
            temp.put("TOTAL", "" + totalScenario);
            temp.put("PASS", "" + totalPass);
            temp.put("FAIL", "" + totalFail);
            double pass_per = ((double) totalPass / (double) totalScenario) * 100;
            double fail_per = ((double) totalFail / (double) totalScenario) * 100;
            temp.put("P_PER", "" + String.format("%.2f", pass_per));
            temp.put("F_PER", "" + String.format("%.2f", fail_per));
            summary.add(temp);
            return summary;
        } catch (Exception e) {
            System.out.println("Exception occurred while getting summary : " + e);
            return new ArrayList<>();
        }
    }

    //Below code will concat the required HTML Code with all the summary details and generate the HTML report.
    public static void generateHTMLReport(List<HashMap<String, String>> summary) {

        String htmlCode = "<html>\n" +
                "<head>\n" +
                "\t<style>\n" +
                "\ttable,\n" +
                "\tth,\n" +
                "\ttd {\n" +
                "\t\tborder: 1px solid black;\n" +
                "\t\tborder-collapse: collapse;\n" +
                "\t}\n" +
                "\t\n" +
                "\t\n" +
                "\tth {\n" +
                "\t\ttext-align: center;\n" +
                "\t}\n" +
                "\t</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\t<table style=\"width:100%\">\n" +
                "\t    <caption style=\"background-color:#000000;color:white\">Sample Project Name</caption>\n" +
                "\t\t<caption style=\"background-color:#4287f5;color:white\">Test Automation Summary Report</caption>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<th>Feature Name</th>\n" +
                "\t\t\t<th>Total Scenario</th>\n" +
                "\t\t\t<th>Pass</th>\n" +
                "\t\t\t<th>Fail</th>\n" +
                "\t\t\t<th>Pass Percentage</th>\n" +
                "\t\t\t<th>Fail Percentage</th>\n" +
                "\t\t\t\n" +
                "\t\t</tr>";
        for (int i = 0; i < summary.size(); i++) {
            htmlCode = htmlCode + "<tr><td>" + summary.get(i).get("FEATURE_NAME") + "</td>\n" +
                    "\t\t\t<td>" + summary.get(i).get("TOTAL") + "</td>\n" +
                    "\t\t\t<td>" + summary.get(i).get("PASS") + "</td>\n" +
                    "\t\t\t<td>" + summary.get(i).get("FAIL") + "</td>\n" +
                    "\t\t\t<td>" + summary.get(i).get("P_PER") + "</td>\n" +
                    "\t\t\t<td>" + summary.get(i).get("F_PER") + "</td></tr>";
        }
        htmlCode = htmlCode + "</table>\n" +
                "\t<br></br>";
        if (!allFailedScenarios.isEmpty()) {
            htmlCode = htmlCode + "<table style=\"width:100%\">\n" +
                    "\t\t<caption style=\"background-color:#4287f5;color:white\">Failed Scenarios</caption>\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<th>Feature Name</th>\n" +
                    "\t\t\t<th>Scenario Name</th>\n" +
                    "\t\t\t<th>Status</th>\n" +
                    "\t\t\t<th>Failure Reason</th>\n" +
                    "\t\t\t\n" +
                    "\t\t</tr>";
            for (int i = 0; i < allFailedScenarios.size(); i++) {
                htmlCode = htmlCode + "<tr>\n" +
                        "\t\t\t<td>" + allFailedScenarios.get(i).get("FEATURE_NAME") + "</td>\n" +
                        "\t\t\t<td>" + allFailedScenarios.get(i).get("SCENARIO_NAME") + "</td>\n" +
                        "\t\t\t<td>Fail</td>\n" +
                        "\t\t\t<td>" + allFailedScenarios.get(i).get("ERROR_MESSAGE") + "</td>\n" +
                        "\t\t</tr>";
            }
            htmlCode = htmlCode + "</table>\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>";
        }

        try {
            Writer fileWriter = new FileWriter(new File(System.getProperty("user.dir") + "/target/emailReport.html"));
            fileWriter.write(htmlCode);
            fileWriter.close();
            System.out.println("Report generated successfully");
        } catch (Exception e) {
            System.out.println("Exception occurred while generating the html code : " + e);
        }

    }
}
