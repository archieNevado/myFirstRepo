import flexunit_framework_TestSuite from "@jangaroo/joounit/flexunit/framework/TestSuite";
import EsAnalyticsChartWidgetEditorTest from "./EsAnalyticsChartWidgetEditorTest";

class TestSuite {
  static suite(): flexunit_framework_TestSuite {
    const testSuite = new flexunit_framework_TestSuite();
    testSuite.addTestSuite(EsAnalyticsChartWidgetEditorTest);
    return testSuite;
  }
}

export default TestSuite;
