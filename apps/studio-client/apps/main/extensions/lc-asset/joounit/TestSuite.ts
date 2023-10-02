import flexunit_framework_TestSuite from "@jangaroo/joounit/flexunit/framework/TestSuite";
import InheritReferencesTest from "./InheritReferencesTest";
import SearchProductImagesTest from "./SearchProductImagesTest";

//noinspection JSUnusedGlobalSymbols
class TestSuite {
  //noinspection JSUnusedGlobalSymbols
  static suite(): flexunit_framework_TestSuite {
    const suite = new flexunit_framework_TestSuite();

    suite.addTestSuite(InheritReferencesTest);
    suite.addTestSuite(SearchProductImagesTest);

    return suite;
  }
}

export default TestSuite;
