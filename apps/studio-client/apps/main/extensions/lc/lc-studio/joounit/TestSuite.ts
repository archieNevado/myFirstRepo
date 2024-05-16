import flexunit_framework_TestSuite from "@jangaroo/joounit/flexunit/framework/TestSuite";
import CollectionViewModelActionTest from "./action/CollectionViewModelActionTest";
import CatalogCollectionViewTest from "./collectionview/CatalogCollectionViewTest";
import CatalogLinkPropertyFieldTest from "./components/link/CatalogLinkPropertyFieldTest";
import ProductTeaserDocumentFormTest from "./forms/ProductTeaserDocumentFormTest";
import ProductTeaserSettingsFormTest from "./forms/ProductTeaserSettingsFormTest";
import ShowInCatalogTreeHelperTest from "./library/ShowInCatalogTreeHelperTest";

class TestSuite {
  static suite(): flexunit_framework_TestSuite {
    const suite = new flexunit_framework_TestSuite();

    suite.addTestSuite(CatalogCollectionViewTest);
    suite.addTestSuite(CollectionViewModelActionTest);
    suite.addTestSuite(CatalogLinkPropertyFieldTest);
    suite.addTestSuite(ProductTeaserDocumentFormTest);
    suite.addTestSuite(ShowInCatalogTreeHelperTest);
    suite.addTestSuite(ProductTeaserSettingsFormTest);

    return suite;
  }
}

export default TestSuite;
