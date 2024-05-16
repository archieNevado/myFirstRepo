import AbstractCatalogTest from "@coremedia-blueprint/studio-client.main.ec-studio-test-helper/AbstractCatalogTest";

class AbstractCatalogStudioTest extends AbstractCatalogTest {

  protected static readonly HELIOS_SITE_ID: string = "HeliosSiteId";

  protected static readonly STORE_CONTEXT_ID: string = AbstractCatalogStudioTest.HELIOS_SITE_ID;

  protected static readonly STORE_CATALOG_CONTEXT_ID: string = AbstractCatalogStudioTest.HELIOS_SITE_ID + "/catalog";

  protected static readonly STORE_ID: string = "livecontext/store/" + AbstractCatalogStudioTest.STORE_CONTEXT_ID;

  protected static readonly ROOT_CATEGORY_ID: string = "livecontext/category/" + AbstractCatalogStudioTest.STORE_CATALOG_CONTEXT_ID + "/ROOT";

  protected static readonly MARKETING_ID: string = "livecontext/marketing/" + AbstractCatalogStudioTest.STORE_CONTEXT_ID;

  protected static readonly TOP_CATEGORY_ID: string = "livecontext/category/" + AbstractCatalogStudioTest.STORE_CATALOG_CONTEXT_ID + "/Grocery";

  protected static readonly LEAF_CATEGORY_ID: string = "livecontext/category/" + AbstractCatalogStudioTest.STORE_CATALOG_CONTEXT_ID + "/Fruit";

  protected static readonly LINK_CATEGORY_ID: string = "livecontext/category/" + AbstractCatalogStudioTest.STORE_CATALOG_CONTEXT_ID + "/Link";

  protected static readonly STORE_NAME: string = "PerfectChefESite";

  protected static readonly TOP_CATEGORY_EXTERNAL_ID: string = "Grocery";

  protected static readonly LEAF_CATEGORY_EXTERNAL_ID: string = "Grocery Fruit";

  protected static readonly SITE_ROOT_DOCUMENT_ID: string = "content/400";

  protected static readonly ROOT_CATEGORY_DOCUMENT_ID: string = "content/500";

  protected static readonly TOP_CATEGORY_DOCUMENT_ID: string = "content/600";

  protected static readonly LEAF_CATEGORY_DOCUMENT_ID: string = "content/700";

  override setUp(): void {
    super.setUp();
    this.resetCatalogHelper();
  }
}

export default AbstractCatalogStudioTest;
