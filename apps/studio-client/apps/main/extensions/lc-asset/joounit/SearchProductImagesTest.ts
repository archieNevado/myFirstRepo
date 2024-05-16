import Store from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Store";
import ECommerceStudioPlugin from "@coremedia-blueprint/studio-client.main.ec-studio/ECommerceStudioPlugin";
import ECommerceStudioPlugin_properties from "@coremedia-blueprint/studio-client.main.ec-studio/ECommerceStudioPlugin_properties";
import CatalogRepositoryContextMenu from "@coremedia-blueprint/studio-client.main.ec-studio/components/repository/CatalogRepositoryContextMenu";
import CatalogRepositoryList from "@coremedia-blueprint/studio-client.main.ec-studio/components/repository/CatalogRepositoryList";
import CatalogRepositoryListContainer from "@coremedia-blueprint/studio-client.main.ec-studio/components/repository/CatalogRepositoryListContainer";
import CatalogTreeDragDropModel from "@coremedia-blueprint/studio-client.main.ec-studio/components/tree/impl/CatalogTreeDragDropModel";
import CatalogTreeModel from "@coremedia-blueprint/studio-client.main.ec-studio/components/tree/impl/CatalogTreeModel";
import CatalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/helper/CatalogHelper";
import ECommerceCollectionViewExtension from "@coremedia-blueprint/studio-client.main.ec-studio/library/ECommerceCollectionViewExtension";
import LivecontextStudioPluginBase from "@coremedia-blueprint/studio-client.main.lc-studio/LivecontextStudioPluginBase";
import LivecontextCollectionViewActionsPlugin from "@coremedia-blueprint/studio-client.main.lc-studio/library/LivecontextCollectionViewActionsPlugin";
import LivecontextCollectionViewExtension from "@coremedia-blueprint/studio-client.main.lc-studio/library/LivecontextCollectionViewExtension";
import LivecontextContentTreeRelation from "@coremedia-blueprint/studio-client.main.lc-studio/library/LivecontextContentTreeRelation";
import contentTreeRelationRegistry from "@coremedia/studio-client.cap-base-models/content/contentTreeRelationRegistry";
import ContentImpl from "@coremedia/studio-client.cap-rest-client-impl/content/impl/ContentImpl";
import Step from "@coremedia/studio-client.client-core-test-helper/Step";
import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import SwitchingContainer from "@coremedia/studio-client.ext.ui-components/components/SwitchingContainer";
import ContextMenuEventAdapter from "@coremedia/studio-client.ext.ui-components/util/ContextMenuEventAdapter";
import TableUtil from "@coremedia/studio-client.ext.ui-components/util/TableUtil";
import CollectionView from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionView";
import CollectionViewConstants from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewConstants";
import CollectionViewContainer from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewContainer";
import CollectionViewManagerInternal from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewManagerInternal";
import CollectionViewModel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewModel";
import LibraryTree from "@coremedia/studio-client.main.editor-components/sdk/collectionview/tree/LibraryTree";
import ComponentBasedEntityWorkAreaTabType from "@coremedia/studio-client.main.editor-components/sdk/desktop/ComponentBasedEntityWorkAreaTabType";
import SidePanelManagerImpl from "@coremedia/studio-client.main.editor-components/sdk/desktop/sidepanel/SidePanelManagerImpl";
import SidePanelStudioPlugin from "@coremedia/studio-client.main.editor-components/sdk/desktop/sidepanel/SidePanelStudioPlugin";
import sidePanelManager from "@coremedia/studio-client.main.editor-components/sdk/desktop/sidepanel/sidePanelManager";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Ext from "@jangaroo/ext-ts";
import Component from "@jangaroo/ext-ts/Component";
import ComponentManager from "@jangaroo/ext-ts/ComponentManager";
import Button from "@jangaroo/ext-ts/button/Button";
import Container from "@jangaroo/ext-ts/container/Container";
import Model from "@jangaroo/ext-ts/data/Model";
import NodeInterface from "@jangaroo/ext-ts/data/NodeInterface";
import GridPanel from "@jangaroo/ext-ts/grid/Panel";
import Item from "@jangaroo/ext-ts/menu/Item";
import TreeSelectionModel from "@jangaroo/ext-ts/selection/TreeModel";
import Toolbar from "@jangaroo/ext-ts/toolbar/Toolbar";
import { as, bind, cast } from "@jangaroo/runtime";
import { AnyFunction } from "@jangaroo/runtime/types";
import LivecontextAssetStudioPlugin from "../src/LivecontextAssetStudioPlugin";
import AbstractCatalogAssetTest from "./AbstractCatalogAssetTest";
import SearchProductImagesTestView from "./SearchProductImagesTestView";

class SearchProductImagesTest extends AbstractCatalogAssetTest {

  #testling: CollectionView = null;

  #catalogTree: LibraryTree = null;

  #searchProductPicturesContextMenuItem: Item = null;

  #getPreferredSite: AnyFunction = null;

  #preferredSiteExpression: ValueExpression = null;

  override setUp(): void {
    super.setUp();
    this.#preferredSiteExpression = ValueExpressionFactory.create("site", beanFactory._.createLocalBean({ site: "HeliosSiteId" }));
    this.#getPreferredSite = editorContext._.getSitesService().getPreferredSiteId;
    editorContext._.getSitesService().getPreferredSiteId = ((): string =>
      this.#preferredSiteExpression.getValue()
    );
    //use SidePanelStudioPlugin to register the CollectionViewContainer
    const plugin: SidePanelStudioPlugin = Ext.create(SidePanelStudioPlugin, {});
    plugin.init(editorContext._);
    //use ECommerceStudioPlugin to add CatalogRepositoryListContainer, CatalogSearchListContainer etc.
    new ECommerceStudioPlugin();
    new LivecontextCollectionViewActionsPlugin();
    new LivecontextAssetStudioPlugin();

    // For the sake of the test, let's assume everything can be opened in a tab.
    // Cleaner alternative: Register all tab types.
    ComponentBasedEntityWorkAreaTabType.canBeOpenedInTab = ((): boolean => true);
  }

  #createTestling(): void {
    const collectionViewManagerInternal =
            (as((editorContext._.getCollectionViewManager()), CollectionViewManagerInternal));

    const catalogTreeModel = new CatalogTreeModel();
    const originalGetNodeModelFunction: AnyFunction = bind(catalogTreeModel, catalogTreeModel.getNodeModel);
    catalogTreeModel.getNodeModel = ((nodeId: string): any => {
      const remoteBean = as(originalGetNodeModelFunction(nodeId), RemoteBean);
      //as the TreeModel now is lazy, we need to load the content for the Test
      remoteBean && remoteBean.load();
      return remoteBean;
    });
    catalogTreeModel["getSortCategoriesByName"] = ((): boolean =>
      true
    );

    collectionViewManagerInternal.addTreeModel(catalogTreeModel, new CatalogTreeDragDropModel(catalogTreeModel));

    const isApplicable: AnyFunction = (): boolean => false ;
    editorContext._.getCollectionViewExtender().addExtension(new ECommerceCollectionViewExtension(), isApplicable);

    const lcContentTreeRelation = new LivecontextContentTreeRelation();
    contentTreeRelationRegistry._.addExtension(lcContentTreeRelation, LivecontextStudioPluginBase.getIsExtensionApplicable(lcContentTreeRelation));
    editorContext._.getCollectionViewExtender().addExtension(new LivecontextCollectionViewExtension(), LivecontextStudioPluginBase.getIsExtensionApplicable(lcContentTreeRelation));

    const viewport = new SearchProductImagesTestView();

    const cvContainer = as(viewport.getComponent(CollectionViewContainer.ID), CollectionViewContainer);
    cast(SidePanelManagerImpl, sidePanelManager._).registerItem(cvContainer);
    this.#testling = as(cvContainer.getComponent(CollectionView.COLLECTION_VIEW_ID), CollectionView);

    new LivecontextCollectionViewActionsPlugin();

    this.#catalogTree = this.#getTree();
  }

  #getRepositoryList(): CatalogRepositoryList {
    const repositorySwitchingContainer = this.#getRepositorySwitchingContainer();
    return as(cast(CatalogRepositoryList, repositorySwitchingContainer.getComponent(CollectionViewConstants.LIST_VIEW)), CatalogRepositoryList);
  }

  #getRepositorySwitchingContainer(): SwitchingContainer {
    const myCatalogRepositoryContainer = cast(Container, this.#getCollectionModesContainer().getComponent(CollectionViewModel.REPOSITORY_MODE));
    const listViewSwitchingContainer = cast(Container, myCatalogRepositoryContainer.getComponent("listViewSwitchingContainer"));
    const repositorySwitchingContainer = cast(SwitchingContainer, listViewSwitchingContainer.getComponent(CatalogRepositoryListContainer.VIEW_CONTAINER_ITEM_ID));
    return repositorySwitchingContainer;
  }

  #getCollectionModesContainer(): SwitchingContainer {
    return cast(SwitchingContainer, this.#testling.getComponent(CollectionView.COLLECTION_MODES_CONTAINER_ITEM_ID));
  }

  #getActiveToolbar(): Toolbar {
    const itemId = this.#getCollectionModesContainer().getActiveItem().getItemId();
    if (itemId === "repository") {
      const repoContainer = as(cast(Container, this.#testling.queryById("toolbarSwitchingContainer")).queryById("catalogRepositoryToolbar"), Container);
      return as(repoContainer.queryById("commerceToolbar"), Toolbar);
    }

    const searchContainer = as(cast(Container, this.#testling.queryById("searchToolbar")).queryById("searchToolbarSwitchingContainer"), Container);
    return as(searchContainer.queryById("commerceToolbar"), Toolbar);
  }

  #getTree(): LibraryTree {
    const myCatalogRepositoryContainer = cast(Container, this.#getCollectionModesContainer().getComponent(CollectionViewModel.REPOSITORY_MODE));
    return as(myCatalogRepositoryContainer.getComponent(CollectionView.TREE_ITEM_ID), LibraryTree);
  }

  override tearDown(): void {
    super.tearDown();
    editorContext._.getSitesService().getPreferredSiteId = this.#getPreferredSite;
  }

  //noinspection JSUnusedGlobalSymbols
  /**
   * Test the product images search
   */
  testSearchProductImages(): void {
    this.chain(
      this.#initStore(),
      this.loadContentRepository(),
      this.waitForContentRepositoryLoaded(),
      this.loadContentTypes(),
      this.waitForContentTypesLoaded(),
      this.#createTestlingStep(),
      this.#selectStore(),
      this.#waitUntilStoreIsSelected(),
      this.#selectNextCatalogTreeNode(),
      this.#waitUntilMarketingSpotsAreSelected(),
      this.#selectNextCatalogTreeNode(),
      this.#waitUntilProductCatalogIsSelected(),
      //wait for the product catalog node to be expanded
      this.#waitUntilSelectedTreeNodeIsExpanded(),
      this.#selectNextCatalogTreeNode(),
      //wait for the Apparel node to be expanded
      this.#waitUntilSelectedTreeNodeIsExpanded(),
      this.#selectNextCatalogTreeNode(),
      //wait for the women node to be expanded
      this.#waitUntilSelectedTreeNodeIsExpanded(),
      this.#selectNextCatalogTreeNode(),
      //wait for the women node to be expanded
      this.#waitUntilSelectedTreeNodeIsExpanded(),
      this.#selectNextCatalogTreeNode(),
      //wait for the women node to be expanded
      this.#waitUntilSelectedTreeNodeIsExpanded(),
      this.#selectNextCatalogTreeNode(),

      //now the Dresses node is selected
      this.#waitUntilProductIsLoadedInRepositoryList(),
      this.#waitUntilSearchProductPicturesToolbarButtonIsInvisible(),
      this.#openContextMenuOnFirstItemOfRepositoryList(),
      this.#waitUntilRepositoryListContextMenuOpened(),
      this.#waitUntilSearchProductPicturesToolbarButtonIsEnabled(),
      this.#waitUntilSearchProductPicturesContextMenuIsEnabled(),
      this.#searchProductPicturesUsingContextMenu(),
      this.#waitUntilSearchModeIsActive(),
      this.#waitUntilSearchTextIsPartnumber(),
      this.#waitUntilSearchTypeIsPicture(),
      this.#waitUntilSearchFolderIsRoot(),
    );
  }

  #createTestlingStep(): Step {
    return new Step("Create the testling",
      (): boolean =>
        true
      , bind(
        this, this.#createTestling),
    );
  }

  #initStore(): Step {
    return new Step("Load Store Data",
      (): boolean => {
        const store: Store = CatalogHelper.getInstance().getActiveStoreExpression().getValue();
        return store !== null && store !== undefined;
      },
      CatalogHelper.getInstance().getActiveStoreExpression().getValue(),
    );
  }

  #getRepositoryContainer(): CatalogRepositoryList {
    const repositoryContainer = cast(Container, this.#getCollectionModesContainer().getComponent(CollectionViewModel.REPOSITORY_MODE));
    const repositorySwitch = cast(SwitchingContainer, cast(Container, repositoryContainer.getComponent("listViewSwitchingContainer")));
    const repositoryListContainer = cast(CatalogRepositoryListContainer, repositorySwitch.getComponent(CatalogRepositoryListContainer.VIEW_CONTAINER_ITEM_ID));
    //ensure type cast!!!! there are other list views too
    return as(repositoryListContainer.getComponent(CollectionViewConstants.LIST_VIEW), CatalogRepositoryList);
  }

  #selectStore(): Step {
    return new Step("Select Store Node",
      (): boolean => {
        const store: Store = CatalogHelper.getInstance().getActiveStoreExpression().getValue();
        this.#testling.setOpenPath(store);
        return !!this.#getRepositoryContainer() && this.#getRepositoryContainer().rendered && !!this.#getRepositoryContainer().getStore();
      },
    );
  }

  #waitUntilStoreIsSelected(): Step {
    return new Step("catalog tree should select the store",
      (): boolean => {
        const selection = this.#catalogTree.getSelection();
        return selection.length === 1 && cast(Model, selection[0]).get("text") === "PerfectChefESite";
      },
      (): void => {
        const selectionModel = cast(TreeSelectionModel, this.#catalogTree.getSelectionModel());
        selectionModel.getSelection()[0]["expand"]();
      });
  }

  #selectNextCatalogTreeNode(): Step {
    return new Step("selecting next catalog tree node",
      (): boolean =>
        true

      ,
      (): void => {
        const selectionModel = cast(TreeSelectionModel, this.#catalogTree.getSelectionModel());
        selectionModel.selectNext();
        selectionModel.getSelection()[0]["expand"]();
      });
  }

  #waitUntilMarketingSpotsAreSelected(): Step {
    return new Step("catalog tree should select the marketing root",
      (): boolean =>
        this.#catalogTree.getSelection().length > 0 &&
                      ECommerceStudioPlugin_properties.StoreTree_marketing_root
                      === cast(Model, this.#catalogTree.getSelection()[0]).get("text"),

    );
  }

  #waitUntilProductCatalogIsSelected(): Step {
    return new Step("catalog tree should select the product catalog",
      (): boolean =>
        this.#catalogTree.getSelection().length > 0 &&
                      ("Product Catalog" === cast(Model, this.#catalogTree.getSelection()[0]).get("text") ||
                      "Produktkatalog" === cast(Model, this.#catalogTree.getSelection()[0]).get("text"))

      ,
      (): void => {
        cast(TreeSelectionModel, this.#catalogTree.getSelectionModel()).selectNext();
      });
  }

  #waitUntilRepositoryListContextMenuOpened(): Step {
    return new Step("Wait for the context menu on the repository list to be opened",
      (): boolean =>
        !!this.#findCatalogRepositoryContextMenu(),

    );
  }

  #getProductPicturesSearchButton(): Button {
    return cast(Button, this.#getActiveToolbar().queryById(LivecontextAssetStudioPlugin.SEARCH_PRODUCT_PICTURES_BUTTON_ITEM_ID));
  }

  #waitUntilSearchModeIsActive(): Step {
    return new Step("Search Mode should be active",
      (): boolean =>
        this.#getCollectionModesContainer().getActiveItemValue() === CollectionViewModel.SEARCH_MODE,

    );
  }

  #waitUntilSearchTextIsPartnumber(): Step {
    return new Step("Search Text should be the part number of the product",
      (): boolean => {
        const mainStateBean = this.#testling.getCollectionViewModel().getMainStateBean();
        return mainStateBean.get(CollectionViewModel.SEARCH_TEXT_PROPERTY) === "AuroraWMDRS-1";
      },
    );
  }

  #waitUntilSearchTypeIsPicture(): Step {
    return new Step("Search Type should be CMPicture",
      (): boolean => {
        const mainStateBean = this.#testling.getCollectionViewModel().getMainStateBean();
        return mainStateBean.get(CollectionViewModel.CONTENT_TYPE_PROPERTY) === "CMPicture";
      },
    );
  }

  #waitUntilSearchFolderIsRoot(): Step {
    return new Step("Search Folder should be root",
      (): boolean => {
        const mainStateBean = this.#testling.getCollectionViewModel().getMainStateBean();
        const folder: ContentImpl = mainStateBean.get(CollectionViewModel.FOLDER_PROPERTY);
        return folder.getPath() === "/";
      },
    );
  }

  #waitUntilProductIsLoadedInRepositoryList(): Step {
    return new Step("Wait for the repository list to be loaded with products",
      (): boolean =>
        this.#getRepositoryList().getStore().getCount() > 0 &&
                      Ext.get(TableUtil.getCellAsDom(this.#getRepositoryList(), 0, 0)).query("[aria-label]")[0].getAttribute("aria-label") === ECommerceStudioPlugin_properties.Product_label,

    );
  }

  #waitUntilSelectedTreeNodeIsExpanded(): Step {
    return new Step("Wait for the selected node of the catalog tree to be expanded",
      (): boolean => {
        const selectionModel = cast(TreeSelectionModel, this.#catalogTree.getSelectionModel());
        const selection = selectionModel.getSelection();
        return selection.length === 1 && cast(NodeInterface, selection[0]).isExpanded();
      },
    );
  }

  #waitUntilSearchProductPicturesToolbarButtonIsInvisible(): Step {
    return new Step("Wait for the product pictures search toolbar button is invisible",
      (): boolean =>
        this.#getProductPicturesSearchButton().hidden,

    );
  }

  #waitUntilSearchProductPicturesToolbarButtonIsEnabled(): Step {
    return new Step("Wait for the product pictures search toolbar button is enabled",
      (): boolean =>
        !this.#getProductPicturesSearchButton().disabled,

    );
  }

  #waitUntilSearchProductPicturesContextMenuIsEnabled(): Step {
    return new Step("Wait for the product pictures search context menu item is enabled",
      (): boolean =>
        !this.#searchProductPicturesContextMenuItem.disabled,

    );
  }

  #openContextMenuOnFirstItemOfRepositoryList(): Step {
    return new Step("Open Context Menu on the first item of the repository list",
      (): boolean =>
        true
      ,
      (): void =>
        this.#openContextMenu(this.#getRepositoryList(), 0),

    );

  }

  #searchProductPicturesUsingContextMenu(): Step {
    return new Step("Search Product Pictures using the context menu",
      (): boolean =>
        true
      ,
      (): void =>
        this.#searchProductPicturesContextMenuItem.baseAction.execute(),

    );

  }

  #openContextMenu(grid: GridPanel, row: number): void {
    const event: Record<string, any> = {
      getXY: (): Array<any> =>
        TableUtil.getCell(grid, row, 1).getXY()
      ,
      preventDefault: (): void => {
        //do nothing
      },
      getTarget: (): HTMLElement =>
        TableUtil.getCellAsDom(grid, row, 1)
      ,
      "type": ContextMenuEventAdapter.EVENT_NAME,
    };
    grid.fireEvent("rowcontextmenu", grid, null, null, row, event);
  }

  #findCatalogRepositoryContextMenu(): CatalogRepositoryContextMenu {
    const contextMenu = as(ComponentManager.getAll().filter((component: Component): boolean =>
      !component.up() && !component.hidden && component.isXType(CatalogRepositoryContextMenu.xtype),
    )[0], CatalogRepositoryContextMenu);
    if (contextMenu) {
      this.#searchProductPicturesContextMenuItem = as(contextMenu.getComponent(LivecontextAssetStudioPlugin.SEARCH_PRODUCT_PICTURES_MENU_ITEM_ID), Item);
    }

    return contextMenu;
  }

}

export default SearchProductImagesTest;
