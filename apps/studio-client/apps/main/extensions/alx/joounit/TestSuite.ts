import flexunit_framework_TestSuite from "@jangaroo/joounit/flexunit/framework/TestSuite";
import AnalyticsDeepLinkButtonContainerTest from "./AnalyticsDeepLinkButtonContainerTest";
import OpenAnalyticsDeepLinkUrlButtonTest from "./OpenAnalyticsDeepLinkUrlButtonTest";
import OpenAnalyticsHomeUrlButtonTest from "./OpenAnalyticsHomeUrlButtonTest";

class TestSuite {
  static suite(): flexunit_framework_TestSuite {
    const testSuite = new flexunit_framework_TestSuite();
    testSuite.addTestSuite(AnalyticsDeepLinkButtonContainerTest);
    testSuite.addTestSuite(OpenAnalyticsHomeUrlButtonTest);
    testSuite.addTestSuite(OpenAnalyticsDeepLinkUrlButtonTest);
    return testSuite;
  }
}

export default TestSuite;
