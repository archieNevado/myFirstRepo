import AbstractCatalogTest from "@coremedia-blueprint/studio-client.main.ec-studio-test-helper/AbstractCatalogTest";

class AbstractCatalogAssetTest extends AbstractCatalogTest {

  override setUp(): void {
    super.setUp();
    this.resetCatalogHelper();
  }
}

export default AbstractCatalogAssetTest;
