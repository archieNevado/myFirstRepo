import flexunit_framework_TestSuite from "@jangaroo/joounit/flexunit/framework/TestSuite";
import CatalogObjectTest from "./model/CatalogObjectTest";
import CategoryTest from "./model/CategoryTest";
import MarketingSpotTest from "./model/MarketingSpotTest";
import MarketingTest from "./model/MarketingTest";
import ProductTest from "./model/ProductTest";
import StoreTest from "./model/StoreTest";

class TestSuite {
  static suite(): flexunit_framework_TestSuite {
    const suite = new flexunit_framework_TestSuite();

    suite.addTestSuite(CatalogObjectTest);
    suite.addTestSuite(StoreTest);
    suite.addTestSuite(MarketingTest);
    suite.addTestSuite(MarketingSpotTest);
    suite.addTestSuite(CategoryTest);
    suite.addTestSuite(ProductTest);
    return suite;
  }
}

export default TestSuite;
