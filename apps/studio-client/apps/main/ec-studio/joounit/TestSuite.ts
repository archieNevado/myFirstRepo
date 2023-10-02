import flexunit_framework_TestSuite from "@jangaroo/joounit/flexunit/framework/TestSuite";
import CatalogRepositoryListTest from "./components/repository/CatalogRepositoryListTest";
import AugmentedCategoryTreeRelationTest from "./components/tree/AugmentedCategoryTreeRelationTest";
import ShowInLibraryHelperTest from "./components/tree/ShowInLibraryHelperTest";
import CatalogTreeModelTest from "./components/tree/impl/CatalogTreeModelTest";
import CatalogHelperTest from "./helper/CatalogHelperTest";

class TestSuite {
  static suite(): flexunit_framework_TestSuite {
    const suite = new flexunit_framework_TestSuite();
    suite.addTestSuite(CatalogHelperTest);
    suite.addTestSuite(CatalogTreeModelTest);
    suite.addTestSuite(AugmentedCategoryTreeRelationTest);
    suite.addTestSuite(CatalogRepositoryListTest);
    suite.addTestSuite(ShowInLibraryHelperTest);
    return suite;
  }
}

export default TestSuite;
