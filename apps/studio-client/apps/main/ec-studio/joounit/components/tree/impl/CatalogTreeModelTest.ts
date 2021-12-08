import EditorContextImpl from "@coremedia/studio-client.main.editor-components/sdk/EditorContextImpl";
import Assert from "@jangaroo/joounit/flexunit/framework/Assert";
import { as } from "@jangaroo/runtime";
import ECommerceStudioPlugin_properties from "../../../../src/ECommerceStudioPlugin_properties";
import CatalogTreeModel from "../../../../src/components/tree/impl/CatalogTreeModel";
import AbstractCatalogStudioTest from "../../../AbstractCatalogStudioTest";

class CatalogTreeModelTest extends AbstractCatalogStudioTest {

  #catalogTreeModel: CatalogTreeModel = null;

  override setUp(): void {
    super.setUp();

    EditorContextImpl.initEditorContext();

    //noinspection BadExpressionStatementJS
    this.#catalogTreeModel = new CatalogTreeModel();
    this.#catalogTreeModel.getSortCategoriesByName = ((): boolean =>
      true
    );
  }

  testGetStoreText(): void {
    this.waitUntil("wait for store text",
      (): boolean => !!this.#catalogTreeModel.getText(AbstractCatalogStudioTest.STORE_ID),
      (): void => Assert.assertEquals(this.#catalogTreeModel.getText(AbstractCatalogStudioTest.STORE_ID), AbstractCatalogStudioTest.STORE_NAME),
    );
  }

  testGetTopCategoryText(): void {
    this.waitUntil("wait for the top category loaded",
      (): boolean => !!this.#catalogTreeModel.getText(AbstractCatalogStudioTest.TOP_CATEGORY_ID),
      (): void => Assert.assertEquals(this.#catalogTreeModel.getText(AbstractCatalogStudioTest.TOP_CATEGORY_ID), AbstractCatalogStudioTest.TOP_CATEGORY_ID),
    );
  }

  testGetLeafCategoryText(): void {
    this.waitUntil("wait for the leaf category loaded",
      (): boolean => !!this.#catalogTreeModel.getText(AbstractCatalogStudioTest.LEAF_CATEGORY_ID),
      (): void => Assert.assertEquals(this.#catalogTreeModel.getText(AbstractCatalogStudioTest.LEAF_CATEGORY_ID), AbstractCatalogStudioTest.LEAF_CATEGORY_ID),
    );
  }

  testGetTopCategoryIdPath(): void {
    this.waitUntil("wait for the top categories loaded",
      (): boolean => {
        const idPaths = as(this.#catalogTreeModel.getIdPath(AbstractCatalogStudioTest.TOP_CATEGORY_ID), Array);
        return idPaths && idPaths.length === 3;
      },
      (): void => {
        const idPaths = as(this.#catalogTreeModel.getIdPath(AbstractCatalogStudioTest.TOP_CATEGORY_ID), Array);
        Assert.assertEquals(idPaths[0], AbstractCatalogStudioTest.STORE_ID);
        Assert.assertEquals(idPaths[1], AbstractCatalogStudioTest.ROOT_CATEGORY_ID);
        Assert.assertEquals(idPaths[2], AbstractCatalogStudioTest.TOP_CATEGORY_ID);
      },
    );
  }

  testGetLeafCategoryIdPath(): void {
    this.waitUntil("wait for leaf category id path",
      (): boolean => !!this.#catalogTreeModel.getIdPath(AbstractCatalogStudioTest.LEAF_CATEGORY_ID),
      (): void => {
        const idPaths = as(this.#catalogTreeModel.getIdPath(AbstractCatalogStudioTest.LEAF_CATEGORY_ID), Array);
        Assert.assertEquals(4, idPaths.length);
        Assert.assertEquals(AbstractCatalogStudioTest.STORE_ID, idPaths[0]);
        Assert.assertEquals(AbstractCatalogStudioTest.ROOT_CATEGORY_ID, idPaths[1]);
        Assert.assertEquals(AbstractCatalogStudioTest.TOP_CATEGORY_ID, idPaths[2]);
        Assert.assertEquals(AbstractCatalogStudioTest.LEAF_CATEGORY_ID, idPaths[3]);
      },
    );
  }

  testGetTopCategoryChildren(): void {
    this.waitUntil("wait for top category children",
      (): boolean => !!this.#catalogTreeModel.getChildren(AbstractCatalogStudioTest.TOP_CATEGORY_ID),
      (): void => {
        const nodeChildren = this.#catalogTreeModel.getChildren(AbstractCatalogStudioTest.TOP_CATEGORY_ID);
        Assert.assertEquals(nodeChildren.getChildIds().length, 2);
      },
    );
  }

  testIdsAreConcatinatedWithLinkPrefix(): void {
    this.waitUntil("wait for top category children",
      (): boolean => !!this.#catalogTreeModel.getChildren(AbstractCatalogStudioTest.LINK_CATEGORY_ID),
      (): void => {
        const nodeChildren = this.#catalogTreeModel.getChildren(AbstractCatalogStudioTest.LINK_CATEGORY_ID);
        Assert.assertEquals(1, nodeChildren.getChildIds().length);
        Assert.assertEquals(CatalogTreeModel.HYPERLINK_PREFIX + AbstractCatalogStudioTest.LINK_CATEGORY_ID + CatalogTreeModel.HYPERLINK_SEPARATOR + AbstractCatalogStudioTest.LEAF_CATEGORY_ID, nodeChildren.getChildIds()[0]);
      },
    );
  }

  testGetLeafCategoryChildren(): void {
    this.waitUntil("wait for leaf category children",
      (): boolean => !!this.#catalogTreeModel.getChildren(AbstractCatalogStudioTest.LEAF_CATEGORY_ID),
      (): void => {
        const nodeChildren = this.#catalogTreeModel.getChildren(AbstractCatalogStudioTest.LEAF_CATEGORY_ID);
        Assert.assertEquals(nodeChildren.getChildIds().length, 0);
      },
    );
  }

  testGetStoreChildren(): void {
    this.waitUntil("wait for store children",
      (): boolean => !!this.#catalogTreeModel.getChildren(AbstractCatalogStudioTest.STORE_ID),
      (): void => {
        const topLevelIds = this.#catalogTreeModel.getChildren(AbstractCatalogStudioTest.STORE_ID).getChildIds();
        Assert.assertEquals(topLevelIds.length, 2);
        Assert.assertEquals(topLevelIds[0], AbstractCatalogStudioTest.MARKETING_ID);
        Assert.assertEquals(topLevelIds[1], AbstractCatalogStudioTest.ROOT_CATEGORY_ID);
      },
    );
  }

  testGetMarketingSpotsText(): void {
    this.waitUntil("wait for tree to be build",
      (): boolean => !!this.#catalogTreeModel.getText(AbstractCatalogStudioTest.MARKETING_ID),
      (): void => Assert.assertEquals(this.#catalogTreeModel.getText(AbstractCatalogStudioTest.MARKETING_ID), ECommerceStudioPlugin_properties.StoreTree_marketing_root),
    );
  }

  testGetRootId(): void {
    this.waitUntil("wait for root id to be loaded",
      (): boolean => this.#catalogTreeModel.getRootId() === AbstractCatalogStudioTest.STORE_ID,
      (): void =>
        Assert.assertEquals(this.#catalogTreeModel.getRootId(), AbstractCatalogStudioTest.STORE_ID),
    );
  }

  testGetStoreIdPath(): void {
    this.waitUntil("wait for store id path",
      (): boolean => !!this.#catalogTreeModel.getIdPath(AbstractCatalogStudioTest.STORE_ID),
      (): void => {
        const idPaths = this.#catalogTreeModel.getIdPath(AbstractCatalogStudioTest.STORE_ID);
        Assert.assertEquals(idPaths.length, 1);
        Assert.assertEquals(idPaths[0], AbstractCatalogStudioTest.STORE_ID);
      },
    );
  }
}

export default CatalogTreeModelTest;
