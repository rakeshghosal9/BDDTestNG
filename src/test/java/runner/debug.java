package runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        glue = "stepdefinition",
        features = {"src\\test\\resources\\features\\stock_Analysis"},
        plugin = {"json:target/cucumber-report/StockAnalysis.json"},
        tags = "@STOCK_ANALYSIS"
)

public class debug {
}
