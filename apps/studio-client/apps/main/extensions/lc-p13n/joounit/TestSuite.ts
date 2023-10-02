import flexunit_framework_TestSuite from "@jangaroo/joounit/flexunit/framework/TestSuite";
import CommerceCatalogObjectsSelectFormTest from "./CommerceCatalogObjectsSelectFormTest";

class TestSuite {
  static suite(): flexunit_framework_TestSuite {
    const suite = new flexunit_framework_TestSuite();
    suite.addTestSuite(CommerceCatalogObjectsSelectFormTest);
    return suite;
  }
}

export default TestSuite;
