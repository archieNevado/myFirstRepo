import flexunit_framework_TestSuite from "@jangaroo/joounit/flexunit/framework/TestSuite";
import GoogleAnalyticsStudioButtonTest from "./GoogleAnalyticsStudioButtonTest";

class TestSuite {
  static suite(): flexunit_framework_TestSuite {
    const testSuite = new flexunit_framework_TestSuite();
    testSuite.addTestSuite(GoogleAnalyticsStudioButtonTest);
    return testSuite;
  }
}

export default TestSuite;
