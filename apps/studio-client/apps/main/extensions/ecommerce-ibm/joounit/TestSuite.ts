import flexunit_framework_TestSuite from "@jangaroo/joounit/flexunit/framework/TestSuite";
import EcommerceIbmCatalogLinkPropertyFieldTest from "./EcommerceIbmCatalogLinkPropertyFieldTest";

class TestSuite {
  static suite(): flexunit_framework_TestSuite {
    const suite = new flexunit_framework_TestSuite();

    suite.addTestSuite(EcommerceIbmCatalogLinkPropertyFieldTest);

    return suite;
  }
}

export default TestSuite;
