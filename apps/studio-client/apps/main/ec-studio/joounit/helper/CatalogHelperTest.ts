import Assert from "@jangaroo/joounit/flexunit/framework/Assert";
import TestCase from "@jangaroo/joounit/flexunit/framework/TestCase";
import CatalogHelper from "../../src/helper/CatalogHelper";

class CatalogHelperTest extends TestCase {
  constructor() {
    super();
  }

  catalogHelper: CatalogHelper = CatalogHelper.getInstance();

  testGetToken(): void {
    Assert.assertStrictlyEquals(undefined, this.catalogHelper.getToken("asdf"));
    Assert.assertStrictlyEquals("/category/", this.catalogHelper.getToken("vendor:///catalog/category/TEST"));
    Assert.assertStrictlyEquals("/product/", this.catalogHelper.getToken("vendor:///catalog/product/techId:TEST"));
  }

  testGetAliasIdFromId(): void {
    Assert.assertEquals("alias", this.catalogHelper.getCatalogAliasFromId("vendor:///catalog/category/catalog:alias;TEST"));
    Assert.assertEquals("catalog", this.catalogHelper.getCatalogAliasFromId("vendor:///catalog/category/catalog:;TEST"));
    Assert.assertEquals("catalog", this.catalogHelper.getCatalogAliasFromId("vendor:///catalog/category/TEST"));
  }

  testGetExternalIdFromId(): void {
    Assert.assertEquals("TEST", this.catalogHelper.getExternalIdFromId("vendor:///catalog/category/catalog:alias;TEST"));
    Assert.assertEquals("catalog:;TEST", this.catalogHelper.getExternalIdFromId("vendor:///catalog/category/catalog:;TEST"));
    Assert.assertEquals("TEST", this.catalogHelper.getExternalIdFromId("vendor:///catalog/category/TEST"));
  }
}

export default CatalogHelperTest;
